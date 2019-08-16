package com.yjh.rer.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleRegistryOwner;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements
        LifecycleRegistryOwner {

    private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        } else {
            setDataBinding();
        }
        ButterKnife.bind(this);

        initView();
    }

    public void setDataBinding() {

    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    public abstract int getLayoutId();

    public abstract void initView();
}
