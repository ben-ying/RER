package com.yjh.rer.repository;


import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yjh.rer.model.ResponseBody;
import com.yjh.rer.network.ApiResponse;
import com.yjh.rer.network.NetworkBoundResource;
import com.yjh.rer.network.Resource;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.db.MyDatabase;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.GsonUtils;
import com.yjh.rer.util.RateLimiter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RedEnvelopeRepository {
    private Webservice webservice;
    private RedEnvelopeDao redEnvelopeDao;
    private RateLimiter<String> repoListRateLimit = new RateLimiter<>(1, TimeUnit.SECONDS);

    @Inject
    public RedEnvelopeRepository(Webservice webservice, MyDatabase database) {
        this.webservice = webservice;
        this.redEnvelopeDao = database.redEnvelopeDao();
    }

    public LiveData<Resource<List<RedEnvelope>>> loadRedEnvelopes(
            final String token, final String userId) {
        return new NetworkBoundResource<List<RedEnvelope>, ResponseBody>() {
            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                try {
                    RedEnvelope[] redEnvelopes = GsonUtils.getJsonData(
                            new JSONObject(item.getResult().toString())
                                    .getString("results"), RedEnvelope[].class);
                    redEnvelopeDao.saveAll(redEnvelopes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RedEnvelope> data) {
                Log.d("", "");
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(token);
            }

            @NonNull
            @Override
            protected LiveData<List<RedEnvelope>> loadFromDb() {
                return redEnvelopeDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ResponseBody>> createCall() {
                return webservice.getRedEnvelopes(token, userId);
            }

            @Override
            protected ResponseBody processResponse(ApiResponse<ResponseBody> response) {
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
        return new NetworkBoundResource<List<RedEnvelope>, ResponseBody>() {

            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
//                redEnvelopeDao.save(item);
                Log.d("", "");
                try {
                    RedEnvelope redEnvelope = GsonUtils.getJsonData(
                            new JSONObject(item.getResult().toString()), RedEnvelope.class);
                    redEnvelopeDao.save(redEnvelope);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RedEnvelope> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<RedEnvelope>> loadFromDb() {
                return redEnvelopeDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ResponseBody>> createCall() {
                return webservice.addRedEnvelope(moneyFrom, money, remark, token);
            }

            @Override
            protected ResponseBody processResponse(ApiResponse<ResponseBody> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<List<RedEnvelope>>> deleteRedEnvelope(final int reId, final String token) {
        return new NetworkBoundResource<List<RedEnvelope>, ResponseBody>() {

            @Override
            protected void saveCallResult(@NonNull ResponseBody item) {
                try {
                    RedEnvelope redEnvelope = GsonUtils.getJsonData(
                            new JSONObject(item.getResult().toString()), RedEnvelope.class);
                    redEnvelopeDao.delete(redEnvelope);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<RedEnvelope> data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<List<RedEnvelope>> loadFromDb() {
                return redEnvelopeDao.loadAll();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<ResponseBody>> createCall() {
                return webservice.deleteRedEnvelope(reId, token);
            }

            @Override
            protected ResponseBody processResponse(ApiResponse<ResponseBody> response) {
                return response.getBody();
            }

            @Override
            protected void onFetchFailed() {
                super.onFetchFailed();
            }
        }.getAsLiveData();
    }
}
