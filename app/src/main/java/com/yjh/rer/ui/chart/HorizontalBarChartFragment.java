package com.yjh.rer.ui.chart;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.custom.MyMarkerView;
import com.yjh.rer.databinding.FragmentHorizontalBarChartBinding;
import com.yjh.rer.data.room.entity.RedEnvelope;
import com.yjh.rer.util.MoneyFormatter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Comparator;

public class HorizontalBarChartFragment extends BaseDaggerFragment
        implements OnChartValueSelectedListener {

    private FragmentHorizontalBarChartBinding binding;
    private HorizontalBarChart chart;
    private final List<String> moneyFromLabels = new ArrayList<>();

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
        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        chart.getAxisRight().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((value, axis) -> {
            int index = (int) value;
            if (index < 0 || index >= moneyFromLabels.size()) {
                return "";
            }
            return moneyFromLabels.get(index);
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
        chart.setData(generateMoneyFromBarData());
        chart.invalidate();
        chart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
        chart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
    }

    private BarData generateMoneyFromBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        moneyFromLabels.clear();

        Map<String, Double> totals = new LinkedHashMap<>();
        for (RedEnvelope redEnvelope : redEnvelopes) {
            String key = redEnvelope.getMoneyFrom();
            if (key == null || key.trim().isEmpty()) {
                key = getString(R.string.category_others);
            }
            totals.merge(key, redEnvelope.getMoneyDouble(), Double::sum);
        }

        List<Map.Entry<String, Double>> sortedTotals = new ArrayList<>(totals.entrySet());
        sortedTotals.sort(Map.Entry.<String, Double>comparingByValue().reversed());

        int index = 0;
        for (Map.Entry<String, Double> entry : sortedTotals) {
            moneyFromLabels.add(entry.getKey());
            double money = entry.getValue();
            BarEntry barEntry = new BarEntry(index, (float) money);
            barEntry.setData(entry.getKey() + ": " + MoneyFormatter.format(money));
            entries.add(barEntry);
            index++;
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_amount));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);

        return new BarData(sets);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int index = (int) e.getX();
        if (index < 0 || index >= moneyFromLabels.size()) {
            return;
        }
        String label = moneyFromLabels.get(index);
        showMoneyFromDialog(label);
    }

    @Override
    public void onNothingSelected() {
    }

    private void showMoneyFromDialog(String moneyFrom) {
        if (redEnvelopes == null || redEnvelopes.isEmpty()) {
            return;
        }
        List<RedEnvelope> matched = new ArrayList<>();
        for (RedEnvelope redEnvelope : redEnvelopes) {
            String from = redEnvelope.getMoneyFrom();
            if ((from == null || from.trim().isEmpty())
                    && moneyFrom.equals(getString(R.string.category_others))) {
                matched.add(redEnvelope);
            } else if (moneyFrom.equals(from)) {
                matched.add(redEnvelope);
            }
        }

        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new MoneyFromListAdapter(matched));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.red_envelopes) + " - " + moneyFrom)
                .setView(recyclerView)
                .setPositiveButton(R.string.ok, null)
                .create();
        dialog.show();
    }

    private static final class MoneyFromListAdapter
            extends RecyclerView.Adapter<MoneyFromListAdapter.ViewHolder> {
        private final List<RedEnvelope> items;

        private MoneyFromListAdapter(List<RedEnvelope> items) {
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
