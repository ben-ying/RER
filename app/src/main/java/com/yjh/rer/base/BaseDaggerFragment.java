package com.yjh.rer.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.yjh.rer.injection.Injectable;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.List;

import javax.inject.Inject;

public abstract class BaseDaggerFragment<T extends ViewDataBinding> extends BaseFragment<T>
        implements Injectable {

    @Inject
    protected ViewModelProvider.Factory viewModelFactory;

    protected List<RedEnvelope> redEnvelopes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        RedEnvelopeViewModel viewModel = ViewModelProviders.of(
                this, viewModelFactory).get(RedEnvelopeViewModel.class);
        viewModel.getRedEnvelopes().observe(getViewLifecycleOwner(), this::setData);

        return view;
    }

    public void setData(List<RedEnvelope> redEnvelopes) {}
}
