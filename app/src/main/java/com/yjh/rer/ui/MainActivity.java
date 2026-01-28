package com.yjh.rer.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.jakewharton.rxbinding2.support.v4.view.RxViewPager;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerActivity;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.databinding.ActivityMainBinding;
import com.yjh.rer.ui.chart.ChartFragment;
import com.yjh.rer.ui.list.HomeViewPagerAdapter;
import com.yjh.rer.ui.list.RedEnvelopesFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends BaseDaggerActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private RedEnvelopesFragment mRedEnvelopesFragment;
    private ChartFragment mChartFragment;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // 初始化视图引用
        toolbar = binding.appBarMain.toolbarLayout.toolbar;
        fab = binding.appBarMain.fab;
        drawer = binding.drawerLayout;
        viewPager = binding.appBarMain.viewPager;
        tabLayout = binding.appBarMain.tabLayoutMain;
        navigationView = binding.navView;
        
        initView();
    }

    @Override
    public void initView() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
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

        disposables.add(RxViewPager.pageSelections(viewPager).subscribe(integer -> {
            if (integer == 0) {
                fab.show();
            } else {
                fab.hide();
            }
        }));
        
        fab.setOnClickListener(v -> addRedEnvelopDialog());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
//        MyApplication.getRefWatcher(this).watch(this);
    }

    public void addRedEnvelopDialog() {
        mRedEnvelopesFragment.addRedEnvelopDialog();
    }

    public void showFab() {
        fab.show();
    }

    public void hideFab() {
        fab.hide();
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
