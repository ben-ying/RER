package com.yjh.rer.main;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

public class RedEnvelopesFragment extends BaseFragment
        implements View.OnClickListener, RedEnvelopeAdapter.RedEnvelopeInterface {

    private RedEnvelopeViewModel mViewModel;
    private List<RedEnvelope> mRedEnvelopes;
    private RecyclerView mRecyclerView;
    private TextView mTotalTextView;
    private FloatingActionButton mFab;
    private RedEnvelopeAdapter mAdapter;
    private boolean mShowFab = true;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE && mAdapter.getItemCount() > 0) {
                    if (mShowFab && mFab != null) {
                        mFab.show();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mShowFab = false;
                    if (mFab != null) {
                        mFab.hide();
                    }
                } else {
                    mShowFab = true;
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                Log.d("", "");
                mSwipeRefreshLayout.setRefreshing(false);
                if (listResource != null && listResource.getData() != null) {
                    mRedEnvelopes = listResource.getData();
                    int total = 0;
                    for (RedEnvelope redEnvelope : mRedEnvelopes) {
                        total += redEnvelope.getMoneyInt();
                    }
                    mTotalTextView.setText(String.format(getString(
                            R.string.red_envelope_total), mRedEnvelopes.size(), total));
                    if (mAdapter == null) {
                        mAdapter = new RedEnvelopeAdapter(getActivity(),
                                mRedEnvelopes, mTotalTextView, RedEnvelopesFragment.this);
                        mRecyclerView.setAdapter(mAdapter);
                    } else {
                        mAdapter.setData(mRedEnvelopes);
                    }
                }
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        mViewModel.load("1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_envelopes, container, false);
        mFab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mTotalTextView = (TextView) view.findViewById(R.id.tv_total);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(true);

        return view;
    }

    private void addRedEnvelopDialog() {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_red_envelope, null);
        final EditText fromEditText = (EditText) view.findViewById(R.id.et_from);
        final EditText moneyEditText = (EditText) view.findViewById(R.id.et_money);
        final AutoCompleteTextView remarkEditText = (AutoCompleteTextView) view.findViewById(R.id.et_remark);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setTitle(R.string.red_envelopes)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        mViewModel.add(fromEditText.getText().toString(),
                                moneyEditText.getText().toString(),
                                remarkEditText.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                addRedEnvelopDialog();
                break;
        }
    }

    @Override
    public void delete(int reId) {
        mSwipeRefreshLayout.setRefreshing(true);
        mViewModel.delete(reId);
    }
}
