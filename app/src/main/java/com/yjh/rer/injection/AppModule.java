package com.yjh.rer.injection;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.yjh.rer.network.Webservice;
import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.room.db.MyDatabase;
import com.yjh.rer.util.LiveDataCallAdapterFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
//    private static final String BASE_URL = "http://bensbabycare.com/webservice/";
    private static final String BASE_URL = "http://mybackend.online:8080/iaer/api/";

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
    MyDatabase provideDb(Application application) {
        return Room.databaseBuilder(application, MyDatabase.class, "rer.db").build();
    }

    @Singleton
    @Provides
    RedEnvelopeDao provideRedEnvelopeDao(MyDatabase db) {
        return db.redEnvelopeDao();
    }
}
