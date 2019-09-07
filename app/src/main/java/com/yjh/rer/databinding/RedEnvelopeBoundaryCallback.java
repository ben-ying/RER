package com.yjh.rer.databinding;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

public class RedEnvelopeBoundaryCallback extends PagedList.BoundaryCallback<RedEnvelope> {

    private static final String TAG = RedEnvelopeBoundaryCallback.class.getSimpleName();

    private RedEnvelopeRepository mRepository;

    public RedEnvelopeBoundaryCallback(RedEnvelopeRepository repository) {
        this.mRepository = repository;
    }

    @Override
    public void onItemAtEndLoaded(@NonNull RedEnvelope itemAtEnd) {
//        super.onItemAtEndLoaded(itemAtEnd);
        Log.d(TAG, "onItemAtEndLoaded");
    }

    @Override
    public void onZeroItemsLoaded() {
//        super.onZeroItemsLoaded();
        Log.d(TAG, "onZeroItemsLoaded");
//        repository.loadRedEnvelopes("83cd0f7a0483db73ce4223658cb61deac6531e85", "1");
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull RedEnvelope itemAtFront) {
//        super.onItemAtFrontLoaded(itemAtFront);
        Log.d(TAG, "onItemAtFrontLoaded");
    }
}
