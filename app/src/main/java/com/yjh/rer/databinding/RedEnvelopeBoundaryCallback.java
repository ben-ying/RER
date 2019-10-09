package com.yjh.rer.databinding;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PagedList;

import com.yjh.rer.network.NetworkState;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.dao.RedEnvelopeCache;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

public class RedEnvelopeBoundaryCallback
        extends PagedList.BoundaryCallback<RedEnvelope> implements NetworkState.callback {

    private static final String TAG = RedEnvelopeBoundaryCallback.class.getSimpleName();
    private static final int NETWORK_PAGE_SIZE = 20;

    private int mLastRequestedPage = 1;
    private MutableLiveData<String> mNetworkErrors = new MutableLiveData<>();
    public LiveData<String> getNetworkErrors() {
        return mNetworkErrors;
    }
    // Avoid triggering multiple requests in the same time
    private boolean mIsRequestInProgress = false;
    private Webservice mService;
    private RedEnvelopeCache mCache;
    private boolean mIsRefreshed;

    public RedEnvelopeBoundaryCallback(Webservice service, RedEnvelopeCache cache) {
        this.mService = service;
        this.mCache = cache;
    }

    @Override
    public void onItemAtEndLoaded(@NonNull RedEnvelope itemAtEnd) {
        super.onItemAtEndLoaded(itemAtEnd);
        Log.d(TAG, "onItemAtEndLoaded");
        requestAndSaveData();
    }

    @Override
    public void onZeroItemsLoaded() {
        super.onZeroItemsLoaded();
        Log.d(TAG, "onZeroItemsLoaded");
        requestAndSaveData();
    }

    @Override
    public void onItemAtFrontLoaded(@NonNull RedEnvelope itemAtFront) {
        super.onItemAtFrontLoaded(itemAtFront);
        Log.d(TAG, "onItemAtFrontLoaded");
        if (!mIsRefreshed) {
            requestAndSaveData();
        }
    }

    private void requestAndSaveData() {
        Log.d(TAG, "requestAndSaveData: " + mIsRequestInProgress);
        if (mIsRequestInProgress) return;

        mIsRequestInProgress = true;
        RedEnvelopeRepository.loadRedEnvelopesFromNetwork(
                mService, mLastRequestedPage, NETWORK_PAGE_SIZE, this);
    }

    @Override
    public void onSuccess(List<RedEnvelope> redEnvelopes, boolean isLastPage) {
        mIsRefreshed = true;
        mCache.insert(redEnvelopes, () -> {
            if (!isLastPage) {
                mLastRequestedPage++;
                mIsRequestInProgress = false;
            }
        });
    }

    @Override
    public void onError(String error) {
        mNetworkErrors.postValue(error);
        mIsRequestInProgress = false;
    }
}
