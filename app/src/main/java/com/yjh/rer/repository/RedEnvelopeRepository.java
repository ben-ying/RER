package com.yjh.rer.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.yjh.rer.databinding.RedEnvelopeBoundaryCallback;
import com.yjh.rer.model.CustomResponse;
import com.yjh.rer.model.ListResponseResult;
import com.yjh.rer.model.RedEnvelopeResult;
import com.yjh.rer.network.ApiResponse;
import com.yjh.rer.network.NetworkBoundResource;
import com.yjh.rer.network.NetworkState;
import com.yjh.rer.network.Resource;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeCache;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.RateLimiter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class RedEnvelopeRepository {
    private final static String TAG = RedEnvelopeRepository.class.getSimpleName();
    private final static String UNKNOWN_ERROR = "Unknown Error";
    private static final int DATABASE_PAGE_SIZE = 5;

    private final Webservice mWebservice;
    private final RedEnvelopeDao mRedEnvelopeDao;
    private final RateLimiter<String> mRepoListRateLimit = new RateLimiter<>(3, TimeUnit.SECONDS);

    private RedEnvelopeCache mCache;

    public RedEnvelopeDao getDao() {
        return mRedEnvelopeDao;
    }

    @Inject
    RedEnvelopeRepository(Webservice webservice,
                          RedEnvelopeDao redEnvelopeDao,
                          RedEnvelopeCache cache) {
        this.mWebservice = webservice;
        this.mRedEnvelopeDao = redEnvelopeDao;
        this.mCache = cache;
    }

    public static void loadRedEnvelopesFromNetwork(Webservice webservice, int page, int size,
                                                   final NetworkState.callback callbacks) {
        loadRedEnvelopesFromNetwork(webservice, page, size, 0, callbacks);
    }

    private static void loadRedEnvelopesFromNetwork(Webservice webservice, int page, int size,
                                        int retryCount, final NetworkState.callback callbacks) {
        Log.d(TAG, "page: " + page + ", size: " + size + ", retryCount: " + retryCount);
        String error = UNKNOWN_ERROR;

        try {
            Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call =
                    webservice.requestRedEnvelopes("83cd0f7a0483db73ce4223658cb61deac6531e85", "1", page, size);
            call.enqueue(new Callback<CustomResponse<ListResponseResult<List<RedEnvelope>>>>() {
                 @Override
                 public void onResponse(@NonNull Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call,
                                        @NonNull Response<CustomResponse<ListResponseResult<List<RedEnvelope>>>> response) {
                     Log.d(TAG, "onResponse url: " + call.request().url());
                     if (response.isSuccessful()) {
                         List<RedEnvelope> redEnvelopes;
                         if (response.body() != null && response.body().getResult() != null) {
                             redEnvelopes = response.body().getResult().getResults();
                             callbacks.onSuccess(redEnvelopes,
                                     response.body().getResult().getNext() == null);
                         } else {
                             callbacks.onError("Empty Response");
                             Log.d(TAG, "Empty Response: " + response.toString());
                         }
                     } else {
                         try {
                             if (response.errorBody() != null) {
                                 callbacks.onError(response.errorBody().string());
                             } else {
                                 callbacks.onError(UNKNOWN_ERROR);
                             }
                         }  catch (IOException e) {
                             e.printStackTrace();
                             Log.d(TAG, "onFailure: " + e.toString());
                             callbacks.onError(e.toString());
                         }
                     }
                 }

                 @Override
                 public void onFailure(@NonNull Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call,
                                       @NonNull Throwable t) {
                     Log.d(TAG, "onFailure: " + t.getMessage());
                     if (retryCount < 3) {
                         loadRedEnvelopesFromNetwork(webservice, page, size, retryCount + 1, callbacks);
                     } else {
                         callbacks.onError(t.getMessage());
                     }

                 }
             });
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException: " + e.toString());
            callbacks.onError(e.toString());
            error = "IOException: " + e.toString();
            callbacks.onError(error);
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.toString());
            callbacks.onError(e.toString());
            error = "IOException: " + e.toString();
            callbacks.onError(error);
        }
    }

    public RedEnvelopeResult loadRedEnvelopes(String query) {
        RedEnvelopeBoundaryCallback boundaryCallback =
                new RedEnvelopeBoundaryCallback(mWebservice, mCache);
        LiveData<String> networkErrors = boundaryCallback.getNetworkErrors();
        DataSource.Factory<Integer, RedEnvelope> dataSourceFactory = mCache.getList();

        PagedList.Config config = new PagedList.Config.Builder()
                .setPrefetchDistance(DATABASE_PAGE_SIZE * 3)
                .setInitialLoadSizeHint(DATABASE_PAGE_SIZE * 3)
                .setPageSize(DATABASE_PAGE_SIZE)
                .build();


        LiveData<PagedList<RedEnvelope>> data =
                new LivePagedListBuilder<>(dataSourceFactory, config)
                        .setBoundaryCallback(boundaryCallback)
                        .build();

        return new RedEnvelopeResult(data, networkErrors);
    }

    public LiveData<Resource<List<RedEnvelope>>> loadRedEnvelopes(
            final String token, final String userId, final int pageSize) {
        return new NetworkBoundResource<List<RedEnvelope>,
                CustomResponse<ListResponseResult<List<RedEnvelope>>>>() {
            @Override
            protected void saveCallResult(
                    @NonNull CustomResponse<ListResponseResult<List<RedEnvelope>>> item) {
                mRedEnvelopeDao.deleteAll();
                mRedEnvelopeDao.insert(item.getResult().getResults());
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
                return mWebservice.getRedEnvelopes(token, userId, pageSize);
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

    public LiveData<Resource<RedEnvelope>> addRedEnvelope(final String moneyFrom,
                                                                final String money,
                                                                final String remark,
                                                                final String token) {
        return new NetworkBoundResource<RedEnvelope, CustomResponse<RedEnvelope>>() {
            int mRedEnvelopeId = -1;

            @Override
            protected void saveCallResult(@NonNull CustomResponse<RedEnvelope> item) {
                mRedEnvelopeDao.save(item.getResult());
                mRedEnvelopeId = item.getResult().getRedEnvelopeId();
            }

            @Override
            protected boolean shouldFetch(@Nullable RedEnvelope data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<RedEnvelope> loadFromDb() {
                return mRedEnvelopeDao.loadById(mRedEnvelopeId);
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

    public LiveData<Resource<RedEnvelope>> deleteRedEnvelope(final int reId, final String token) {
        return new NetworkBoundResource<RedEnvelope, CustomResponse<RedEnvelope>>() {
            @Override
            protected void saveCallResult(@NonNull CustomResponse<RedEnvelope> item) {
                mRedEnvelopeDao.delete(item.getResult());
            }

            @Override
            protected boolean shouldFetch(@Nullable RedEnvelope data) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<RedEnvelope> loadFromDb() {
                return mRedEnvelopeDao.loadById(reId);
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
