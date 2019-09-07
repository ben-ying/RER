package com.yjh.rer.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.yjh.rer.custom.CustomCall;
import com.yjh.rer.model.CustomResponse;
import com.yjh.rer.model.ListResponseResult;
import com.yjh.rer.network.ApiResponse;
import com.yjh.rer.network.NetworkBoundResource;
import com.yjh.rer.network.Resource;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.RateLimiter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RedEnvelopeRepository {
    private final static String TAG = RedEnvelopeRepository.class.getSimpleName();

    private final Webservice mWebservice;
    private final RedEnvelopeDao mRedEnvelopeDao;
    private final RateLimiter<String> mRepoListRateLimit = new RateLimiter<>(3, TimeUnit.SECONDS);

    public RedEnvelopeDao getDao() {
        return mRedEnvelopeDao;
    }

    @Inject
    RedEnvelopeRepository(Webservice webservice, RedEnvelopeDao redEnvelopeDao) {
        this.mWebservice = webservice;
        this.mRedEnvelopeDao = redEnvelopeDao;
    }

    public List<RedEnvelope> getRedEnvelopeList(int page, int size) {
        return getRedEnvelopeList(page, size, 0);
    }

    private List<RedEnvelope> getRedEnvelopeList(int page, int size, int retryCount) {
        Log.d(TAG, "page: " + page + ", size: " + size + ", retryCount: " + retryCount);

        CustomCall<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call =
                mWebservice.getRedEnvelopeList(
                        "83cd0f7a0483db73ce4223658cb61deac6531e85", "1", page, size);
        try {
            CustomResponse<ListResponseResult<List<RedEnvelope>>> response = call.get();
            if (page == 1) {
                mRedEnvelopeDao.deleteAll();
            }
            mRedEnvelopeDao.saveAll(response.getResult().getResults());
            return response.getResult().getResults();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IOException: " + e.getLocalizedMessage());
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Log.d(TAG, "IllegalStateException: " + e.getLocalizedMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Exception: " + e.getLocalizedMessage());
        }

        if (retryCount < 3) {
            return getRedEnvelopeList(page, size, retryCount + 1);
        }

        return null;
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
                return mWebservice.getRedEnvelopes(token, userId, 100);
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
