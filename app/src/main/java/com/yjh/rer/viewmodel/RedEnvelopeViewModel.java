package com.yjh.rer.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.yjh.rer.network.Resource;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.injection.DaggerRedEnvelopeComponent;
import com.yjh.rer.injection.RedEnvelopeModule;
import com.yjh.rer.repository.RedEnvelopeRepository;

import java.util.List;

import javax.inject.Inject;

public class RedEnvelopeViewModel extends ViewModel implements RedEnvelopeDao {
    private LiveData<Resource<List<RedEnvelope>>> redEnvelopes;
    @Inject
    RedEnvelopeRepository repository;

    public LiveData<Resource<List<RedEnvelope>>> getRedEnvelopes() {
            return redEnvelopes;
    }

    public void init(String token, String userId) {
        if (this.redEnvelopes != null) {
            return;
        }
        DaggerRedEnvelopeComponent.builder().redEnvelopeModule(
                new RedEnvelopeModule(this)).build().inject(this);
        redEnvelopes = repository.loadRedEnvelopes(token, userId);
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
