package com.yjh.rer.viewmodel;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.yjh.rer.network.Resource;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import javax.inject.Inject;

public class RedEnvelopeViewModel extends ViewModel {
    private static final int TYPE_LOAD = 0;
    private static final int TYPE_ADD = 1;
    private static final int TYPE_DELETE = 2;

    private final MutableLiveData<ReId> reIdLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mToken = new MutableLiveData<>();
    private LiveData<Resource<List<RedEnvelope>>> redEnvelopes;

    @Inject
    public RedEnvelopeViewModel(final RedEnvelopeRepository repository) {
        if (this.redEnvelopes != null) {
            return;
        }
        redEnvelopes = Transformations.switchMap(reIdLiveData, new Function<ReId,
                LiveData<Resource<List<RedEnvelope>>>>() {
            @Override
            public LiveData<Resource<List<RedEnvelope>>> apply(ReId reId) {
                switch (reId.type) {
                    case TYPE_LOAD:
                        return repository.loadRedEnvelopes(mToken.getValue(), reId.userId);
                    case TYPE_ADD:
                        return repository.addRedEnvelope(
                                reId.moneyFrom, reId.money, reId.remark, mToken.getValue());
                    case TYPE_DELETE:
                        return repository.deleteRedEnvelope(reId.id, mToken.getValue());
                }
                return null;
            }
        });
    }

    public void setToken(String token) {
        mToken.setValue(token);
    }

    public LiveData<Resource<List<RedEnvelope>>> getRedEnvelopes() {
        return redEnvelopes;
    }

    public void load(String userId) {
        ReId reId = new ReId();
        reId.type = TYPE_LOAD;
        reId.userId = userId;
        reIdLiveData.setValue(reId);
    }

    public void add(String moneyFrom, String money, String remark) {
        ReId reId = new ReId();
        reId.type = TYPE_ADD;
        reId.moneyFrom = moneyFrom;
        reId.money = money;
        reId.remark = remark;
        reIdLiveData.setValue(reId);
    }

    public void delete(int id) {
        ReId reId = new ReId();
        reId.type = TYPE_DELETE;
        reId.id = id;
        reIdLiveData.setValue(reId);
    }

    static class ReId {
        int id;
        int type;
        String userId;
        String moneyFrom;
        String money;
        String remark;
    }
}
