package com.yjh.rer.main.chart;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.custom.MyMarkerView;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BarChartFragment extends BaseDaggerFragment {

    @BindView(R.id.bar_chart)
    BarChart chart;

    public static BarChartFragment newInstance() {
        return  new BarChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bar_chart;
    }

    @Override
    public void initView() {
        chart.getDescription().setEnabled(false);
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);
        chart.setDrawGridBackground(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(7);
        xAxis.setValueFormatter((value, axis) ->
            String.valueOf(redEnvelopes.get((int) value).getMoneyFrom()));
        chart.setDoubleTapToZoomEnabled(false);
    }

    @Override
    public void setData(List<RedEnvelope> redEnvelopes) {
        super.setData(redEnvelopes);
        this.redEnvelopes = redEnvelopes;
        chart.setData(generateBarData());
        chart.invalidate();
        chart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < redEnvelopes.size(); i++) {
            entries.add(new BarEntry(i, redEnvelopes.get(i).getMoneyInt()));
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_date));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);
        return new BarData(sets);
    }
}
