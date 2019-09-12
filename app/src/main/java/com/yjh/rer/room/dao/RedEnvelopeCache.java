package com.yjh.rer.room.dao;

import android.util.Log;

import androidx.paging.DataSource;

import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;
import java.util.concurrent.Executor;


public class RedEnvelopeCache {
    private static final String TAG = RedEnvelopeDao.class.getSimpleName();

    private RedEnvelopeDao mDao;
    private Executor mIOExecutor;

    public RedEnvelopeCache(RedEnvelopeDao dao, Executor ioExecutor) {
        this.mDao = dao;
        this.mIOExecutor = ioExecutor;
    }

    public void insert(final List<RedEnvelope> redEnvelopes,
                       final InsertFinishedListener listener) {
        mIOExecutor.execute(() -> {
            Log.d(TAG, String.format("Inserting %s redEnvelopes", redEnvelopes.size()));
            mDao.insert(redEnvelopes);
            listener.onInsertFinished();
        });
    }

    public DataSource.Factory<Integer, RedEnvelope> getList() {
        return mDao.getList();
    }

    public interface InsertFinishedListener {
        void onInsertFinished();
    }
}
