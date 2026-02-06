package com.yjh.rer.ui.chart;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.custom.MyMarkerView;
import com.yjh.rer.databinding.FragmentBarChartBinding;
import com.yjh.rer.data.room.entity.RedEnvelope;
import com.yjh.rer.util.MoneyFormatter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BarChartFragment extends BaseDaggerFragment implements OnChartValueSelectedListener {

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
        chart.setOnChartValueSelectedListener(this);
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
        this.redEnvelopes.sort(Comparator.comparing(RedEnvelope::getCreated).reversed());
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

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int index = (int) e.getX();
        if (index < 0 || index >= yearLabels.size()) {
            return;
        }
        String year = yearLabels.get(index);
        showYearDialog(year);
    }

    @Override
    public void onNothingSelected() {
    }

    private void showYearDialog(String year) {
        if (redEnvelopes == null || redEnvelopes.isEmpty()) {
            return;
        }
        List<RedEnvelope> matched = new ArrayList<>();
        for (RedEnvelope redEnvelope : redEnvelopes) {
            String created = redEnvelope.getCreatedDate();
            String y = created != null && created.length() >= 4
                    ? created.substring(0, 4)
                    : getString(R.string.category_others);
            if (year.equals(y)) {
                matched.add(redEnvelope);
            }
        }

        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new YearListAdapter(matched));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.red_envelopes) + " - " + year)
                .setView(recyclerView)
                .setPositiveButton(R.string.ok, null)
                .create();
        dialog.show();
    }

    private static final class YearListAdapter
            extends RecyclerView.Adapter<YearListAdapter.ViewHolder> {
        private final List<RedEnvelope> items;

        private YearListAdapter(List<RedEnvelope> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            RedEnvelope redEnvelope = items.get(position);
            String money = MoneyFormatter.format(redEnvelope.getMoneyDouble());
            String from = redEnvelope.getMoneyFrom();
            if (from == null || from.trim().isEmpty()) {
                from = holder.itemView.getContext().getString(R.string.category_others);
            }
            holder.title.setText(from + "  " + money);
            String remark = redEnvelope.getRemark() == null ? "" : redEnvelope.getRemark();
            holder.subtitle.setText(redEnvelope.getCreatedDate() + "  " + remark);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static final class ViewHolder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView subtitle;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.text1);
                subtitle = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
