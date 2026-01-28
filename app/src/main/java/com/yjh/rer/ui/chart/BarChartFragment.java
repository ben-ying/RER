package com.yjh.rer.ui.chart;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.yjh.rer.databinding.FragmentBarChartBinding;
import com.yjh.rer.data.room.entity.RedEnvelope;
import com.yjh.rer.util.MoneyFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BarChartFragment extends BaseDaggerFragment {

    private FragmentBarChartBinding binding;
    private BarChart chart;
    private final List<String> yearLabels = new ArrayList<>();

    public static BarChartFragment newInstance() {
        return new BarChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_bar_chart;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentBarChartBinding.inflate(inflater, container, false);
        chart = binding.barChart;
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(7);
        xAxis.setValueFormatter((value, axis) -> {
            int index = (int) value;
            if (index < 0 || index >= yearLabels.size()) {
                return "";
            }
            return yearLabels.get(index);
        });
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
        this.redEnvelopes = redEnvelopes;
        chart.setData(generateYearBarData());
        chart.invalidate();
        if (redEnvelopes.size() > 0) {
            // if data is empty set this, when has data chart always not shown
            chart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
        }
    }

    private BarData generateYearBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        yearLabels.clear();

        Map<String, Double> yearTotals = new TreeMap<>();
        for (RedEnvelope redEnvelope : redEnvelopes) {
            String created = redEnvelope.getCreatedDate();
            String year = created != null && created.length() >= 4
                    ? created.substring(0, 4)
                    : getString(R.string.category_others);
            yearTotals.merge(year, redEnvelope.getMoneyDouble(), Double::sum);
        }

        int index = 0;
        for (Map.Entry<String, Double> entry : yearTotals.entrySet()) {
            yearLabels.add(entry.getKey());
            double money = entry.getValue();
            BarEntry barEntry = new BarEntry(index, (float) money);
            barEntry.setData(entry.getKey() + ": " + MoneyFormatter.format(money));
            entries.add(barEntry);
            index++;
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_date));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);
        return new BarData(sets);
    }
}
