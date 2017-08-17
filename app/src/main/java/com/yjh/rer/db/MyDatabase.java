package com.yjh.rer.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.yjh.rer.dao.RedEnvelopeDao;
import com.yjh.rer.entity.RedEnvelope;

@Database(entities = {RedEnvelope.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract RedEnvelopeDao redEnvelopeDao();
}
