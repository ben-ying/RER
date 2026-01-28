package com.yjh.rer.ui.chart;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.custom.MyMarkerView;
import com.yjh.rer.databinding.FragmentHorizontalBarChartBinding;
import com.yjh.rer.room.entity.RedEnvelope;
import com.yjh.rer.util.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class HorizontalBarChartFragment extends BaseDaggerFragment {

    private FragmentHorizontalBarChartBinding binding;
    private HorizontalBarChart chart;

    public static HorizontalBarChartFragment newInstance() {
        return new HorizontalBarChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_horizontal_bar_chart;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHorizontalBarChartBinding.inflate(inflater, container, false);
        chart = binding.horizontalBarChart;
        initView();
        return binding.getRoot();
    }

    @Override
    public void initView() {
        chart.getDescription().setEnabled(false);
        MyMarkerView mv = new MyMarkerView(
                getActivity().getApplicationContext(), R.layout.custom_marker_view);
        mv.setChartView(chart);
        chart.setMarker(mv);
        chart.setDrawGridBackground(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((value, axis) ->
                String.valueOf(redEnvelopes.get((int) value).getMoneyFrom()));
        chart.setDoubleTapToZoomEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void setData(List<RedEnvelope> redEnvelopes) {
        super.setData(redEnvelopes);
        sortByAmount(redEnvelopes);
        chart.setData(generateBarData());
        chart.invalidate();
        chart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
        chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
    }

    private void sortByAmount(final List<RedEnvelope> redEnvelopes) {
        redEnvelopes.sort(Comparator.comparing(RedEnvelope::getMoneyDouble));
        this.redEnvelopes = redEnvelopes;
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < redEnvelopes.size(); i++) {
            RedEnvelope redEnvelope = redEnvelopes.get(i);
            double money = redEnvelope.getMoneyDouble();
            BarEntry barEntry = new BarEntry(i, (float) money);
            barEntry.setData(redEnvelope.getCreatedDate() + "\n"
                    + redEnvelope.getMoneyFrom()
                    + ": " + MoneyFormatter.format(money));
            entries.add(barEntry);
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_amount));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);

        return new BarData(sets);
    }
}
