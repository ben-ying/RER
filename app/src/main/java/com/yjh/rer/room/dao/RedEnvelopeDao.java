package com.yjh.rer.room.dao;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
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
    void insert(List<RedEnvelope> redEnvelopes);

    @Delete
    void delete(RedEnvelope redEnvelope);

    @Query("DELETE FROM " + RedEnvelope.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " ORDER BY -" + RedEnvelope.FIELD_RED_ENVELOPE_ID)
    DataSource.Factory<Integer, RedEnvelope> getList();

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " ORDER BY -" + RedEnvelope.FIELD_RED_ENVELOPE_ID + " LIMIT :size " )
    List<RedEnvelope> loadFromDb(int size);

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " ORDER BY -" + RedEnvelope.FIELD_RED_ENVELOPE_ID)
    LiveData<List<RedEnvelope>> loadAll();

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " WHERE " + RedEnvelope.FIELD_RED_ENVELOPE_ID + " = :id" +
            " ORDER BY -" + RedEnvelope.FIELD_RED_ENVELOPE_ID)
    LiveData<RedEnvelope> loadById(int id);

    @Query("SELECT Count(*) FROM " + RedEnvelope.TABLE_NAME)
    int count();
}
