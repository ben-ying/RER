package com.yjh.rer.base;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleRegistry;


public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity {

    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    public T dataBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, getLayoutId());

        initView();
    }

    @Override
    @NonNull
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    public abstract int getLayoutId();

    public abstract void initView();
}
