package com.javalong.rr.lib;

public class HttpError extends RuntimeException {

    private ResponseMessageBean response;
    private String message;

    public ResponseMessageBean getResponse() {
        return response;
    }

    public void setResponse(ResponseMessageBean response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpError(ResponseMessageBean response, String message) {
        super(message);
        this.response = response;
        this.message = message;
    }

    public HttpError() {
    }
}
