package com.yjh.rer.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.util.Log;

import com.yjh.rer.injection.DaggerRedEnvelopeComponent;
import com.yjh.rer.injection.RedEnvelopeModule;
import com.yjh.rer.network.Resource;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import javax.inject.Inject;

public class RedEnvelopeViewModel extends AndroidViewModel {
    private LiveData<Resource<List<RedEnvelope>>> redEnvelopes;
    private LiveData<Resource<RedEnvelope>> redEnvelope;
    private String token;
    @Inject
    RedEnvelopeRepository repository;
    Application application;

    public RedEnvelopeViewModel(Application application) {
        super(application);
        this.application = application;
    }

    public LiveData<Resource<List<RedEnvelope>>> getRedEnvelopes() {
            return redEnvelopes;
    }

    public LiveData<Resource<RedEnvelope>> getRedEnvelope() {
        return redEnvelope;
    }

    public void init(String token, String userId) {
        if (this.redEnvelopes != null) {
            return;
        }
        this.token = token;
        DaggerRedEnvelopeComponent.builder().redEnvelopeModule(
                new RedEnvelopeModule(application)).build().inject(this);
        redEnvelopes = repository.loadRedEnvelopes(token, userId);
    }

    public void add(String fromMoney, String money, String remark) {
        redEnvelope = repository.addRedEnvelope(fromMoney, money, remark, token);
    }

    public void delete(int reId) {
        redEnvelope = repository.deleteRedEnvelope(reId, token);
    }
}
