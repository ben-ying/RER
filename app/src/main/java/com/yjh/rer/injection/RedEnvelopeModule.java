package com.yjh.rer.injection;

import com.yjh.rer.dao.RedEnvelopeDao;
import com.yjh.rer.repository.RedEnvelopeRepository;
import com.yjh.rer.webservice.Webservice;

import java.util.concurrent.Executor;

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
