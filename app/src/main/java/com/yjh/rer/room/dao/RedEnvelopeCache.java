package com.yjh.rer.room.dao;

import android.util.Log;

import androidx.paging.DataSource;

import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


public class RedEnvelopeCache {
    private static final String TAG = RedEnvelopeDao.class.getSimpleName();
    private long mExpireTime = TimeUnit.MINUTES.toMillis(3);

    private RedEnvelopeDao mDao;
    private Executor mIOExecutor;

    public RedEnvelopeCache(RedEnvelopeDao dao, Executor ioExecutor) {
        this.mDao = dao;
        this.mIOExecutor = ioExecutor;
    }

    public void insert(final int page,
                       final List<RedEnvelope> redEnvelopes,
                       final InsertFinishedListener listener) {
        mIOExecutor.execute(() -> {
            Log.d(TAG, String.format("Inserting %s redEnvelopes", redEnvelopes.size()));
            for (RedEnvelope redEnvelope : redEnvelopes) {
                redEnvelope.setPage(page);
                mDao.insert(redEnvelope);
            }
            deleteExpired(page);
            listener.onInsertFinished();
        });
    }

    private void deleteExpired(final int page) {
        final List<RedEnvelope> redEnvelopes = mDao.loadFromDb(page);
        for (RedEnvelope redEnvelope : redEnvelopes) {
            if (System.currentTimeMillis() - redEnvelope.getInsertedTime() > mExpireTime) {
                mDao.delete(redEnvelope);
                Log.d(TAG, "Delete expired redEnvelopes: %s \n" + redEnvelope.getLabel());
            }
        }
    }

    public DataSource.Factory<Integer, RedEnvelope> getList() {
        return mDao.getList();
    }

    public interface InsertFinishedListener {
        void onInsertFinished();
    }
}
