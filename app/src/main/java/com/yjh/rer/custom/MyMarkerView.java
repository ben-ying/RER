
package com.yjh.rer.custom;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.yjh.rer.R;
import com.yjh.rer.databinding.CustomMarkerViewBinding;


public class MyMarkerView extends MarkerView {

    private CustomMarkerViewBinding mBinding;

    public MyMarkerView(Context context){
        super(context, 0);
    }

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        removeAllViews();
        mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), layoutResource, this, true);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        mBinding.tvContent.setText(e.getData().toString());

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
