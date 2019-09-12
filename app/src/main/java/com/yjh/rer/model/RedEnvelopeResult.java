package com.yjh.rer.model;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.yjh.rer.room.entity.RedEnvelope;


public class RedEnvelopeResult {
    private LiveData<PagedList<RedEnvelope>> data;
    private LiveData<String> networkErrors;

    public RedEnvelopeResult(LiveData<PagedList<RedEnvelope>> data, LiveData<String> networkErrors) {
        this.data = data;
        this.networkErrors = networkErrors;
    }

    public LiveData<PagedList<RedEnvelope>> getData() {
        return data;
    }

    public void setData(LiveData<PagedList<RedEnvelope>> data) {
        this.data = data;
    }

    public LiveData<String> getNetworkErrors() {
        return networkErrors;
    }

    public void setNetworkErrors(LiveData<String> networkErrors) {
        this.networkErrors = networkErrors;
    }
}
