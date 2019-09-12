package com.yjh.rer.databinding;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;


public class RedEnvelopeDataSource extends PageKeyedDataSource<Integer, RedEnvelope> {
    private final static String TAG = RedEnvelopeDataSource.class.getSimpleName();

    private int mType;
    private RedEnvelopeRepository mRepository;

    RedEnvelopeDataSource(int type, RedEnvelopeRepository redEnvelopeRepository) {
        this.mType = type;
        this.mRepository = redEnvelopeRepository;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, RedEnvelope> callback) {
//        callback.onResult(mRepository.getDao().loadFromDb(
//                params.requestedLoadSize), null, 2);
//        List<RedEnvelope> redEnvelopes = getRedEnvelopes(1, params.requestedLoadSize);
//        Log.d(TAG, "loadInitial: " + params.requestedLoadSize);
//
//        if (redEnvelopes != null) {
//            Log.d(TAG, ", " + redEnvelopes.size());
//            if (redEnvelopes.size() == params.requestedLoadSize) {
//                callback.onResult(redEnvelopes, null, 2);
//            } else {
//                callback.onResult(redEnvelopes, null, null);
//            }
//        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, RedEnvelope> callback) {
//        List<RedEnvelope> redEnvelopes = getRedEnvelopes(params.key, params.requestedLoadSize);
//        Log.d(TAG, "loadAfter: " + params.key + ", " + params.requestedLoadSize);
//
//        if (redEnvelopes != null) {
//            Log.d(TAG, ", " + redEnvelopes.size());
//            if (redEnvelopes.size() == params.requestedLoadSize) {
//                callback.onResult(redEnvelopes, params.key + 1);
//            } else {
//                callback.onResult(redEnvelopes, null);
//            }
//        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, RedEnvelope> callback) {
//        Log.d(TAG, "loadBefore: " + params.key + ", " + params.requestedLoadSize);
//        List<RedEnvelope> redEnvelopes = getRedEnvelopes(params.key, params.requestedLoadSize);
//
//        if (redEnvelopes != null) {
//            Log.d(TAG, ", " + redEnvelopes.size());
//            if (redEnvelopes.size() == params.requestedLoadSize) {
//                callback.onResult(redEnvelopes, params.key - 1);
//            } else {
//                callback.onResult(redEnvelopes, null);
//            }
//        }
    }

//    private List<RedEnvelope> getRedEnvelopes(int page, int size) {
//        return mRepository.loadRedEnvelopesFromNetwork(page, size);
//    }
}
