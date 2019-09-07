package com.yjh.rer.databinding;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.entity.RedEnvelope;

public class RedEnvelopeDataSourceFactory extends DataSource.Factory<Integer, RedEnvelope> {

    private int mType;
    private RedEnvelopeRepository mRedEnvelopeRepository;

    private RedEnvelopeDataSource source;
    private MutableLiveData<RedEnvelopeDataSource> sourceLiveData = new MutableLiveData<>();

    public RedEnvelopeDataSourceFactory(int type, RedEnvelopeRepository redEnvelopeRepository) {
        this.mType = type;
        this.mRedEnvelopeRepository = redEnvelopeRepository;
    }

    @NonNull
    @Override
    public RedEnvelopeDataSource create() {
        source = new RedEnvelopeDataSource(mType, mRedEnvelopeRepository);
        sourceLiveData.postValue(source);

        return source;
    }
}
