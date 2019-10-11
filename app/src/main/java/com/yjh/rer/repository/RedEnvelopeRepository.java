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
    private static final int MAX_RETRY_COUNT = 5;

    private final Webservice mWebservice;
    private final RedEnvelopeDao mRedEnvelopeDao;
    private final RateLimiter<String> mRepoListRateLimit = new RateLimiter<>(3, TimeUnit.SECONDS);

    private RedEnvelopeCache mCache;

    private static ListResponseResult<List<RedEnvelope>> mResult;

    public RedEnvelopeDao getDao() {
        return mRedEnvelopeDao;
    }

    public ListResponseResult<List<RedEnvelope>> getResult() {
        return mResult;
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
                             mResult = response.body().getResult();
                         } else {
                             retryLoad(webservice, page, size,
                                     retryCount, "Empty Response", callbacks);
                             Log.d(TAG, "Empty Response: " + response.toString());
                         }
                     } else {
                         try {
                             if (response.errorBody() != null) {
                                 retryLoad(webservice, page, size,
                                         retryCount, response.errorBody().string(), callbacks);
                             } else {
                                 retryLoad(webservice, page, size,
                                         retryCount, UNKNOWN_ERROR, callbacks);
                             }
                         }  catch (IOException e) {
                             Log.d(TAG, "onFailure: " + e.toString());
                             retryLoad(webservice, page, size, retryCount, e.toString(), callbacks);
                         }
                     }
                 }

                 @Override
                 public void onFailure(@NonNull Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call,
                                       @NonNull Throwable t) {
                     Log.d(TAG, "onFailure: " + t.getMessage());
                     retryLoad(webservice, page, size, retryCount, t.getMessage(), callbacks);
                 }
             });
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException: " + e.toString());
            error = "IOException: " + e.toString();
            retryLoad(webservice, page, size, retryCount, error, callbacks);
        } catch (Exception e) {
            Log.d(TAG, "Exception: " + e.toString());
            error = "IOException: " + e.toString();
            retryLoad(webservice, page, size, retryCount, error, callbacks);
        }
    }

    private static void retryLoad(final Webservice webservice, final int page, final int size,
                           int retryCount, final String errorMessage,
                           final NetworkState.callback callbacks) {
        if (retryCount < MAX_RETRY_COUNT) {
            loadRedEnvelopesFromNetwork(webservice, page, size, retryCount + 1, callbacks);
        } else {
            callbacks.onError(errorMessage);
        }
    }

    public RedEnvelopeResult loadRedEnvelopes(int type) {
        RedEnvelopeBoundaryCallback boundaryCallback =
                new RedEnvelopeBoundaryCallback(mWebservice, mCache, type);
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

    public LiveData<Resource<RedEnvelope>> addRedEnvelope(final String moneyFrom,
                                                                final String money,
                                                                final String remark,
                                                                final String token,
                                                                final int retry) {
        return new NetworkBoundResource<RedEnvelope, CustomResponse<RedEnvelope>>() {
            int mRedEnvelopeId = -1;

            @Override
            protected void saveCallResult(@NonNull CustomResponse<RedEnvelope> item) {
                RedEnvelope addedItem = item.getResult();
                addedItem.setPage(1);
                mRedEnvelopeDao.save(addedItem);
                mRedEnvelopeId = addedItem.getRedEnvelopeId();
                if (mResult != null && mResult.getResults() != null) {
                    mResult.getResults().add(0, addedItem);
                    mResult.setCount(mResult.getCount() + 1);
                    mResult.setTotal(mResult.getTotal() + addedItem.getMoneyInt());
                }
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
            protected void onFetchFailed() {
                if (retry < MAX_RETRY_COUNT) {
                    addRedEnvelope(moneyFrom, money, remark, token, retry);
                } else {
                    super.onFetchFailed();
                }
            }
        }.getAsLiveData();
    }

    public LiveData<Resource<RedEnvelope>> deleteRedEnvelope(final int reId,
                                                             final String token,
                                                             final int retry) {
        return new NetworkBoundResource<RedEnvelope, CustomResponse<RedEnvelope>>() {
            @Override
            protected void saveCallResult(@NonNull CustomResponse<RedEnvelope> item) {
                RedEnvelope deletedItem = item.getResult();
                mRedEnvelopeDao.delete(item.getResult());
                if (mResult != null && mResult.getResults() != null) {
                    List<RedEnvelope> redEnvelopes = mResult.getResults();
                    for (RedEnvelope redEnvelope : redEnvelopes) {
                        if (redEnvelope.getRedEnvelopeId() == deletedItem.getRedEnvelopeId()) {
                            mResult.getResults().remove(redEnvelope);
                            mResult.setCount(mResult.getCount() - 1);
                            mResult.setTotal(mResult.getTotal() - deletedItem.getMoneyInt());
                            break;
                        }
                    }
                }
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
            protected void onFetchFailed() {
                if (retry < MAX_RETRY_COUNT) {
                    deleteRedEnvelope(reId, token, retry);
                } else {
                    super.onFetchFailed();
                }
            }
        }.getAsLiveData();
    }
}
