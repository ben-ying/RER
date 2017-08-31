package com.yjh.rer.chart;

import android.content.Context;
import android.view.View;

import com.github.mikephil.charting.data.ChartData;


public abstract class ChartItem {
    
    protected static final int TYPE_BAR_CHART = 0;
    protected static final int TYPE_LINE_CHART = 1;
    protected static final int TYPE_PIE_CHART = 2;
    
    protected ChartData<?> mChartData;
    
    public ChartItem(ChartData<?> cd) {
        this.mChartData = cd;      
    }
    
    public abstract int getItemType();
    
    public abstract View getView(int position, View convertView, Context c);
}
