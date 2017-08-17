package com.yjh.rer.injection;

import com.yjh.rer.room.dao.RedEnvelopeDao;
import com.yjh.rer.repository.RedEnvelopeRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;


@Module
public class RedEnvelopeModule {
    private final RedEnvelopeDao dao;

    public RedEnvelopeModule(RedEnvelopeDao dao) {
        this.dao = dao;
    }

    @Singleton
    @Provides
    RedEnvelopeRepository provideRedEnvelopeRepository() {
        return new RedEnvelopeRepository(dao);
    }
}
