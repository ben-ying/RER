package com.yjh.rer.network;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.yjh.rer.BuildConfig;
import com.yjh.rer.model.CustomResponse;

import retrofit2.Response;

public class ApiResponse<T> {
    private final int mCode;
    @Nullable
    private final T mBody;
    @Nullable
    private final String mErrorMessage;

    public ApiResponse(Throwable error) {
        mCode = 500;
        mBody = null;
        mErrorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        if (BuildConfig.DEBUG) {
            Log.d("HTTP", "request: " + response.raw().request());
            Log.d("HTTP", "response: " + new Gson().toJson(response.body()));
        }
        CustomResponse responseBody = (CustomResponse) response.body();
        mCode = responseBody.getCode();
        if (responseBody.isSuccessful()) {
            mBody = response.body();
            mErrorMessage = null;
        } else {
            mErrorMessage = responseBody.getMessage();
            mBody = null;
        }
    }

    public boolean isSuccessful() {
        return mCode == CustomResponse.SUCCESS_CODE;
    }

    public int getCode() {
        return mCode;
    }

    @Nullable
    public T getBody() {
        return mBody;
    }

    @Nullable
    public String getErrorMessage() {
        return mErrorMessage;
    }
}
