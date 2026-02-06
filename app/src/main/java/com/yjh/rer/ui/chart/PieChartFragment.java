package com.yjh.rer.ui.chart;

import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.databinding.FragmentPieChartBinding;
import com.yjh.rer.data.room.entity.RedEnvelope;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class PieChartFragment extends BaseDaggerFragment implements OnChartValueSelectedListener {
    private FragmentPieChartBinding binding;
    private PieChart pieChart;
    private final Set<String> shownCategories = new HashSet<>();

    public static PieChartFragment newInstance() {
        return new PieChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_pie_chart;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPieChartBinding.inflate(inflater, container, false);
        pieChart = binding.pieChart;
        initView();
        return binding.getRoot();
    }

    @Override
    public void initView() {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void setData(List<RedEnvelope> redEnvelopes) {
        this.redEnvelopes = redEnvelopes;
        this.redEnvelopes.sort(Comparator.comparing(RedEnvelope::getCreated).reversed());
        pieChart.setData(generatePieData());
        pieChart.invalidate();
    }

    protected PieData generatePieData() {
        final DecimalFormat format = new DecimalFormat("###,###,###.##");
        Map<String, Double> map = new HashMap<>();
        Map<String, Double> sortedMap = new HashMap<>();
        sortedMap.put(getString(R.string.category_others), 0.0);
        shownCategories.clear();
        double total = 0.0;
        for (RedEnvelope redEnvelope : redEnvelopes) {
            double money = redEnvelope.getMoneyDouble();
            total += money;
            // if mapValue == null, mapValue = redEnvelope.getMoneyDouble(),
            // else mapValue += redEnvelope.getMoneyDouble()
            map.merge(redEnvelope.getRemark(), money, Double::sum);
        }

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.google_red));
        colors.add(getResources().getColor(R.color.google_blue));
        colors.add(getResources().getColor(R.color.colorPrimary));
        colors.add(getResources().getColor(R.color.google_green));
        colors.add(getResources().getColor(R.color.colorPrimaryDark));
        colors.add(getResources().getColor(R.color.google_yellow));

        final double totalMoney = total;
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        map.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .forEach(entry -> {
                    if (sortedMap.size() <= colors.size() - 1
                            && entry.getValue() / totalMoney > 0.02) {
                        String key = entry.getKey();
                        PieEntry pieEntry = new PieEntry(entry.getValue().floatValue(),
                                key + "\n: " + format.format(entry.getValue()));
                        pieEntry.setData(key);
                        entries.add(pieEntry);
                        sortedMap.put(entry.getKey(), entry.getValue());
                        shownCategories.add(entry.getKey());
                    } else {
                        sortedMap.put(getString(R.string.category_others),
                                sortedMap.get(getString(R.string.category_others)) + entry.getValue());
                    }
                });

        if (sortedMap.get(getString(R.string.category_others)) > 0.0) {
            String others = getString(R.string.category_others);
            PieEntry pieEntry = new PieEntry(sortedMap.get(others).floatValue(),
                    others + "\n: " + format.format(sortedMap.get(others)));
            pieEntry.setData(others);
            entries.add(pieEntry);
        }

        PieDataSet pieDataSet = new PieDataSet(
                entries, getString(R.string.action_sorted_by_category));
        pieDataSet.setSliceSpace(2f);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueFormatter((value, entry, datasetIndex, viewPortHandler)
                -> format.format(value) + "%");
        pieDataSet.setColors(colors);

        return new PieData(pieDataSet);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry pe = (PieEntry) e;
        String key = pe.getData() instanceof String ? (String) pe.getData() : null;
        if (key == null || key.trim().isEmpty()) {
            pieChart.setCenterText(pe.getLabel());
            return;
        }
        pieChart.setCenterText(key);
        showCategoryDialog(key);
    }

    @Override
    public void onNothingSelected() {
        pieChart.setCenterText(getString(R.string.action_sorted_by_category));
    }

    private void showCategoryDialog(String category) {
        if (redEnvelopes == null || redEnvelopes.isEmpty()) {
            return;
        }
        String others = getString(R.string.category_others);
        List<RedEnvelope> matched = new ArrayList<>();

        for (RedEnvelope redEnvelope : redEnvelopes) {
            String remark = redEnvelope.getRemark();
            if (others.equals(category)) {
                // 其它 = 未在图上单独展示的种类
                if (!shownCategories.contains(remark)) {
                    matched.add(redEnvelope);
                }
            } else {
                if (category.equals(remark)) {
                    matched.add(redEnvelope);
                }
            }
        }

        RecyclerView recyclerView = new RecyclerView(requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(new CategoryListAdapter(matched));

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.red_envelopes) + " - " + category)
                .setView(recyclerView)
                .setPositiveButton(R.string.ok, null)
                .create();
        dialog.show();
    }

    private static final class CategoryListAdapter
            extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
        private final List<RedEnvelope> items;

        private CategoryListAdapter(List<RedEnvelope> items) {
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
            String money = com.yjh.rer.util.MoneyFormatter.format(redEnvelope.getMoneyDouble());
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
