package com.yjh.rer.injection;

import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = RedEnvelopeModule.class)
public interface RedEnvelopeComponent {
    void inject(RedEnvelopeViewModel model);
}
