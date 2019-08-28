package com.yjh.rer.databinding;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.core.widget.NestedScrollView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.BindingConversion;
import androidx.databinding.BindingMethod;
import androidx.databinding.BindingMethods;
import androidx.databinding.adapters.ListenerUtil;
import androidx.databinding.adapters.ViewBindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.navigation.NavigationView;
import com.yjh.rer.R;

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
    @BindingAdapter("android:paddingLeft")
    public static void setPaddingLeft(View view, int padding) {
        view.setPadding(padding,
                view.getPaddingTop(),
                view.getPaddingRight(),
                view.getPaddingBottom());
    }

    /*
    <ImageView app:imageUrl="@{venue.imageUrl}" app:error="@{@drawable/venueError}" />
     */
    @BindingAdapter({"imageUrl", "error"})
    public static void loadImage(ImageView view, String url, Drawable error) {
//                Picasso.get().load(url).error(error).into(view);
    }

    @BindingAdapter(value = {"imageUrl", "placeholder"}, requireAll = false)
    public static void setImageUrl(ImageView imageView, String url, Drawable placeHolder) {
        if (url == null) {
            imageView.setImageDrawable(placeHolder);
        } else {
            loadImage(imageView, url, placeHolder);
        }
    }

    /*
    <View android:onLayoutChange="@{() -> handler.layoutChanged()}"/>
     */
    @BindingAdapter("android:onLayoutChange")
    public static void setOnLayoutChangeListener(View view, View.OnLayoutChangeListener oldValue,
                                                 View.OnLayoutChangeListener newValue) {
        if (oldValue != null) {
            view.removeOnLayoutChangeListener(oldValue);
        }
        if (newValue != null) {
            view.addOnLayoutChangeListener(newValue);
        }
    }

    /*
    @TargetApi(VERSION_CODES.HONEYCOMB_MR1)
    public interface OnViewDetachedFromWindow {
        void onViewDetachedFromWindow(View v);
    }

    @TargetApi(VERSION_CODES.HONEYCOMB_MR1)
    public interface OnViewAttachedToWindow {
        void onViewAttachedToWindow(View v);
    }
    */
    @BindingAdapter(value = {"android:onViewDetachedFromWindow", "android:onViewAttachedToWindow"}, requireAll = false)
    public static void setListener(View view, ViewBindingAdapter.OnViewDetachedFromWindow detach, ViewBindingAdapter.OnViewAttachedToWindow attach) {
        View.OnAttachStateChangeListener newListener;
        if (detach == null && attach == null) {
            newListener = null;
        } else {
            newListener = new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    if (attach != null) {
                        attach.onViewAttachedToWindow(v);
                    }
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    if (detach != null) {
                        detach.onViewDetachedFromWindow(v);
                    }
                }
            };
        }

        View.OnAttachStateChangeListener oldListener = ListenerUtil.trackListener(view, newListener,
                R.id.onAttachStateChangeListener);
        if (oldListener != null) {
            view.removeOnAttachStateChangeListener(oldListener);
        }
        if (newListener != null) {
            view.addOnAttachStateChangeListener(newListener);
        }
    }

    /*
    <View
    android:background="@{isError ? @drawable/error : @color/white}"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
    */
    @BindingConversion
    public static ColorDrawable convertColorToDrawable(int color) {
        return new ColorDrawable(color);
    }
}
