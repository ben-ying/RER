package com.yjh.rer.injection;

import com.yjh.rer.ui.chart.BarChartFragment;
import com.yjh.rer.ui.chart.HorizontalBarChartFragment;
import com.yjh.rer.ui.chart.PieChartFragment;
import com.yjh.rer.ui.list.RedEnvelopesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract RedEnvelopesFragment contributeRedEnvelopesFragment();
    @ContributesAndroidInjector
    abstract BarChartFragment contributeBarChartFragment();
    @ContributesAndroidInjector
    abstract HorizontalBarChartFragment contributeHorizontalBarChartFragment();
    @ContributesAndroidInjector
    abstract PieChartFragment contributePieChartFragment();
}
