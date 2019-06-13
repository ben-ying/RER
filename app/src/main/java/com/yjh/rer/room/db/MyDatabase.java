package com.yjh.rer.room.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.entity.RedEnvelope;

@Database(entities = {RedEnvelope.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract RedEnvelopeDao redEnvelopeDao();
}
