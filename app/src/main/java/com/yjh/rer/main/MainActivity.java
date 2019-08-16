package com.yjh.rer.main;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
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

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    public FloatingActionButton fab;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.tab_layout_main)
    TabLayout tabLayout;

    private RedEnvelopesFragment mRedEnvelopesFragment;
    private ChartFragment mChartFragment;

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void setDataBinding() {
        super.setDataBinding();
        ActivityMainBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);
        binding.setHandler(this);
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
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
                fab.show();
            } else {
                fab.hide();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        MyApplication.getRefWatcher(this).watch(this);
    }

    @OnClick(R.id.fab)
    public void addRedEnvelopDialog() {
        mRedEnvelopesFragment.addRedEnvelopDialog();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
