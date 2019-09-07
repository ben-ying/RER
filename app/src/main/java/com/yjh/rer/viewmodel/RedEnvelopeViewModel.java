package com.yjh.rer.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.yjh.rer.databinding.RedEnvelopeBoundaryCallback;
import com.yjh.rer.databinding.RedEnvelopeDataSource;
import com.yjh.rer.databinding.RedEnvelopeDataSourceFactory;
import com.yjh.rer.network.Resource;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class RedEnvelopeViewModel extends ViewModel {
    private static final int TYPE_LOAD_FROM_DB = 0;
    private static final int TYPE_LOAD_FROM_NETWORK = 1;
    private static final int TYPE_ADD = 2;
    private static final int TYPE_DELETE = 3;
    private static final int PAGE_SIZE = 20;
    private static final int MAX_SIZE = 1000;

    private final MutableLiveData<ReId> mReIdLiveData = new MutableLiveData<>();

    private MutableLiveData<String> mToken = new MutableLiveData<>();
    private LiveData<Resource<List<RedEnvelope>>> mRedEnvelopes;
    private ReId mReId = new ReId();
    private LiveData<PagedList<RedEnvelope>> mRedEnvelopeList1;
    private RedEnvelopeDataSource mDataSource;
    private LiveData<PagedList<RedEnvelope>> mRedEnvelopeList;

    @Inject
    RedEnvelopeDao dao;

    @Inject
    RedEnvelopeViewModel(final RedEnvelopeRepository repository) {
        Executor executor = Executors.newFixedThreadPool(5);;
        RedEnvelopeDataSourceFactory dataSourceFactory =
                new RedEnvelopeDataSourceFactory(1, repository);
        mDataSource = dataSourceFactory.create();

        mRedEnvelopeList = Transformations.switchMap(mReIdLiveData, reId -> {
            switch (reId.type) {
                case TYPE_LOAD_FROM_DB:
                    return new LivePagedListBuilder<>(
                            repository.getDao().getList(), getPageListConfig(true))
                            .setBoundaryCallback(new RedEnvelopeBoundaryCallback(repository))
                            .setFetchExecutor(executor)
                            .build();
                case TYPE_LOAD_FROM_NETWORK:
                    return new LivePagedListBuilder<>(
                            dataSourceFactory, getPageListConfig(false))
                            .setBoundaryCallback(new RedEnvelopeBoundaryCallback(repository))
                            .setFetchExecutor(executor)
                            .build();
            }

            return null;
        });

//        mRedEnvelopes = Transformations.switchMap(mReIdLiveData, reId -> {
//            switch (reId.type) {
//                case TYPE_LOAD:
//                    return repository.loadRedEnvelopes(mToken.getValue(), reId.userId);
//                case TYPE_ADD:
//                    return repository.addRedEnvelope(
//                            reId.moneyFrom, reId.money, reId.remark, mToken.getValue());
//                case TYPE_DELETE:
//                    return repository.deleteRedEnvelope(reId.id, mToken.getValue());
//            }
//            return null;
//        });
    }

    public LiveData<PagedList<RedEnvelope>> getRedEnvelopeList() {
        return mRedEnvelopeList;
    }

    public LiveData<PagedList<RedEnvelope>> getRedEnvelopeList1() {
        return mRedEnvelopeList1;
    }

    public void setToken(String token) {
        mToken.setValue(token);
    }

//    public LiveData<Resource<List<RedEnvelope>>> getRedEnvelopesResource() {
//        return mRedEnvelopes;
//    }

    public LiveData<List<RedEnvelope>> getRedEnvelopes() {
        return dao.loadAll();
    }

    public void loadFromDB(String userId) {
        mReId.type = TYPE_LOAD_FROM_DB;
        mReId.userId = userId;
        mReIdLiveData.setValue(mReId);
    }

    public void loadFromNetwork(String userId) {
        mReId.type = TYPE_LOAD_FROM_NETWORK;
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

    public void invalidateDataSource() {
        mDataSource.invalidate();
    }

    private PagedList.Config getPageListConfig(boolean isDb) {
        return new PagedList.Config.Builder()
                .setPageSize(isDb ? PAGE_SIZE * 5 : PAGE_SIZE)
                .setInitialLoadSizeHint(isDb ? PAGE_SIZE * 20 : PAGE_SIZE * 2)
                .setPrefetchDistance(PAGE_SIZE * 2)
                .setEnablePlaceholders(false)
                .build();
    }
}
