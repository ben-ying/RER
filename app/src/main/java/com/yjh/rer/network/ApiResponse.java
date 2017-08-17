package com.yjh.rer.network;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yjh.rer.BaseResponse;
import com.yjh.rer.util.GsonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Response;

public class ApiResponse<T> {
    private final int code;
    @Nullable
    private final T body;
    @Nullable
    private final String errorMessage;
    @Nullable
    private String nextPageUrl;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
    }

    public ApiResponse(Response<T> response) {
        BaseResponse baseResponse = GsonUtils.getJsonData(
                new Gson().toJson(response), BaseResponse.class);
        if (baseResponse != null) {
            code = baseResponse.getCode();
            if (baseResponse.isSuccessful()) {
                body = response.body();
                errorMessage = null;
            } else {
                errorMessage = baseResponse.getMessage();
                body = null;
            }
        } else {
            code = response.code();
            if (code == BaseResponse.SUCCESS_CODE) {
                body = response.body();
                errorMessage = null;
            } else {
                errorMessage = response.message();
                body = null;
            }
        }

        getNextPageUrl();
    }

    public boolean isSuccessful() {
        return code == 200;
    }

    public String getNextPageUrl() {
        try {
            if (body != null) {
                nextPageUrl = new JSONObject(body.toString()).getString("next");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return nextPageUrl;
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

    public void setNextPageUrl(@Nullable String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }
}
