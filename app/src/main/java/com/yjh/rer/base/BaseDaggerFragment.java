package com.yjh.rer.base;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjh.rer.injection.Injectable;
import com.yjh.rer.data.room.entity.RedEnvelope;
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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RedEnvelopeViewModel viewModel = new ViewModelProvider(
                this, viewModelFactory).get(RedEnvelopeViewModel.class);
        viewModel.getRedEnvelopes().observe(getViewLifecycleOwner(), this::setData);
    }

    public void setData(List<RedEnvelope> redEnvelopes) {}
}
