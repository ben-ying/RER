package com.yjh.rer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.yjh.rer.network.Resource;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import javax.inject.Inject;

public class RedEnvelopeViewModel extends ViewModel {
    private static final int TYPE_LOAD = 0;
    private static final int TYPE_ADD = 1;
    private static final int TYPE_DELETE = 2;

    private final MutableLiveData<ReId> mReIdLiveData = new MutableLiveData<>();
    private MutableLiveData<String> mToken = new MutableLiveData<>();
    private LiveData<Resource<List<RedEnvelope>>> mRedEnvelopes;
    private ReId mReId = new ReId();

    @Inject
    RedEnvelopeDao dao;

    @Inject
    RedEnvelopeViewModel(final RedEnvelopeRepository repository) {
        mRedEnvelopes = Transformations.switchMap(mReIdLiveData, reId -> {
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
        });
    }

    public void setToken(String token) {
        mToken.setValue(token);
    }

    public LiveData<Resource<List<RedEnvelope>>> getRedEnvelopesResource() {
        return mRedEnvelopes;
    }

    public LiveData<List<RedEnvelope>> getRedEnvelopes() {
        return dao.loadAll();
    }

    public void load(String userId) {
        mReId.type = TYPE_LOAD;
        mReId.userId = userId;
        mReIdLiveData.setValue(mReId);
    }

    public void add(String moneyFrom, String money, String remark) {
        mReId.type = TYPE_ADD;
        mReId.moneyFrom = moneyFrom;
        mReId.money = money;
        mReId.remark = remark;
        mReIdLiveData.setValue(mReId);
    }

    public void delete(int id) {
        mReId.type = TYPE_DELETE;
        mReId.id = id;
        mReIdLiveData.setValue(mReId);
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
