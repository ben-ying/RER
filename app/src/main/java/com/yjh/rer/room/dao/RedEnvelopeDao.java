package com.yjh.rer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface RedEnvelopeDao {
    @Insert(onConflict = REPLACE)
    void save(RedEnvelope redEnvelope);

    @Insert(onConflict = REPLACE)
    void saveAll(List<RedEnvelope> redEnvelopes);

    @Delete
    void delete(RedEnvelope redEnvelope);

    @Query("DELETE FROM " + RedEnvelope.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " ORDER BY -" + RedEnvelope.FIELD_RED_ENVELOPE_ID)
    LiveData<List<RedEnvelope>> loadAll();

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " ORDER BY -" + RedEnvelope.FIELD_RED_ENVELOPE_ID)
    List<RedEnvelope> loadAllList();

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME)
    List<RedEnvelope> loadAllList1();

    @Query("SELECT Count(*) FROM " + RedEnvelope.TABLE_NAME)
    int count();
}
