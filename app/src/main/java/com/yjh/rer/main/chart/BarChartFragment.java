package com.yjh.rer.main.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.custom.MyMarkerView;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.ArrayList;

public class BarChartFragment extends BaseFragment {

    private static final String RED_ENVELOPE = "red_envelope";
    private ArrayList<RedEnvelope> mRedEnvelopes;
    private BarChart mChart;

    public static BarChartFragment newInstance(ArrayList<RedEnvelope> redEnvelopes) {

        Bundle args = new Bundle();
        args.putSerializable(RED_ENVELOPE, redEnvelopes);
        BarChartFragment fragment = new BarChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bar_chart, container, false);
        mChart = v.findViewById(R.id.bar_chart);
        mRedEnvelopes = (ArrayList<RedEnvelope>) getArguments().getSerializable(RED_ENVELOPE);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mChart.getDescription().setEnabled(false);
        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);
        mChart.setDrawGridBackground(false);
        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        mChart.getAxisRight().setEnabled(false);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(7);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf(mRedEnvelopes.get((int) value).getMoneyFrom());
            }
        });
        mChart.setDoubleTapToZoomEnabled(false);
        updateData(mRedEnvelopes);
    }

    @Override
    public void updateData(ArrayList<RedEnvelope> redEnvelopes) {
        if (redEnvelopes != null) {
            this.mRedEnvelopes = redEnvelopes;
            mChart.setData(generateBarData());
            mChart.invalidate();
            mChart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
        }
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> xValues = new ArrayList<>();

        for (int i = 0; i < mRedEnvelopes.size(); i++) {
            entries.add(new BarEntry(i, mRedEnvelopes.get(i).getMoneyInt()));
            xValues.add(mRedEnvelopes.get(i).getMoneyFrom());
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_date));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);
        return new BarData(sets);
    }
}
