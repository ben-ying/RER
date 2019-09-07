package com.yjh.rer.databinding;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.paging.PagedList;

import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

public class RedEnvelopeBoundaryCallback extends PagedList.BoundaryCallback<RedEnvelope> {

    private RedEnvelopeRepository repository;

    public RedEnvelopeBoundaryCallback(RedEnvelopeRepository repository) {
        this.repository = repository;
    }

    @Override
    public void onItemAtEndLoaded(@NonNull RedEnvelope itemAtEnd) {
//        super.onItemAtEndLoaded(itemAtEnd);
        Log.d("", "");
    }

    @Override
    public void onZeroItemsLoaded() {
//        super.onZeroItemsLoaded();
        Log.d("", "");
//        repository.loadRedEnvelopes("83cd0f7a0483db73ce4223658cb61deac6531e85", "1");
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull RedEnvelope itemAtFront) {
//        super.onItemAtFrontLoaded(itemAtFront);
        Log.d("", "");
    }
}
