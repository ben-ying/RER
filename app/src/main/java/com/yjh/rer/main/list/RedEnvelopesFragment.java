package com.yjh.rer.main.list;


import android.app.AlertDialog;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.databinding.DialogAddRedEnvelopeBinding;
import com.yjh.rer.databinding.FragmentRedEnvelopesBinding;
import com.yjh.rer.main.MainActivity;
import com.yjh.rer.network.Resource;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RedEnvelopesFragment extends BaseDaggerFragment
        implements RedEnvelopeAdapter.RedEnvelopeInterface {

    private static final String TAG =
            RedEnvelopesFragment.class.getSimpleName();

    private static final int SCROLL_UP = 0;
    private static final int SCROLL_DOWN = 1;
    private static final int SCROLL_VIEW_BRING_FRONT = 2;
    private static final String FIRST_OPEN_APP = "first_open_app";

    private FragmentRedEnvelopesBinding binding;
    private TextView totalTextView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;

    private RedEnvelopeViewModel mViewModel;
    private RedEnvelopeAdapter mAdapter;
    private final CompositeDisposable disposables = new CompositeDisposable();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRedEnvelopesBinding.inflate(inflater, container, false);
        
        // 初始化视图引用
        totalTextView = binding.tvTotal;
        recyclerView = binding.recyclerView;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        progressBar = binding.progressLayout.progressBar;
        scrollView = binding.scrollView;
        
        initView();
        
        return binding.getRoot();
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
        disposables.clear();
        binding = null;
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
            redEnvelopes.sort(reverseSorting
                    ? Comparator.comparing(RedEnvelope::getRedEnvelopeId).reversed()
                    : Comparator.comparing(RedEnvelope::getRedEnvelopeId));
            reverseSorting = !reverseSorting;
            requireActivity().invalidateOptionsMenu();
            setAdapter();
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

        disposables.add(createScrollViewObservable()
                .filter(integer -> mScrollViewState != integer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(integer -> {
                    mScrollViewState = integer;
                    switch (integer) {
                        case SCROLL_UP:
                            ((MainActivity) requireActivity()).hideFab();
                            totalTextView.bringToFront();
                            break;
                        case SCROLL_DOWN:
                            ((MainActivity) requireActivity()).showFab();
                            break;
                        case SCROLL_VIEW_BRING_FRONT:
                            swipeRefreshLayout.bringToFront();
                            break;
                    }
                }));
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
        mViewModel = new ViewModelProvider(this, viewModelFactory).get(RedEnvelopeViewModel.class);
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
            double total = 0.0;
            for (RedEnvelope redEnvelope : redEnvelopes) {
                total += redEnvelope.getMoneyDouble();
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
        DialogAddRedEnvelopeBinding dialogBinding = DialogAddRedEnvelopeBinding.inflate(
                LayoutInflater.from(getActivity()));
        
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setTitle(R.string.red_envelopes)
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->  {
            if (isValid(dialogBinding)) {
                dialog.dismiss();
                progressBar.setVisibility(View.VISIBLE);
                mViewModel.add(dialogBinding.etFrom.getText().toString(),
                        dialogBinding.etMoney.getText().toString(),
                        dialogBinding.etRemark.getText().toString());
            }
        });
    }

    private boolean isValid(DialogAddRedEnvelopeBinding dialogBinding) {
        if (TextUtils.isEmpty(dialogBinding.etFrom.getText().toString().trim())) {
            dialogBinding.etFrom.setError(getString(R.string.non_empty_field));
            return false;
        }
        if (TextUtils.isEmpty(dialogBinding.etMoney.getText().toString().trim())) {
            dialogBinding.etMoney.setError(getString(R.string.non_empty_field));
            return false;
        }
        if (TextUtils.isEmpty(dialogBinding.etRemark.getText().toString().trim())) {
            dialogBinding.etRemark.setError(getString(R.string.non_empty_field));
            return false;
        }

        return true;
    }
}
