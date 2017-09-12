package com.yjh.rer.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(RedEnvelopeViewModel.class)
    abstract ViewModel bindRedEnvelopeViewModel(RedEnvelopeViewModel redEnvelopeViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(RedEnvelopeViewModelFactory factory);
}
