package com.javalong.retrofit_rxjava.bean;

public class MockBean {
    private String test;

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    @Override
    public String toString() {
        return "MockBean{" +
                "test='" + test + '\'' +
                '}';
    }
}
