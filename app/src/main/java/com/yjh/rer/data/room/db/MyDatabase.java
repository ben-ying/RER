package com.yjh.rer.data.room.db;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.yjh.rer.data.room.dao.RedEnvelopeDao;
import com.yjh.rer.data.room.entity.RedEnvelope;


@Database(entities = {RedEnvelope.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract RedEnvelopeDao redEnvelopeDao();
}
