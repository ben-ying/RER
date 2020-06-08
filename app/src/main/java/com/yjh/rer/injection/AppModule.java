package com.yjh.rer.injection;

import android.app.Application;

import androidx.room.Room;

import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeCache;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.db.MyDatabase;
import com.yjh.rer.custom.CustomCallAdapterFactory;
import com.yjh.rer.util.LiveDataCallAdapterFactory;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
//    private static final String BASE_URL = "http://bensbabycare.com/webservice/";
    private static final String BASE_URL = "http://mybackend.online:8000/iaer/api/";

    @Singleton
    @Provides
    Webservice provideWebservice() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CustomCallAdapterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(Webservice.class);
    }

    @Singleton
    @Provides
    MyDatabase provideDb(Application application) {
        return Room.databaseBuilder(application, MyDatabase.class, "rer.db").build();
    }

    @Singleton
    @Provides
    RedEnvelopeDao provideRedEnvelopeDao(MyDatabase db) {
        return db.redEnvelopeDao();
    }

    @Singleton
    @Provides
    RedEnvelopeCache provideCache(MyDatabase db) {
        return new RedEnvelopeCache(db.redEnvelopeDao(), Executors.newSingleThreadExecutor());
    }
}
