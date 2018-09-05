/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.javalong.rr.api;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.Result;

/**
 * A {@linkplain CallAdapter.Factory call adapter} which uses RxJava 2 for creating observables.
 * <p>
 * Adding this class to {@link Retrofit} allows you to return an {@link Observable},
 * {@link Flowable}, {@link Single}, {@link Completable} or {@link Maybe} from service methods.
 * <pre><code>
 * interface MyService {
 *   &#64;GET("user/me")
 *   Observable&lt;User&gt; getUser()
 * }
 * </code></pre>
 * There are three configurations supported for the {@code Observable}, {@code Flowable},
 * {@code Single}, {@link Completable} and {@code Maybe} type parameter:
 * <ul>
 * <li>Direct body (e.g., {@code Observable<User>}) calls {@code onNext} with the deserialized body
 * for 2XX responses and calls {@code onError} with {@link HttpException} for non-2XX responses and
 * {@link IOException} for network errors.</li>
 * <li>Response wrapped body (e.g., {@code Observable<Response<User>>}) calls {@code onNext}
 * with a {@link Response} object for all HTTP responses and calls {@code onError} with
 * {@link IOException} for network errors</li>
 * <li>Result wrapped body (e.g., {@code Observable<Result<User>>}) calls {@code onNext} with a
 * {@link Result} object for all HTTP responses and errors.</li>
 * </ul>
 */
public final class CustomRxJava2CallAdapterFactory extends CallAdapter.Factory {
  /**
   * Returns an instance which creates synchronous observables that do not operate on any scheduler
   * by default.
   */
  public static CustomRxJava2CallAdapterFactory create() {
    return new CustomRxJava2CallAdapterFactory(null, false);
  }

  /**
   * Returns an instance which creates asynchronous observables. Applying
   * {@link Observable#subscribeOn} has no effect on stream types created by this factory.
   */
  public static CustomRxJava2CallAdapterFactory createAsync() {
    return new CustomRxJava2CallAdapterFactory(null, true);
  }

  /**
   * Returns an instance which creates synchronous observables that
   * {@linkplain Observable#subscribeOn(Scheduler) subscribe on} {@code scheduler} by default.
   */
  @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
  public static CustomRxJava2CallAdapterFactory createWithScheduler(Scheduler scheduler) {
    if (scheduler == null) throw new NullPointerException("scheduler == null");
    return new CustomRxJava2CallAdapterFactory(scheduler, false);
  }

  private final  Scheduler scheduler;
  private final boolean isAsync;

  private CustomRxJava2CallAdapterFactory( Scheduler scheduler, boolean isAsync) {
    this.scheduler = scheduler;
    this.isAsync = isAsync;
  }

  @Override
  public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
    Class<?> rawType = getRawType(returnType);

    if (rawType == Completable.class) {
      // Completable is not parameterized (which is what the rest of this method deals with) so it
      // can only be created with a single configuration.
      return new RxJava2CallAdapter(Void.class, scheduler, isAsync, false, true, false, false,
          false, true);
    }

    boolean isFlowable = rawType == Flowable.class;
    boolean isSingle = rawType == Single.class;
    boolean isMaybe = rawType == Maybe.class;
    if (rawType != Observable.class && !isFlowable && !isSingle && !isMaybe) {
      return null;
    }

    boolean isResult = false;
    boolean isBody = false;
    Type responseType;
    if (!(returnType instanceof ParameterizedType)) {
      String name = isFlowable ? "Flowable"
          : isSingle ? "Single"
          : isMaybe ? "Maybe" : "Observable";
      throw new IllegalStateException(name + " return type must be parameterized"
          + " as " + name + "<Foo> or " + name + "<? extends Foo>");
    }

    Type observableType = getParameterUpperBound(0, (ParameterizedType) returnType);
    Class<?> rawObservableType = getRawType(observableType);
    if (rawObservableType == Response.class) {
      if (!(observableType instanceof ParameterizedType)) {
        throw new IllegalStateException("Response must be parameterized"
            + " as Response<Foo> or Response<? extends Foo>");
      }
      responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
    } else if (rawObservableType == Result.class) {
      if (!(observableType instanceof ParameterizedType)) {
        throw new IllegalStateException("Result must be parameterized"
            + " as Result<Foo> or Result<? extends Foo>");
      }
      responseType = getParameterUpperBound(0, (ParameterizedType) observableType);
      isResult = true;
    } else {
      responseType = observableType;
      isBody = true;
    }

