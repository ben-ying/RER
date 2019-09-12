package com.yjh.rer.network;

import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;


public class NetworkState {
    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    public interface callback {
        void onSuccess(List<RedEnvelope> redEnvelopes, boolean isLastPage);

        void onError(String error);
    }
}