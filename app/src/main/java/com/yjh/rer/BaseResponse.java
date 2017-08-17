package com.yjh.rer;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class BaseResponse {
    public static final int SUCCESS_CODE = 200;

    @SerializedName("message")
    private String message;
    @SerializedName("code")
    private int code;
    @SerializedName("result")
    private JsonObject result;

    public boolean isSuccessful() {
        return code == SUCCESS_CODE;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public JsonObject getResult() {
        return result;
    }

    public void setResult(JsonObject result) {
        this.result = result;
    }
}
