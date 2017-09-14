package com.yjh.rer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yjh.rer.model.CustomResponse;
import com.yjh.rer.model.ListResponseResult;
import com.yjh.rer.network.ApiResponse;
import com.yjh.rer.network.NetworkBoundResource;
import com.yjh.rer.network.Resource;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.RateLimiter;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RedEnvelopeRepository {
    private final Webservice mWebservice;
    private final RedEnvelopeDao mRedEnvelopeDao;
    private final RateLimiter<String> mRepoListRateLimit = new RateLimiter<>(3, TimeUnit.SECONDS);

    @Inject
    public RedEnvelopeRepository(Webservice webservice, RedEnvelopeDao redEnvelopeDao) {
        this.mWebservice = webservice;
        this.mRedEnvelopeDao = redEnvelopeDao;
    }

    public LiveData<Resource<List<RedEnvelope>>> loadRedEnvelopes(
            final String token, final String userId) {
        return new NetworkBoundResource<List<RedEnvelope>,
                CustomResponse<ListResponseResult<List<RedEnvelope>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<RedEnvelope>>> item) {
                mRedEnvelopeDao.deleteAll();
                mRedEnvelopeDao.saveAll(item.getResult().getResults());
                Log.d("", "");
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RedEnvelope> data) {
                return data == null || data.isEmpty() || mRepoListRateLimit.shouldFetch(token);
            }

            @NonNull
            @Override
            protected LiveData<List<RedEnvelope>> loadFromDb() {
                return mRedEnvelopeDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<
                    CustomResponse<ListResponseResult<List<RedEnvelope>>>>> createCall() {
                return mWebservice.getRedEnvelopes(token, userId);
            }

            @Override
            protected CustomResponse<ListResponseResult<List<RedEnvelope>>> processResponse(
                    ApiResponse<CustomResponse<ListResponseResult<List<RedEnvelope>>>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<RedEnvelope>>> addRedEnvelope(final String moneyFrom,
                                                          final String money,
                                                          final String remark,
                                                          final String token) {
        return new NetworkBoundResource<List<RedEnvelope>, CustomResponse<RedEnvelope>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<RedEnvelope> item) {
                mRedEnvelopeDao.save(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RedEnvelope> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<RedEnvelope>> loadFromDb() {
                return mRedEnvelopeDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CustomResponse<RedEnvelope>>> createCall() {
                return mWebservice.addRedEnvelope(moneyFrom, money, remark, token);
            }

            @Override
            protected CustomResponse<RedEnvelope> processResponse(
                    ApiResponse<CustomResponse<RedEnvelope>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<RedEnvelope>>> deleteRedEnvelope(final int reId, final String token) {
        return new NetworkBoundResource<List<RedEnvelope>, CustomResponse<RedEnvelope>>() {

            @Override
            protected void saveCallResult(@NonNull CustomResponse<RedEnvelope> item) {
                mRedEnvelopeDao.delete(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RedEnvelope> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<RedEnvelope>> loadFromDb() {
                return mRedEnvelopeDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<CustomResponse<RedEnvelope>>> createCall() {
                return mWebservice.deleteRedEnvelope(reId, token);
            }

            @Override
            protected CustomResponse<RedEnvelope> processResponse(
                    ApiResponse<CustomResponse<RedEnvelope>> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }
}
