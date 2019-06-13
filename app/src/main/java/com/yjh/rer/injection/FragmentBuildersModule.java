package com.yjh.rer.injection;

import com.yjh.rer.main.chart.BarChartFragment;
import com.yjh.rer.main.list.RedEnvelopesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract RedEnvelopesFragment contributeRedEnvelopesFragment();
    @ContributesAndroidInjector
    abstract BarChartFragment contributeBarChartFragment();
}
