package com.yjh.rer.base;

import android.arch.lifecycle.ViewModelProvider;

import com.yjh.rer.injection.Injectable;

import javax.inject.Inject;

public abstract class BaseDaggerFragment extends BaseFragment
        implements Injectable {

    @Inject
    public ViewModelProvider.Factory viewModelFactory;
}
