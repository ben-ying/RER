package com.yjh.rer.main.chart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.room.entity.RedEnvelope;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PieChartFragment extends BaseFragment implements OnChartValueSelectedListener {
    private static final String RED_ENVELOPE = "red_envelope";

    @BindView(R.id.pie_chart)
    PieChart pieChart;
    private ArrayList<RedEnvelope> mRedEnvelopes;
    private Unbinder mUnBinder;

    public static PieChartFragment newInstance(ArrayList<RedEnvelope> redEnvelopes) {

        Bundle args = new Bundle();
        args.putSerializable(RED_ENVELOPE, redEnvelopes);
        PieChartFragment fragment = new PieChartFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        mUnBinder = ButterKnife.bind(this, view);
        mRedEnvelopes = (ArrayList<RedEnvelope>) getArguments().getSerializable(RED_ENVELOPE);

        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText(getString(R.string.action_sorted_by_category));
        pieChart.setCenterTextColor(getResources().getColor(R.color.default_text_color));
        pieChart.setCenterTextSize(14f);
        pieChart.setDrawEntryLabels(false);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setUsePercentValues(true);
        pieChart.setOnChartValueSelectedListener(this);
        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        updateData(mRedEnvelopes);

        return view;
    }

    @Override
    public void updateData(ArrayList<RedEnvelope> redEnvelopes) {
        mRedEnvelopes = redEnvelopes;
        pieChart.setData(generatePieData());
        pieChart.invalidate();
    }

    protected PieData generatePieData() {
        Map<String, Integer> map = new HashMap<>();
        Map<String, Integer> sortedMap = new HashMap<>();
        sortedMap.put(getString(R.string.category_others), 0);
        int total = 0;
        for (RedEnvelope redEnvelope : mRedEnvelopes) {
            total += redEnvelope.getMoneyInt();
            // if mapValue == null, mapValue = redEnvelope.getMoneyInt(),
            // else mapValue += redEnvelope.getMoneyInt()
            map.merge(redEnvelope.getRemark(), redEnvelope.getMoneyInt(), Integer::sum);
        }

        final int totalMoney = total;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    if (sortedMap.size() <= 5 && (float) entry.getValue() / totalMoney > 0.02) {
                        entries.add(new PieEntry(entry.getValue(),
                                entry.getKey() + "\n(" + new DecimalFormat("###,###,###")
                                        .format(entry.getValue()) + ")"));
                        sortedMap.put(entry.getKey(), entry.getValue());
                    } else {
                        sortedMap.put(getString(R.string.category_others),
                                sortedMap.get(getString(R.string.category_others)) + entry.getValue());
                    }
                });

        if (sortedMap.get(getString(R.string.category_others)) > 0) {
            entries.add(new PieEntry(sortedMap.get(getString(R.string.category_others)),
                    getString(R.string.category_others) +
                            "\n(" + new DecimalFormat("###,###,###").format(
                            sortedMap.get(getString(R.string.category_others))) + ")"));
        }

        PieDataSet pieDataSet = new PieDataSet(
                entries, getString(R.string.action_sorted_by_category));
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueFormatter((value, entry, datasetIndex, viewPortHandler)
                -> (new DecimalFormat("###,###,##0.0").format(value) + "%"));

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.google_red));
        colors.add(getResources().getColor(R.color.google_blue));
        colors.add(getResources().getColor(R.color.google_green));
        colors.add(getResources().getColor(R.color.google_yellow));
        for (int c : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.JOYFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.COLORFUL_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.LIBERTY_COLORS) {
            colors.add(c);
        }
        for (int c : ColorTemplate.PASTEL_COLORS) {
            colors.add(c);
        }
        pieDataSet.setColors(colors);

        return new PieData(pieDataSet);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnBinder.unbind();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        pieChart.setCenterText(((PieEntry) e).getLabel());
    }

    @Override
    public void onNothingSelected() {
        pieChart.setCenterText(getString(R.string.action_sorted_by_category));
    }
}
