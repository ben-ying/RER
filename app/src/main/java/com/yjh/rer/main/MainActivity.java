package com.yjh.rer.main;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerActivity;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.databinding.ActivityMainBinding;
import com.yjh.rer.main.chart.ChartFragment;
import com.yjh.rer.main.list.HomeViewPagerAdapter;
import com.yjh.rer.main.list.RedEnvelopesFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseDaggerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;
    public ActivityMainBinding mBinding;

    private RedEnvelopesFragment mRedEnvelopesFragment;
    private ChartFragment mChartFragment;

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();
        mBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        mBinding.setHandler(this);
        setSupportActionBar(mBinding.appBarMain.toolbar.toolbar);
        setSupportActionBar(mBinding.appBarMain.toolbar.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mBinding.drawerLayout,
                mBinding.appBarMain.toolbar.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mBinding.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void initView() {
        List<BaseFragment> fragments = new ArrayList<>();
        mRedEnvelopesFragment = RedEnvelopesFragment.newInstance();
        fragments.add(mRedEnvelopesFragment);
        mChartFragment = ChartFragment.newInstance();
        fragments.add(mChartFragment);
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(homeViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        RxViewPager.pageSelections(viewPager).subscribe(integer -> {
            if (integer == 0) {
                mBinding.appBarMain.fab.show();
            } else {
                mBinding.appBarMain.fab.hide();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.getRefWatcher(this).watch(this);
    }

    public void addRedEnvelopDialog() {
        mRedEnvelopesFragment.addRedEnvelopDialog();
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
