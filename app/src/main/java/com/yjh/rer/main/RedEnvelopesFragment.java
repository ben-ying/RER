package com.yjh.rer.main;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.network.Resource;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RedEnvelopesFragment extends BaseFragment
        implements RedEnvelopeAdapter.RedEnvelopeInterface {

    @BindView(R.id.tv_total) TextView totalTextView;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private RedEnvelopeViewModel mViewModel;
    private RedEnvelopeAdapter mAdapter;
    private Unbinder mUnBinder;
    private Unbinder mDialogUnBinder;

    public static RedEnvelopesFragment newInstance() {
        Bundle args = new Bundle();
        RedEnvelopesFragment fragment = new RedEnvelopesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.load("1");
            }
        });

        RedEnvelopeViewModel.Factory factory = new RedEnvelopeViewModel.Factory(
                getActivity().getApplication(), "1272dc0fe06c52383c7a9bdfef33255b940c195b");
        mViewModel = ViewModelProviders.of(this, factory).get(RedEnvelopeViewModel.class);
        mViewModel.getRedEnvelopes().observe(this, new Observer<Resource<List<RedEnvelope>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<RedEnvelope>> listResource) {
                setData(listResource);
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        mViewModel.load("1");
    }

    private void setData(@Nullable Resource<List<RedEnvelope>> listResource) {
        Log.d("", "");
        swipeRefreshLayout.setRefreshing(false);
        if (listResource != null && listResource.getData() != null) {
            List<RedEnvelope> redEnvelopes = listResource.getData();
            int total = 0;
            for (RedEnvelope redEnvelope : redEnvelopes) {
                total += redEnvelope.getMoneyInt();
            }
            totalTextView.setText(String.format(getString(
                    R.string.red_envelope_total), redEnvelopes.size(), total));
            if (mAdapter == null) {
                mAdapter = new RedEnvelopeAdapter(getActivity(),
                        redEnvelopes, totalTextView, RedEnvelopesFragment.this);
                recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setData(redEnvelopes);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_envelopes, container, false);
        mUnBinder = ButterKnife.bind(this, view);

        return view;
    }

    public void addRedEnvelopDialog() {
        final DialogViews dialogViews = new DialogViews();
        View view = View.inflate(getActivity(), R.layout.dialog_add_red_envelope, null);
        mDialogUnBinder = ButterKnife.bind(dialogViews, view);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setTitle(R.string.red_envelopes)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        swipeRefreshLayout.setRefreshing(true);
                        mViewModel.add(dialogViews.fromEditText.getText().toString(),
                                dialogViews.moneyEditText.getText().toString(),
                                dialogViews.remarkEditText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.show();
    }

    class DialogViews {
        @BindView(R.id.et_from) EditText fromEditText;
        @BindView(R.id.et_money) EditText moneyEditText;
        @BindView(R.id.et_remark) AutoCompleteTextView remarkEditText;
    }

    @Override
    public void delete(int reId) {
        swipeRefreshLayout.setRefreshing(true);
        mViewModel.delete(reId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        mDialogUnBinder.unbind();
    }
}