    return new RxJava2CallAdapter(responseType, scheduler, isAsync, isResult, isBody, isFlowable,
        isSingle, isMaybe, false);
  }


  final class RxJava2CallAdapter<R> implements CallAdapter<R, Object> {
    private final Type responseType;
    private final  Scheduler scheduler;
    private final boolean isAsync;
    private final boolean isResult;
    private final boolean isBody;
    private final boolean isFlowable;
    private final boolean isSingle;
    private final boolean isMaybe;
    private final boolean isCompletable;

    RxJava2CallAdapter(Type responseType,  Scheduler scheduler, boolean isAsync,
                       boolean isResult, boolean isBody, boolean isFlowable, boolean isSingle, boolean isMaybe,
                       boolean isCompletable) {
      this.responseType = responseType;
      this.scheduler = scheduler;
      this.isAsync = isAsync;
      this.isResult = isResult;
      this.isBody = isBody;
      this.isFlowable = isFlowable;
      this.isSingle = isSingle;
      this.isMaybe = isMaybe;
      this.isCompletable = isCompletable;
    }

    @Override public Type responseType() {
      return responseType;
    }

    @Override public Object adapt(Call<R> call) {
      Observable<Response<R>> responseObservable = isAsync
              ? new CallEnqueueObservable<>(call)
              : new CallExecuteObservable<>(call);

      Observable<?> observable;
      if (isResult) {
        observable = new ResultObservable<>(responseObservable);
      } else if (isBody) {
        observable = new BodyObservable<>(responseObservable);
      } else {
        observable = responseObservable;
      }

      if (scheduler != null) {
        observable = observable.subscribeOn(scheduler);
      }

      if (isFlowable) {
        return observable.toFlowable(BackpressureStrategy.LATEST);
      }
      if (isSingle) {
        return observable.singleOrError();
      }
      if (isMaybe) {
        return observable.singleElement();
      }
      if (isCompletable) {
        return observable.ignoreElements();
      }
      return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
  }
}

final class ResultObservable<T> extends Observable<Result<T>> {
  private final Observable<Response<T>> upstream;

  ResultObservable(Observable<Response<T>> upstream) {
    this.upstream = upstream;
  }

  @Override protected void subscribeActual(Observer<? super Result<T>> observer) {
    upstream.subscribe(new ResultObservable.ResultObserver<T>(observer));
  }

  private static class ResultObserver<R> implements Observer<Response<R>> {
    private final Observer<? super Result<R>> observer;

    ResultObserver(Observer<? super Result<R>> observer) {
      this.observer = observer;
    }

    @Override public void onSubscribe(Disposable disposable) {
      observer.onSubscribe(disposable);
    }

    @Override public void onNext(Response<R> response) {
      observer.onNext(Result.response(response));
    }

    @Override public void onError(Throwable throwable) {
      try {
        observer.onNext(Result.<R>error(throwable));
      } catch (Throwable t) {
        try {
          observer.onError(t);
        } catch (Throwable inner) {
          Exceptions.throwIfFatal(inner);
          RxJavaPlugins.onError(new CompositeException(t, inner));
        }
        return;
      }
      observer.onComplete();
    }

    @Override public void onComplete() {
      observer.onComplete();
    }
  }
}

final class BodyObservable<T> extends Observable<T> {
  private final Observable<Response<T>> upstream;

  BodyObservable(Observable<Response<T>> upstream) {
    this.upstream = upstream;
  }

  @Override protected void subscribeActual(Observer<? super T> observer) {
    upstream.subscribe(new BodyObservable.BodyObserver<T>(observer));
  }

  private static class BodyObserver<R> implements Observer<Response<R>> {
    private final Observer<? super R> observer;
    private boolean terminated;

    BodyObserver(Observer<? super R> observer) {
      this.observer = observer;
    }

    @Override public void onSubscribe(Disposable disposable) {
      observer.onSubscribe(disposable);
    }

