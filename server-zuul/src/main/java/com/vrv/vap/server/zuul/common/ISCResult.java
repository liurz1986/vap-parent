package com.vrv.vap.server.zuul.common;

/**
 * Created by lizj on 2019/8/23.
 */
public class ISCResult<T> {

    private boolean success = false;

    private String message ="";


    private T data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
