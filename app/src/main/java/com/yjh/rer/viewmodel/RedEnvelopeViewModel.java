package com.yjh.rer.viewmodel;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.yjh.rer.injection.DaggerRedEnvelopeComponent;
import com.yjh.rer.injection.RedEnvelopeModule;
import com.yjh.rer.network.Resource;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import javax.inject.Inject;

public class RedEnvelopeViewModel extends AndroidViewModel {
    private static final int TYPE_LOAD = 0;
    private static final int TYPE_ADD = 1;
    private static final int TYPE_DELETE = 2;

    private LiveData<Resource<List<RedEnvelope>>> redEnvelopes;
    private final MutableLiveData<ReId> reIdLiveData = new MutableLiveData<>();

    @Inject
    RedEnvelopeRepository repository;

    public RedEnvelopeViewModel(Application application, final String token) {
        super(application);
        if (this.redEnvelopes != null) {
            return;
        }
        DaggerRedEnvelopeComponent.builder().redEnvelopeModule(
                new RedEnvelopeModule(this.getApplication())).build().inject(this);
        redEnvelopes = Transformations.switchMap(reIdLiveData, new Function<ReId,
                LiveData<Resource<List<RedEnvelope>>>>() {
            @Override
            public LiveData<Resource<List<RedEnvelope>>> apply(ReId reId) {
                switch (reId.type) {
                    case TYPE_LOAD:
                        return repository.loadRedEnvelopes(token, reId.userId);
                    case TYPE_ADD:
                        return repository.addRedEnvelope(reId.moneyFrom, reId.money, reId.remark, token);
                    case TYPE_DELETE:
                        return repository.deleteRedEnvelope(reId.id, token);
                }
                return null;
            }
        });
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

    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application application;
        private final String token;

        public Factory(@NonNull Application application, String token) {
            this.application = application;
            this.token = token;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new RedEnvelopeViewModel(application, token);
        }
    }
}
