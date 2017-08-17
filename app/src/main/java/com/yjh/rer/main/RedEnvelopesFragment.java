package com.yjh.rer.main;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.network.Resource;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.List;

public class RedEnvelopesFragment extends BaseFragment {

    private List<RedEnvelope> mRedEnvelopes;
    private RedEnvelopeViewModel mViewModel;

    public static RedEnvelopesFragment newInstance() {
        Bundle args = new Bundle();
        RedEnvelopesFragment fragment = new RedEnvelopesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RedEnvelopeViewModel.class);
        mViewModel.init("1272dc0fe06c52383c7a9bdfef33255b940c195b", "1");
        if (mViewModel.getRedEnvelopes() != null) {
            mViewModel.getRedEnvelopes().observe(this, new Observer<Resource<List<RedEnvelope>>>() {
                @Override
                public void onChanged(@Nullable Resource<List<RedEnvelope>> listResource) {
                    Log.d("", "");
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_red_envelopes, container, false);
    }
}
