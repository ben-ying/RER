package com.yjh.rer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.yjh.rer.model.ListResponseResult;
import com.yjh.rer.model.RedEnvelopeResult;
import com.yjh.rer.network.Resource;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import javax.inject.Inject;

public class RedEnvelopeViewModel extends ViewModel {
    public static final int TYPE_LOAD = 1;
    public static final int TYPE_REFRESH = 2;
    private static final int TYPE_ADD = 3;
    private static final int TYPE_DELETE = 4;

    private final MutableLiveData<Integer> mQueryLiveData = new MutableLiveData<>();
    private final MutableLiveData<ReId> mReIdLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mToken = new MutableLiveData<>();
    private ReId mReId = new ReId();
    private LiveData<PagedList<RedEnvelope>> mRedEnvelopeList;
    private LiveData<String> mNetworkErrors;
    private LiveData<Resource<RedEnvelope>> mOperatingItem;
    private RedEnvelopeRepository mRepository;

    @Inject
    RedEnvelopeDao dao;

    @Inject
    RedEnvelopeViewModel(final RedEnvelopeRepository repository) {
        this.mRepository = repository;

        LiveData<RedEnvelopeResult> repoResult = Transformations.map(mQueryLiveData,
                repository::loadRedEnvelopes
        );

        mOperatingItem = Transformations.switchMap(mReIdLiveData, reId -> {
            switch (reId.type) {
                case TYPE_ADD:
                    return repository.addRedEnvelope(
                            reId.moneyFrom, reId.money, reId.remark, mToken.getValue(), 0);
                case TYPE_DELETE:
                    return repository.deleteRedEnvelope(reId.id, mToken.getValue(), 0);
            }
            return null;
        });

        mRedEnvelopeList = Transformations.switchMap(repoResult,
                RedEnvelopeResult::getData
        );

        mNetworkErrors = Transformations.switchMap(repoResult,
                RedEnvelopeResult::getNetworkErrors
        );
    }

    public ListResponseResult<List<RedEnvelope>> getResult() {
        return mRepository.getResult();
    }

    public LiveData<PagedList<RedEnvelope>> getRedEnvelopeList() {
        return mRedEnvelopeList;
    }

    public LiveData<Resource<RedEnvelope>> getOperatingItem() {
        return mOperatingItem;
    }

    public LiveData<String> getNetworkErrors() {
        return mNetworkErrors;
    }

    public void setToken(String token) {
        mToken.setValue(token);
    }

    public LiveData<List<RedEnvelope>> getRedEnvelopes() {
        return dao.loadAll();
    }

    public void load(int type) {
        mQueryLiveData.postValue(type);
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
