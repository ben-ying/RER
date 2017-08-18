package com.yjh.rer.network;

import android.support.annotation.Nullable;

import com.yjh.rer.model.ResponseBody;

import retrofit2.Response;

public class ApiResponse<T> {
    private final int code;
    @Nullable
    private final T body;
    @Nullable
    private final String errorMessage;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        ResponseBody responseBody = (ResponseBody) response.body();
        code = responseBody.getCode();
        if (responseBody.isSuccessful()) {
            body = response.body();
            errorMessage = null;
        } else {
            errorMessage = responseBody.getMessage();
            body = null;
        }
    }

    public boolean isSuccessful() {
        return code == ResponseBody.SUCCESS_CODE;
    }

    public int getCode() {
        return code;
    }

    @Nullable
    public T getBody() {
        return body;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }
}
