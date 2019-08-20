package com.yjh.rer.main.chart;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.custom.MyMarkerView;
import com.yjh.rer.databinding.FragmentBarChartBinding;
import com.yjh.rer.room.entity.RedEnvelope;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class BarChartFragment extends BaseDaggerFragment<FragmentBarChartBinding> {

    static BarChartFragment newInstance() {
        return new BarChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bar_chart;
    }

    @Override
    public void initView() {
        dataBinding.barChart.getDescription().setEnabled(false);
        if (getActivity() != null) {
            MyMarkerView mv = new MyMarkerView(
                    getActivity().getApplicationContext(), R.layout.custom_marker_view);
            mv.setChartView(dataBinding.barChart);
            dataBinding.barChart.setMarker(mv);
            dataBinding.barChart.setDrawGridBackground(false);
            YAxis leftAxis = dataBinding.barChart.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            dataBinding.barChart.getAxisRight().setEnabled(false);
            XAxis xAxis = dataBinding.barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(7);
            xAxis.setValueFormatter((value, axis) ->
                    String.valueOf(redEnvelopes.get((int) value).getMoneyFrom()));
            dataBinding.barChart.setDoubleTapToZoomEnabled(false);
        }
    }

    @Override
    public void setData(List<RedEnvelope> redEnvelopes) {
        super.setData(redEnvelopes);
        this.redEnvelopes = redEnvelopes;
        dataBinding.barChart.setData(generateBarData());
        dataBinding.barChart.invalidate();
        if (redEnvelopes.size() > 0) {
            // if data is empty set this, when has data chart always not shown
            dataBinding.barChart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
        }
    }

    private BarData generateBarData() {
        final DecimalFormat format = new DecimalFormat("###,###,###");
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < redEnvelopes.size(); i++) {
            RedEnvelope redEnvelope = redEnvelopes.get(i);
            BarEntry barEntry = new BarEntry(i, redEnvelope.getMoneyInt());
            barEntry.setData(redEnvelope.getCreatedDate() + "\n"
                    + redEnvelope.getMoneyFrom()
                    + ": " + format.format(redEnvelope.getMoneyInt()));
            entries.add(barEntry);
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_date));
        if (getActivity() != null) {
            ds.setColor(getActivity().getColor(R.color.colorPrimary));
        }
        sets.add(ds);
        return new BarData(sets);
    }
}
