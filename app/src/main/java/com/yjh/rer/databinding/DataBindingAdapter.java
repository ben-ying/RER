package com.yjh.rer.databinding;

import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;

import com.google.android.material.navigation.NavigationView;

@BindingMethods({
        @BindingMethod(
                type = NavigationView.class,
                attribute = "app:onNavigationItemSelected",
                method = "setNavigationItemSelectedListener"
        ),
})

public class DataBindingAdapter {

}
