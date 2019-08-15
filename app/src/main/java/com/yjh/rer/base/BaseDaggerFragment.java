package com.yjh.rer.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.yjh.rer.injection.Injectable;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.List;

import javax.inject.Inject;

public abstract class BaseDaggerFragment extends BaseFragment
        implements Injectable {

    @Inject
    public ViewModelProvider.Factory viewModelFactory;

    public List<RedEnvelope> redEnvelopes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        RedEnvelopeViewModel viewModel = ViewModelProviders.of(
                this, viewModelFactory).get(RedEnvelopeViewModel.class);
        viewModel.getRedEnvelopes().observe(this, this::setData);

        return view;
    }

    public void setData(List<RedEnvelope> redEnvelopes) {}
}
