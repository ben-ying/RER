package com.yjh.rer.custom;


import android.util.Log;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class CustomCall<T> {
    private static final String TAG = CustomCall.class.getSimpleName();

    private final Call<T> call;

    CustomCall(Call<T> call) {
        this.call = call;
    }

    public T get() throws IOException {
        Response<T> response = call.execute();
        Log.d(TAG, "request: " + response.raw().request().url());
        Log.d(TAG, "response: " + response);
        return response.body();
    }

    public Response<T> getResponse() throws IOException {
        return call.execute();
    }
}
