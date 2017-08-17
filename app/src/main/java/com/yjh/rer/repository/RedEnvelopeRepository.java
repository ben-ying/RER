package com.yjh.rer.repository;


import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.yjh.rer.dao.RedEnvelopeDao;
import com.yjh.rer.entity.RedEnvelope;
import com.yjh.rer.webservice.Webservice;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class RedEnvelopeRepository {
    private static final String BASE_URL = "http://bensbabycare.com/webservice/";
    private final Webservice webservice;
    private final RedEnvelopeDao redEnvelopeDao;

    @Inject
    public RedEnvelopeRepository(RedEnvelopeDao redEnvelopeDao) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        this.webservice = retrofit.create(Webservice.class);
        this.redEnvelopeDao = redEnvelopeDao;
    }

    public LiveData<List<RedEnvelope>> getRedEnvelopes(String token, String userId) {
        refreshRedEnvelopes(token, userId);
        return redEnvelopeDao.loadRedEnvelopes(userId);
    }

    private void refreshRedEnvelopes(final String token, final String userId) {

        Call<List<RedEnvelope>> call = webservice.getRedEnvelopes(token, userId);
        call.enqueue(new Callback<List<RedEnvelope>>() {
            @Override
            public void onResponse(Call<List<RedEnvelope>> call, Response<List<RedEnvelope>> response) {
                Log.d("", "");
            }

            @Override
            public void onFailure(Call<List<RedEnvelope>> call, Throwable t) {
                Log.d("", "");
            }
        });
    }
}
