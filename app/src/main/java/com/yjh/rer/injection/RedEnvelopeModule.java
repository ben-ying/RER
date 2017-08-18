package com.yjh.rer.injection;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.yjh.rer.MyApplication;
import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.room.db.MyDatabase;
import com.yjh.rer.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class RedEnvelopeModule {

    private Application application;
    private static final String BASE_URL = "http://bensbabycare.com/webservice/";

    @Singleton
    @Provides
    RedEnvelopeRepository provideRedEnvelopeRepository(Webservice webservice, MyDatabase database) {
        return new RedEnvelopeRepository(webservice, database);
    }

    @Singleton
    @Provides
    Webservice provideWebservice() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(Webservice.class);
    }

    @Singleton
    @Provides
    MyDatabase provideDb(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "rer.db").build();
    }

    @Singleton
    @Provides
    RedEnvelopeDao provideRedEnvelopeDao(MyDatabase db) {
        return db.redEnvelopeDao();
    }


    @Provides
    Context applicationContext() {
        return application;
    }

    public RedEnvelopeModule(Application application) {
        this.application = application;
    }
}
