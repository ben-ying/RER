package com.yjh.rer.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.squareup.leakcanary.RefWatcher;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LifecycleRegistryOwner;

import com.yjh.rer.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment
        implements LifecycleRegistryOwner {

    private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
//    private Unbinder mUnBinder;

    public T dataBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(), container, false);
//        mUnBinder = ButterKnife.bind(this, v);
        dataBinding = DataBindingUtil.bind(v);

        initView();

        return v;
    }

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    protected void replaceFragment(BaseFragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.container, newFragment);
        transaction.commit();
    }

    public abstract int getLayoutId();

    public abstract void initView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mUnBinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }
}
