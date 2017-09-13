
package com.yjh.rer.custom;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;
import com.yjh.rer.R;

import butterknife.ButterKnife;


public class MyMarkerView extends MarkerView {

    private TextView mContentTextView;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        ButterKnife.bind(this);
        mContentTextView = findViewById(R.id.tv_content);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        mContentTextView.setText(String.valueOf(
                Utils.formatNumber(e.getY(), 0, true)));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
