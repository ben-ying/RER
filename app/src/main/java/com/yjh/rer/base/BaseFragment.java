package com.yjh.rer.base;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.squareup.leakcanary.RefWatcher;
import com.yjh.rer.MyApplication;
import com.yjh.rer.R;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment implements LifecycleRegistryOwner {

    public LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);

    @Override
    public LifecycleRegistry getLifecycle() {
        return mLifecycleRegistry;
    }

    protected void replaceFragment(BaseFragment newFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.container, newFragment);
//        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void updateData(ArrayList<RedEnvelope> redEnvelopes){}

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
