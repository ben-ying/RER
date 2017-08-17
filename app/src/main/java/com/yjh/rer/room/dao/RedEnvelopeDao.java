package com.yjh.rer.room.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.yjh.rer.room.entity.RedEnvelope;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface RedEnvelopeDao {
    @Insert(onConflict = REPLACE)
    void saveRedEnvelope(RedEnvelope redEnvelope);

    @Query("SELECT * FROM " + RedEnvelope.TABLE_NAME +
            " WHERE " + RedEnvelope.FIELD_USER_ID + " = :userId")
    LiveData<List<RedEnvelope>> loadRedEnvelopes(String userId);
}
