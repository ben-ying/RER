package com.yjh.rer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RedEnvelopeDao {
    @Insert(onConflict = REPLACE)
    void save(RedEnvelope redEnvelope);

    @Insert(onConflict = REPLACE)
    void saveAll(RedEnvelope... redEnvelopes);

    @Delete
    void delete(RedEnvelope redEnvelope);

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " ORDER BY -" + RedEnvelope.FIELD_RED_ENVELOPE_ID)
    LiveData<List<RedEnvelope>> loadAll();

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " WHERE " + RedEnvelope.FIELD_RED_ENVELOPE_ID + " =:reId LIMIT 1")
    LiveData<RedEnvelope> findById(int reId);

    @Query("SELECT Count(*) FROM " + RedEnvelope.TABLE_NAME)
    int count();
}
