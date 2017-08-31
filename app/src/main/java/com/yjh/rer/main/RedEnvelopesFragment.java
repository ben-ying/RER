package com.yjh.rer.main;


import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.network.Resource;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.viewmodel.RedEnvelopeViewModel;

import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

public class RedEnvelopesFragment extends BaseFragment
        implements RedEnvelopeAdapter.RedEnvelopeInterface {

    private static final String TAG = RedEnvelopesFragment.class.getSimpleName();

    private static final int SCROLL_UP = 0;
    private static final int SCROLL_DOWN = 1;
    private static final int SCROLL_VIEW_BRING_FRONT = 2;

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
    private Unbinder mUnBinder;
    private Disposable mDisposable;
    private int mScrollViewState = -1;
    private boolean reverseSorting;
    private List<RedEnvelope> mRedEnvelopes;

    public static RedEnvelopesFragment newInstance() {
        Bundle args = new Bundle();
        RedEnvelopesFragment fragment = new RedEnvelopesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setScrollViewOnChangedListener();

        initRecyclerViewData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_red_envelopes, container, false);
        mUnBinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.red_envelope_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                sort();
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

    private void sort() {
        if (mRedEnvelopes != null && mAdapter != null) {
            mDisposable = Observable.create(new ObservableOnSubscribe<RedEnvelope>() {
                @Override
                public void subscribe(ObservableEmitter<RedEnvelope> e) throws Exception {
                    for (RedEnvelope redEnvelope : mRedEnvelopes) {
                        e.onNext(redEnvelope);
                    }
                    e.onComplete();
                }
            }).toSortedList(new Comparator<RedEnvelope>() {
                @Override
                public int compare(RedEnvelope redEnvelope, RedEnvelope t1) {
                    return reverseSorting ? t1.getRedEnvelopeId() - redEnvelope.getRedEnvelopeId()
                            : redEnvelope.getRedEnvelopeId() - t1.getRedEnvelopeId();
                }
            }).subscribe(new Consumer<List<RedEnvelope>>() {
                @Override
                public void accept(List<RedEnvelope> redEnvelopes) throws Exception {
                    mRedEnvelopes = redEnvelopes;
                    reverseSorting = !reverseSorting;
                    getActivity().invalidateOptionsMenu();
                    mAdapter.setData(mRedEnvelopes);
                }
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
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mViewModel.load("1");
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        mDisposable = createScrollViewObservable()
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return mScrollViewState != integer;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mScrollViewState = integer;
                        Log.d(TAG, "state: " + mScrollViewState);
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
                    }
                });
    }

    private Observable<Integer> createScrollViewObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(final ObservableEmitter<Integer> emitter) throws Exception {
                scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View view, int scrollX, int scrollY, int oldX, int oldY) {
                        if (scrollY > oldY) {
                            emitter.onNext(SCROLL_UP);
                        } else if (scrollY < oldY) {
                            emitter.onNext(SCROLL_DOWN);
                            if (scrollY < 10) {
                                emitter.onNext(SCROLL_VIEW_BRING_FRONT);
                            }
                        }
                    }
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        scrollView.setOnScrollChangeListener(
                                (NestedScrollView.OnScrollChangeListener) null);
                    }
                });
            }
        });
    }

    private void initRecyclerViewData() {
        RedEnvelopeViewModel.Factory factory = new RedEnvelopeViewModel.Factory(
                getActivity().getApplication(), "1272dc0fe06c52383c7a9bdfef33255b940c195b");
        mViewModel = ViewModelProviders.of(this, factory).get(RedEnvelopeViewModel.class);
        mViewModel.getRedEnvelopes().observe(this, new Observer<Resource<List<RedEnvelope>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<RedEnvelope>> listResource) {
                setData(listResource);
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.load("1");
    }

    private void setData(@Nullable Resource<List<RedEnvelope>> listResource) {
        if (listResource != null && listResource.getData() != null) {
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            mRedEnvelopes = listResource.getData();
            int total = 0;
            for (RedEnvelope redEnvelope : mRedEnvelopes) {
                total += redEnvelope.getMoneyInt();
            }
            totalTextView.setText(String.format(getString(
                    R.string.red_envelope_total), mRedEnvelopes.size(), total));
            if (mAdapter == null) {
                mAdapter = new RedEnvelopeAdapter(getActivity(),
                        mRedEnvelopes, totalTextView, RedEnvelopesFragment.this);
                recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.setData(mRedEnvelopes);
            }
        }
    }

    public void addRedEnvelopDialog() {
        final DialogViews dialogViews = new DialogViews();
        View view = View.inflate(getActivity(), R.layout.dialog_add_red_envelope, null);
        ButterKnife.bind(dialogViews, view);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme)
                .setTitle(R.string.red_envelopes)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBar.setVisibility(View.VISIBLE);
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
        @BindView(R.id.et_from)
        EditText fromEditText;
        @BindView(R.id.et_money)
        EditText moneyEditText;
        @BindView(R.id.et_remark)
        AutoCompleteTextView remarkEditText;
    }

    @Override
    public void delete(int reId) {
        progressBar.setVisibility(View.VISIBLE);
        mViewModel.delete(reId);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }
}
