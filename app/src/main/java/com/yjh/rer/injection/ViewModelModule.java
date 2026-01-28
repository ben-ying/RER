package com.yjh.rer.injection;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
