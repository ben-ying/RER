package com.yjh.rer.base;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public abstract class BaseDaggerActivity<T extends ViewDataBinding> extends BaseActivity<T>
        implements
        HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
