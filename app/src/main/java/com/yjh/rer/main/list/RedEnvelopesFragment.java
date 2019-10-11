package com.yjh.rer.main.list;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.databinding.DialogAddRedEnvelopeBinding;
import com.yjh.rer.databinding.FragmentRedEnvelopesBinding;
import com.yjh.rer.model.ListResponseResult;
import com.yjh.rer.network.NetworkState;
import com.yjh.rer.network.Resource;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;

public class RedEnvelopesFragment extends BaseDaggerFragment<FragmentRedEnvelopesBinding>
        implements RedEnvelopeAdapter.RedEnvelopeInterface {

    private static final String TAG = RedEnvelopesFragment.class.getSimpleName();

    private static final int SCROLL_UP = 0;
    private static final int SCROLL_DOWN = 1;
    private static final int SCROLL_VIEW_BRING_FRONT = 2;
    private static final String FIRST_OPEN_APP = "first_open_app";

    private RedEnvelopeViewModel mViewModel;
    private RedEnvelopeAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private Disposable mDisposable;
    private int mScrollViewState = -1;
    private boolean reverseSorting;
    private boolean mIsFirstOpen;
    private SharedPreferences mSharedPreferences;
    private ObservableEmitter<Integer> mEmitter;

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
        dataBinding.setHandler(this);

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
        dataBinding.progressLayout.progressBar.setVisibility(View.VISIBLE);
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
                        if (getActivity() != null) {
                            reverseSorting = !reverseSorting;
                            getActivity().invalidateOptionsMenu();
                            redEnvelopes = res;
                            Toast.makeText(getContext(), reverseSorting ?
                                    R.string.action_sorted_by_reverse_date :
                                    R.string.action_sorted_by_date, Toast.LENGTH_SHORT).show();
//                            setAdapter();
                        }
                    });
        }
    }

    private void setScrollViewOnChangedListener() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        dataBinding.recyclerView.setLayoutManager(mLayoutManager);
        dataBinding.recyclerView.setHasFixedSize(false);
        dataBinding.recyclerView.setNestedScrollingEnabled(false);
        dataBinding.swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);

//        mDisposable = createScrollViewObservable()
//                .filter(integer -> mScrollViewState != integer)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(integer -> {
//                    mScrollViewState = integer;
//                    switch (integer) {
//                        case SCROLL_UP:
//                            if (getActivity() != null) {
//                                ((MainActivity) getActivity()).dataBinding.appBarMain.fab.hide();
//                                dataBinding.tvTotal.bringToFront();
//                            }
//                            break;
//                        case SCROLL_DOWN:
//                            if (getActivity() != null) {
//                                ((MainActivity) getActivity()).dataBinding.appBarMain.fab.show();
//                            }
//                            break;
//                        case SCROLL_VIEW_BRING_FRONT:
//                            dataBinding.swipeRefreshLayout.bringToFront();
//                            break;
//                    }
//                });
    }

    public void onRefreshListener() {
        mViewModel.load(RedEnvelopeViewModel.TYPE_REFRESH);
    }

//    public void onScrollChangeListener(View view, int scrollX, int scrollY, int oldX, int oldY) {
//        if (scrollY > oldY) {
//            mEmitter.onNext(SCROLL_UP);
//        } else if (scrollY < oldY) {
//            mEmitter.onNext(SCROLL_DOWN);
//            if (scrollY < 10) {
//                mEmitter.onNext(SCROLL_VIEW_BRING_FRONT);
//            }
//        }
//    }

