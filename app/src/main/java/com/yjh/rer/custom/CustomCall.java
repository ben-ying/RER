package com.yjh.rer.custom;


import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

public class CustomCall<T> {
    private final Call<T> call;

    CustomCall(Call<T> call) {
        this.call = call;
    }

    public T get() throws IOException {
        return call.execute().body();
    }

    public Response<T> getResponse() throws IOException {
        return call.execute();
    }
}
