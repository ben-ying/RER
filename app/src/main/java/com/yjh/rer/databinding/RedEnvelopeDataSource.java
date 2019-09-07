package com.yjh.rer.databinding;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.paging.ItemKeyedDataSource;
import androidx.paging.PageKeyedDataSource;
import androidx.paging.PagedListAdapter;

import com.github.mikephil.charting.utils.Utils;
import com.google.gson.JsonObject;
import com.yjh.rer.custom.CustomCall;
import com.yjh.rer.model.CustomResponse;
import com.yjh.rer.model.ListResponseResult;
import com.yjh.rer.network.ApiResponse;
import com.yjh.rer.network.NetworkBoundResource;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.RateLimiter;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RedEnvelopeDataSource extends PageKeyedDataSource<Integer, RedEnvelope> {
    private final static String TAG = RedEnvelopeDataSource.class.getSimpleName();

    private int type;
    private RedEnvelopeRepository repository;
    private final Webservice mWebservice;
    private final RedEnvelopeDao mRedEnvelopeDao;
    private final RateLimiter<String> mRepoListRateLimit = new RateLimiter<>(3, TimeUnit.SECONDS);

    RedEnvelopeDataSource(int type, RedEnvelopeRepository redEnvelopeRepository) {
        this.type = type;
        this.repository = redEnvelopeRepository;
        this.mWebservice = redEnvelopeRepository.getWebservice();
        this.mRedEnvelopeDao = redEnvelopeRepository.getRedEnvelopeDao();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, RedEnvelope> callback) {
        Log.d(TAG, "loadInitial: " + params.requestedLoadSize);
//        callback.onResult(this.repository.getRedEnvelopeDao().loadFromDb(
//                params.requestedLoadSize), null, 2);
        CustomCall<CustomResponse<ListResponseResult<RedEnvelope>>> call =
                repository.getWebservice().getRedEnvelopeList(
                "83cd0f7a0483db73ce4223658cb61deac6531e85",
                        "1", 1, params.requestedLoadSize);
        try {
            CustomResponse<ListResponseResult<RedEnvelope>> response = call.get();
            Log.d(TAG, response.getResult().getCount() + "");
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
//        a.enqueue(new Callback<CustomResponse<ListResponseResult<List<RedEnvelope>>>>() {
//            @Override
//            public void onResponse(Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call,
//                                   Response<CustomResponse<ListResponseResult<List<RedEnvelope>>>> response) {
//                callback.onResult(response.body().getResult().getResults(), null, 2);
//            }
//
//            @Override
//            public void onFailure(Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call, Throwable t) {
//                Log.d(TAG, "response: " + t.getMessage());
//            }
//        });
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, RedEnvelope> callback) {
        Log.d(TAG, "loadAfter: " + params.key + ", " + params.requestedLoadSize);
//        Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> a = repository.getWebservice().getRedEnvelopeList(
//                "83cd0f7a0483db73ce4223658cb61deac6531e85", "1", params.key, params.requestedLoadSize);
//        a.enqueue(new Callback<CustomResponse<ListResponseResult<List<RedEnvelope>>>>() {
//            @Override
//            public void onResponse(Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call,
//                                   Response<CustomResponse<ListResponseResult<List<RedEnvelope>>>> response) {
////                if (response.code() == 200) {
////                    if (response.body().getResult().getNext() != null) {
////                        callback.onResult(response.body().getResult().getResults(), params.key + 1);
////                    } else {
////                        callback.onResult(response.body().getResult().getResults(), null);
////                    }
////                }
//            }

//            @Override
//            public void onFailure(Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call, Throwable t) {
//                Log.d(TAG, "response: " + t.getMessage());
//            }
//        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, RedEnvelope> callback) {
//        Log.d(TAG, "loadBefore: " + params.key + ", " + params.requestedLoadSize);
//        Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> a = repository.getWebservice().getRedEnvelopeList(
//                "83cd0f7a0483db73ce4223658cb61deac6531e85", "1", params.key, params.requestedLoadSize);
//        a.enqueue(new Callback<CustomResponse<ListResponseResult<List<RedEnvelope>>>>() {
//            @Override
//            public void onResponse(Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call,
//                                   Response<CustomResponse<ListResponseResult<List<RedEnvelope>>>> response) {
//                if (params.key > 1) {
//                    callback.onResult(response.body().getResult().getResults(), params.key - 1);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<CustomResponse<ListResponseResult<List<RedEnvelope>>>> call, Throwable t) {
//                Log.d(TAG, "response: " + t.getMessage());
//            }
//        });
    }
}
