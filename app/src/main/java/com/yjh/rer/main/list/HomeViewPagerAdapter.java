package com.yjh.rer.main.list;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;

import java.util.List;

public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> mFragments;
    private Context mContext;

    public HomeViewPagerAdapter(Context context, FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.mContext = context;
        this.mFragments = fragments;
    }

    @Override
    public BaseFragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getApplicationContext().getResources()
                .getStringArray(R.array.fragment_options)[position];
    }
}
