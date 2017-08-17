package com.yjh.rer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yjh.rer.BaseResponse;
import com.yjh.rer.network.ApiResponse;
import com.yjh.rer.network.NetworkBoundResource;
import com.yjh.rer.network.Resource;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.LiveDataCallAdapterFactory;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

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
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build();
        this.webservice = retrofit.create(Webservice.class);
        this.redEnvelopeDao = redEnvelopeDao;
    }

    public LiveData<Resource<List<RedEnvelope>>> loadRedEnvelopes(
            final String token, final String userId) {
        return new NetworkBoundResource<List<RedEnvelope>, BaseResponse>() {
            @Override
            protected void saveCallResult(@NonNull BaseResponse item) {
                Log.d("", "");
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RedEnvelope> data) {
                Log.d("", "");
                return data == null;
//                return rateLimiter.canFetch(userId) && (data == null || !isFresh(data));
            }

            @NonNull
            @Override
            protected LiveData<List<RedEnvelope>> loadFromDb() {
                return redEnvelopeDao.loadRedEnvelopes(userId);
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<BaseResponse>> createCall() {
                return webservice.getRedEnvelopes(token, userId);
            }
        }.getAsLiveData();
    }
}
