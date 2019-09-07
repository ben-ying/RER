package com.yjh.rer.databinding;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;


public class RedEnvelopeDataSource extends PageKeyedDataSource<Integer, RedEnvelope> {
    private final static String TAG = RedEnvelopeDataSource.class.getSimpleName();

    private int type;
    private RedEnvelopeRepository repository;

    RedEnvelopeDataSource(int type, RedEnvelopeRepository redEnvelopeRepository) {
        this.type = type;
        this.repository = redEnvelopeRepository;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                            @NonNull LoadInitialCallback<Integer, RedEnvelope> callback) {
        Log.d(TAG, "loadInitial: " + params.requestedLoadSize);
        List<RedEnvelope> redEnvelopes = getRedEnvelopes(1, params.requestedLoadSize);

        if (redEnvelopes != null) {
            callback.onResult(redEnvelopes, null, 2);
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params,
                          @NonNull LoadCallback<Integer, RedEnvelope> callback) {
        Log.d(TAG, "loadAfter: " + params.key + ", " + params.requestedLoadSize);
        List<RedEnvelope> redEnvelopes = getRedEnvelopes(params.key, params.requestedLoadSize);
        if (redEnvelopes != null) {
            callback.onResult(redEnvelopes, params.key + 1);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params,
                           @NonNull LoadCallback<Integer, RedEnvelope> callback) {
        Log.d(TAG, "loadBefore: " + params.key + ", " + params.requestedLoadSize);
        List<RedEnvelope> redEnvelopes = getRedEnvelopes(params.key, params.requestedLoadSize);
        if (redEnvelopes != null) {
            callback.onResult(redEnvelopes, params.key - 1);
        }
    }

    public List<RedEnvelope> getRedEnvelopes(int page, int size) {
        return repository.getRedEnvelopeList(page, size);
    }
}
