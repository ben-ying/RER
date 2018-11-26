package com.yjh.rer.main.list;


import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.main.MainActivity;
import com.yjh.rer.network.Resource;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RedEnvelopesFragment extends BaseDaggerFragment
        implements RedEnvelopeAdapter.RedEnvelopeInterface {

    private static final String TAG =
            RedEnvelopesFragment.class.getSimpleName();

    private static final int SCROLL_UP = 0;
    private static final int SCROLL_DOWN = 1;
    private static final int SCROLL_VIEW_BRING_FRONT = 2;
    private static final String FIRST_OPEN_APP = "first_open_app";

    @BindView(R.id.tv_total)
    TextView totalTextView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.scroll_view)
    NestedScrollView scrollView;

    private RedEnvelopeViewModel mViewModel;
    private RedEnvelopeAdapter mAdapter;
    private Disposable mDisposable;
    private int mScrollViewState = -1;
    private boolean reverseSorting;
    private boolean mIsFirstOpen;
    private SharedPreferences mSharedPreferences;

    public static RedEnvelopesFragment newInstance() {
        Bundle args = new Bundle();
        RedEnvelopesFragment fragment = new RedEnvelopesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_red_envelopes;
    }

    @Override
    public void initView() {
        setScrollViewOnChangedListener();

        initRecyclerViewData();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences =
                getActivity().getSharedPreferences(getActivity().getApplicationContext()
                        .getPackageName(), Context.MODE_PRIVATE);
        mIsFirstOpen = mSharedPreferences.getBoolean(FIRST_OPEN_APP, true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.red_envelope_list, menu);
    }

    @Override
    public void delete(int reId) {
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.delete(reId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                sortDataByTime();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_sort).setIcon(
                reverseSorting ? R.mipmap.ic_resort_white_24dp : R.mipmap.ic_sort_white_24dp);
        super.onPrepareOptionsMenu(menu);
    }

    public List<RedEnvelope> getData() {
        return redEnvelopes;
    }

    private void sortDataByTime() {
        if (redEnvelopes != null && mAdapter != null) {
            mDisposable = Observable
                    .create((ObservableEmitter<RedEnvelope> e) -> {
                        for (RedEnvelope redEnvelope : redEnvelopes) {
                            e.onNext(redEnvelope);
                        }
                        e.onComplete();
                    })
                    .toSortedList(reverseSorting ?
                            Comparator.comparing(RedEnvelope::getRedEnvelopeId).reversed() :
                            Comparator.comparing(RedEnvelope::getRedEnvelopeId))
                    .subscribe((res) -> {
                        reverseSorting = !reverseSorting;
                        getActivity().invalidateOptionsMenu();
                        redEnvelopes = res;
                        setAdapter();
                    });
        }
    }

    private void setScrollViewOnChangedListener() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            mViewModel.load("1");
            progressBar.setVisibility(View.VISIBLE);
        });

        mDisposable = createScrollViewObservable()
                .filter(integer -> mScrollViewState != integer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    mScrollViewState = integer;
                    switch (integer) {
                        case SCROLL_UP:
                            ((MainActivity) getActivity()).fab.hide();
                            totalTextView.bringToFront();
                            break;
                        case SCROLL_DOWN:
                            ((MainActivity) getActivity()).fab.show();
                            break;
                        case SCROLL_VIEW_BRING_FRONT:
                            swipeRefreshLayout.bringToFront();
                            break;
                    }
                });
    }

    private Observable<Integer> createScrollViewObservable() {
        return Observable.create((ObservableEmitter<Integer> emitter) -> {
            scrollView.setOnScrollChangeListener((
                    View view, int scrollX, int scrollY, int oldX, int oldY) -> {
                if (scrollY > oldY) {
                    emitter.onNext(SCROLL_UP);
                } else if (scrollY < oldY) {
                    emitter.onNext(SCROLL_DOWN);
                    if (scrollY < 10) {
                        emitter.onNext(SCROLL_VIEW_BRING_FRONT);
                    }
                }
            });
            emitter.setCancellable(() -> {
//                        scrollView.setOnScrollChangeListener(
//                                (NestedScrollView.OnScrollChangeListener) null);
            });
        });
    }

    private void initRecyclerViewData() {
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(RedEnvelopeViewModel.class);
        mViewModel.setToken("83cd0f7a0483db73ce4223658cb61deac6531e85");
        mViewModel.getRedEnvelopesResource().observe(this, this::setData);
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.load("1");
    }

    private void setData(@Nullable Resource<List<RedEnvelope>> listResource) {
        if (listResource != null && listResource.getData() != null) {
            if (listResource.getData().size() > 0) {
                progressBar.setVisibility(View.GONE);
            }
            swipeRefreshLayout.setRefreshing(false);
            redEnvelopes = listResource.getData();
            int total = 0;
            for (RedEnvelope redEnvelope : redEnvelopes) {
                total += redEnvelope.getMoneyInt();
            }
            if (totalTextView.getVisibility() == View.GONE) {
                totalTextView.setVisibility(View.VISIBLE);
            }
            totalTextView.setText(String.format(getString(
                    R.string.red_envelope_total), redEnvelopes.size(), total));
            if (reverseSorting) {
                Collections.reverse(redEnvelopes);
            }
            setAdapter();

            // init chart data when first open app
            if (mIsFirstOpen && redEnvelopes.size() > 0) {
                Fragment fragment = getActivity().getSupportFragmentManager()
                        .findFragmentById(R.id.container);
                if (fragment != null && fragment.isAdded()
                        && fragment instanceof BaseDaggerFragment) {
                    mSharedPreferences.edit().putBoolean(
                            FIRST_OPEN_APP, false).apply();
                    mIsFirstOpen = false;
                    ((BaseDaggerFragment) fragment).setData(redEnvelopes);
                }
            }
        }
    }

    private void setAdapter() {
        if (mAdapter == null) {
            mAdapter = new RedEnvelopeAdapter(getActivity(),
                    redEnvelopes, totalTextView, RedEnvelopesFragment.this);
            recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setData(redEnvelopes);
        }
    }

    public void addRedEnvelopDialog() {
        final DialogViews dialogViews = new DialogViews();
        View view = View.inflate(getActivity(), R.layout.dialog_add_red_envelope, null);
        ButterKnife.bind(dialogViews, view);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setTitle(R.string.red_envelopes)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->  {
            if (isValid(dialogViews)) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                mViewModel.add(dialogViews.fromEditText.getText().toString(),
                        dialogViews.moneyEditText.getText().toString(),
                        dialogViews.remarkEditText.getText().toString());
            }
        });
    }

    private boolean isValid(DialogViews dialogViews) {
        if (TextUtils.isEmpty(dialogViews.fromEditText.getText().toString().trim())) {
            dialogViews.fromEditText.setError(getString(R.string.non_empty_field));
            return false;
        }
        if (TextUtils.isEmpty(dialogViews.moneyEditText.getText().toString().trim())) {
            dialogViews.moneyEditText.setError(getString(R.string.non_empty_field));
            return false;
        }
        if (TextUtils.isEmpty(dialogViews.remarkEditText.getText().toString().trim())) {
            dialogViews.remarkEditText.setError(getString(R.string.non_empty_field));
            return false;
        }

        return true;
    }

    class DialogViews {
        @BindView(R.id.et_from)
        EditText fromEditText;
        @BindView(R.id.et_money)
        EditText moneyEditText;
        @BindView(R.id.et_remark)
        AutoCompleteTextView remarkEditText;
    }
}
