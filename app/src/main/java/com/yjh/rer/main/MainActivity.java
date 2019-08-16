package com.yjh.rer.main;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
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

public class MainActivity extends BaseDaggerActivity<ActivityMainBinding>
        implements NavigationView.OnNavigationItemSelectedListener {

    private RedEnvelopesFragment mRedEnvelopesFragment;
    private ChartFragment mChartFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        dataBinding.setHandler(this);
        setSupportActionBar(dataBinding.appBarMain.toolbar.toolbar);
        setSupportActionBar(dataBinding.appBarMain.toolbar.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                dataBinding.drawerLayout,
                dataBinding.appBarMain.toolbar.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        dataBinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        List<BaseFragment> fragments = new ArrayList<>();
        mRedEnvelopesFragment = RedEnvelopesFragment.newInstance();
        fragments.add(mRedEnvelopesFragment);
        mChartFragment = ChartFragment.newInstance();
        fragments.add(mChartFragment);
        HomeViewPagerAdapter homeViewPagerAdapter = new HomeViewPagerAdapter(
                this, getSupportFragmentManager(), fragments);
        dataBinding.appBarMain.viewPager.setOffscreenPageLimit(1);
        dataBinding.appBarMain.viewPager.setAdapter(homeViewPagerAdapter);
        dataBinding.appBarMain.tabLayoutMain.setupWithViewPager(dataBinding.appBarMain.viewPager);

        RxViewPager.pageSelections(dataBinding.appBarMain.viewPager).subscribe(integer -> {
            if (integer == 0) {
                dataBinding.appBarMain.fab.show();
            } else {
                dataBinding.appBarMain.fab.hide();
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
        if (dataBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            dataBinding.drawerLayout.closeDrawer(GravityCompat.START);
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
        dataBinding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