//    private Observable<Integer> createScrollViewObservable() {
//        return Observable.create((ObservableEmitter<Integer> emitter) -> {
//            dataBinding.scrollView.setOnScrollChangeListener((
//                    View view, int scrollX, int scrollY, int oldX, int oldY) -> {
//                mEmitter = emitter;
//                if (scrollY > oldY) {
//                    emitter.onNext(SCROLL_UP);
//                } else if (scrollY < oldY) {
//                    emitter.onNext(SCROLL_DOWN);
//                    if (scrollY < 10) {
//                        emitter.onNext(SCROLL_VIEW_BRING_FRONT);
//                    }
//                }
//            });
//            emitter.setCancellable(() -> {
////                        scrollView.setOnScrollChangeListener(
////                                (NestedScrollView.OnScrollChangeListener) null);
//            });
//        });
//    }

    private void initRecyclerViewData() {
        dataBinding.tvTotal.setVisibility(View.GONE);
        mAdapter = new RedEnvelopeAdapter(this, getContext());
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(RedEnvelopeViewModel.class);
        mViewModel.setToken("83cd0f7a0483db73ce4223658cb61deac6531e85");
        mViewModel.getRedEnvelopeList().observe(getViewLifecycleOwner(), this::submitList);
        mViewModel.getOperatingItem().observe(getViewLifecycleOwner(), this::operateResult);
        mViewModel.getNetworkErrors().observe(getViewLifecycleOwner(), this::handleNetworkResult);
        dataBinding.recyclerView.setAdapter(mAdapter);
        mViewModel.load(RedEnvelopeViewModel.TYPE_LOAD);
    }

    private void submitList(PagedList<RedEnvelope> list) {
        setLabel();
        mAdapter.submitList(list);
        dataBinding.progressLayout.progressBar.setVisibility(View.GONE);
        dataBinding.swipeRefreshLayout.setRefreshing(false);
    }

    private void operateResult(@Nullable Resource<RedEnvelope> listResource) {
        if (listResource != null) {
            if (listResource.getStatus() == NetworkState.Status.SUCCESS){
                dataBinding.progressLayout.progressBar.setVisibility(View.GONE);
                dataBinding.swipeRefreshLayout.setRefreshing(false);
                setLabel();
                if (listResource.getData() != null) {
                    Log.d(TAG, "Item added");
                    new Handler().postDelayed(() -> {
                        Toast.makeText(getContext(), R.string.item_added, Toast.LENGTH_SHORT).show();
                        dataBinding.recyclerView.smoothScrollToPosition(0);
                    }, 100);
                } else {
                    Log.d(TAG, "Item deleted");
                    Toast.makeText(getContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                }
            } else if (listResource.getStatus() == NetworkState.Status.ERROR) {
                dataBinding.progressLayout.progressBar.setVisibility(View.GONE);
                dataBinding.swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "OperateResult ERROR: " + listResource.getMessage());
                Toast.makeText(getContext(), R.string.operate_error, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setLabel() {
        ListResponseResult<List<RedEnvelope>> responseResult = mViewModel.getResult();
        if (responseResult != null && responseResult.getCount() != 0) {
            dataBinding.tvTotal.setVisibility(View.VISIBLE);
            dataBinding.tvTotal.setText(String.format(getString(R.string.red_envelope_total),
                    responseResult.getCount(), responseResult.getTotal()));
        }

    }

    private void handleNetworkResult(String s) {
        Log.e(TAG, "Network ERROR: " + s);
        Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_LONG).show();
        dataBinding.progressLayout.progressBar.setVisibility(View.GONE);
        dataBinding.swipeRefreshLayout.setRefreshing(false);
    }

    public void addRedEnvelopDialog() {
        View view = View.inflate(getActivity(), R.layout.dialog_add_red_envelope, null);
        DialogAddRedEnvelopeBinding binding = DataBindingUtil.bind(view);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setTitle(R.string.red_envelopes)
                .setView(view)
                .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setCancelable(true);
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if (binding != null && isValid(binding)) {
                dialog.dismiss();
                dataBinding.progressLayout.progressBar.setVisibility(View.VISIBLE);
                mViewModel.add(binding.etFrom.getText().toString(),
                        binding.etMoney.getText().toString(),
                        binding.etRemark.getText().toString());
            }
        });
    }

    private boolean isValid(DialogAddRedEnvelopeBinding binding) {
        if (TextUtils.isEmpty(binding.etFrom.getText().toString().trim())) {
            binding.etFrom.setError(getString(R.string.non_empty_field));
            return false;
        }
        if (TextUtils.isEmpty(binding.etMoney.getText().toString().trim())) {
            binding.etMoney.setError(getString(R.string.non_empty_field));
            return false;
        }
        if (TextUtils.isEmpty(binding.etRemark.getText().toString().trim())) {
            binding.etRemark.setError(getString(R.string.non_empty_field));
            return false;
        }

        return true;
    }
}
