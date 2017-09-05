package com.yjh.rer.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.ArrayList;

public class ChartFragment extends BaseFragment {

    public static final int CHART_PAGE_SIZE = 8;
    private ArrayList<RedEnvelope> mRedEnvelopes;
    private BaseFragment mCurrentFragment;

    public static ChartFragment newInstance() {
        Bundle args = new Bundle();
        ChartFragment fragment = new ChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chart, container, false);
        mCurrentFragment = HorizontalBarChartFragment.newInstance(mRedEnvelopes);
        replaceFragment(mCurrentFragment);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.chart, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_by_amount:
                mCurrentFragment = HorizontalBarChartFragment.newInstance(mRedEnvelopes);
                replaceFragment(mCurrentFragment);
                break;
            case R.id.action_list_all:
                mCurrentFragment = BarChartFragment.newInstance(mRedEnvelopes);
                replaceFragment(mCurrentFragment);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setChartData(ArrayList<RedEnvelope> redEnvelopes) {
        mRedEnvelopes = redEnvelopes;
        if (mCurrentFragment != null && mCurrentFragment.isAdded()) {
            mCurrentFragment.updateData(mRedEnvelopes);
        }
    }
}
