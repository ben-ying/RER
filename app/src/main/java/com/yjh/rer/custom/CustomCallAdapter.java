package com.yjh.rer.custom;


import androidx.annotation.NonNull;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;

public class CustomCallAdapter<T> implements CallAdapter<T, CustomCall<T>> {
    private final Type responseType;

    CustomCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NonNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @NonNull
    @Override
    public CustomCall<T> adapt(@NonNull Call<T> call) {
        return new CustomCall<>(call);
    }
}
