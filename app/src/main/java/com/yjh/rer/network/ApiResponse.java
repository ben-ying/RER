package com.yjh.rer.network;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.yjh.rer.BuildConfig;
import com.yjh.rer.model.CustomResponse;

import retrofit2.Response;

public class ApiResponse<T> {
    private  int code;
    @Nullable
    private  T body;
    @Nullable
    private  String errorMessage;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        if (BuildConfig.DEBUG) {
            Log.d("HTTP", "request: " + response.raw().request());
            Log.d("HTTP", "response: " + new Gson().toJson(response.body()));
        }
        CustomResponse responseBody = (CustomResponse) response.body();
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
        return code == CustomResponse.SUCCESS_CODE;
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
