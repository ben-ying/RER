package com.yjh.rer.injection;

import com.yjh.rer.main.RedEnvelopesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract RedEnvelopesFragment contributeRedEnvelopesFragment();
}
