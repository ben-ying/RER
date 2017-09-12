package com.yjh.rer.base;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.arch.lifecycle.ViewModelProvider;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.squareup.leakcanary.RefWatcher;
import com.yjh.rer.MyApplication;
import com.yjh.rer.R;
import com.yjh.rer.injection.Injectable;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.ArrayList;

import javax.inject.Inject;

public abstract class BaseLifecycleFragment extends BaseFragment
        implements LifecycleRegistryOwner, Injectable {

    public LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }
}
