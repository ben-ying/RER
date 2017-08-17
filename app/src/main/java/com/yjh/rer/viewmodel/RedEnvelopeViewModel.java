package com.yjh.rer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import com.yjh.rer.dao.RedEnvelopeDao;
import com.yjh.rer.entity.RedEnvelope;
import com.yjh.rer.injection.DaggerRedEnvelopeComponent;
import com.yjh.rer.injection.RedEnvelopeModule;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.webservice.Webservice;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;

public class RedEnvelopeViewModel extends ViewModel implements RedEnvelopeDao {
    private LiveData<List<RedEnvelope>> redEnvelopes;
    @Inject
    RedEnvelopeRepository repository;

    public LiveData<List<RedEnvelope>> getRedEnvelopes() {
            return redEnvelopes;
    }

    public void init(String token, String userId) {
        if (this.redEnvelopes != null) {
            return;
        }
        DaggerRedEnvelopeComponent.builder().redEnvelopeModule(
                new RedEnvelopeModule(this)).build().inject(this);
        redEnvelopes = repository.getRedEnvelopes(token, userId);
    }

    @Override
    public void saveRedEnvelope(RedEnvelope redEnvelope) {
        Log.d("", "");
    }

    @Override
    public LiveData<List<RedEnvelope>> loadRedEnvelopes(String userId) {
        Log.d("", "");
        return null;
    }
}