    @Override public void onNext(Response<R> response) {
      if (response.isSuccessful()) {
        observer.onNext(response.body());
      } else {
        terminated = true;
        Throwable t = new retrofit2.adapter.rxjava2.HttpException(response);
        try {
          observer.onError(t);
        } catch (Throwable inner) {
          Exceptions.throwIfFatal(inner);
          RxJavaPlugins.onError(new CompositeException(t, inner));
        }
      }
    }

    @Override public void onComplete() {
      if (!terminated) {
        observer.onComplete();
      }
    }

    @Override public void onError(Throwable throwable) {
      if (!terminated) {
        observer.onError(throwable);
      } else {
        // This should never happen! onNext handles and forwards errors automatically.
        Throwable broken = new AssertionError(
                "This should never happen! Report as a bug with the full stacktrace.");
        //noinspection UnnecessaryInitCause Two-arg AssertionError constructor is 1.7+ only.
        broken.initCause(throwable);
        RxJavaPlugins.onError(broken);
      }
    }
  }
}

final class CallEnqueueObservable<T> extends Observable<Response<T>> {
  private final Call<T> originalCall;

  CallEnqueueObservable(Call<T> originalCall) {
    this.originalCall = originalCall;
  }

  @Override protected void subscribeActual(Observer<? super Response<T>> observer) {
    // Since Call is a one-shot type, clone it for each new observer.
    Call<T> call = originalCall.clone();
    CallEnqueueObservable.CallCallback<T> callback = new CallEnqueueObservable.CallCallback<>(call, observer);
    observer.onSubscribe(callback);
    call.enqueue(callback);
  }

  private static final class CallCallback<T> implements Disposable, Callback<T> {
    private final Call<?> call;
    private final Observer<? super Response<T>> observer;
    private volatile boolean disposed;
    boolean terminated = false;

    CallCallback(Call<?> call, Observer<? super Response<T>> observer) {
      this.call = call;
      this.observer = observer;
    }

    @Override public void onResponse(Call<T> call, Response<T> response) {
      if (disposed) return;

      try {
        observer.onNext(response);

        if (!disposed) {
          terminated = true;
          observer.onComplete();
        }
      } catch (Throwable t) {
        if (terminated) {
          RxJavaPlugins.onError(t);
        } else if (!disposed) {
          try {
            observer.onError(t);
          } catch (Throwable inner) {
            Exceptions.throwIfFatal(inner);
            RxJavaPlugins.onError(new CompositeException(t, inner));
          }
        }
      }
    }

    @Override public void onFailure(Call<T> call, Throwable t) {
      if (call.isCanceled()) return;

      try {
        observer.onError(t);
      } catch (Throwable inner) {
        Exceptions.throwIfFatal(inner);
        RxJavaPlugins.onError(new CompositeException(t, inner));
      }
    }

    @Override public void dispose() {
      disposed = true;
      call.cancel();
    }

    @Override public boolean isDisposed() {
      return disposed;
    }
  }
}

final class CallExecuteObservable<T> extends Observable<Response<T>> {
  private final Call<T> originalCall;

  CallExecuteObservable(Call<T> originalCall) {
    this.originalCall = originalCall;
  }

  @Override protected void subscribeActual(Observer<? super Response<T>> observer) {
    // Since Call is a one-shot type, clone it for each new observer.
    Call<T> call = originalCall.clone();
    CallExecuteObservable.CallDisposable disposable = new CallExecuteObservable.CallDisposable(call);
    observer.onSubscribe(disposable);

    boolean terminated = false;
    try {
      Response<T> response = call.execute();
      if (!disposable.isDisposed()) {
        observer.onNext(response);
      }
      if (!disposable.isDisposed()) {
        terminated = true;
        observer.onComplete();
      }
    } catch (Throwable t) {
      Exceptions.throwIfFatal(t);
      if (terminated) {
        RxJavaPlugins.onError(t);
      } else if (!disposable.isDisposed()) {
        try {
          observer.onError(t);
        } catch (Throwable inner) {
          Exceptions.throwIfFatal(inner);
          RxJavaPlugins.onError(new CompositeException(t, inner));
        }
      }
    }
  }

  private static final class CallDisposable implements Disposable {
    private final Call<?> call;
    private volatile boolean disposed;

    CallDisposable(Call<?> call) {
      this.call = call;
    }

    @Override public void dispose() {
      disposed = true;
      call.cancel();
    }

    @Override public boolean isDisposed() {
      return disposed;
    }
  }
}




