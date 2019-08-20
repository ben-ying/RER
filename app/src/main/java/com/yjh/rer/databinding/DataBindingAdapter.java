package com.yjh.rer.databinding;

import androidx.core.widget.NestedScrollView;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;

@BindingMethods({
        @BindingMethod(
                type = NavigationView.class,
                attribute = "app:onNavigationItemSelected",
                method = "setNavigationItemSelectedListener"
        ),
        @BindingMethod(
                type = SwipeRefreshLayout.class,
                attribute = "app:onRefreshListener",
                method = "setOnRefreshListener"
        ),
        @BindingMethod(
                type = NestedScrollView.class,
                attribute = "app:onScrollChangeListener",
                method = "setOnScrollChangeListener"
        ),
})

public class DataBindingAdapter {

}
