package com.yjh.rer.main.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
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
import java.util.Comparator;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;

public class HorizontalBarChartFragment extends BaseFragment {

    private static final String RED_ENVELOPE = "red_envelope";
    protected ArrayList<RedEnvelope> mRedEnvelopes;
    protected HorizontalBarChart mChart;
    private Disposable mDisposable;

    public static HorizontalBarChartFragment newInstance(ArrayList<RedEnvelope> redEnvelopes) {

        Bundle args = new Bundle();
        args.putSerializable(RED_ENVELOPE, redEnvelopes);
        HorizontalBarChartFragment fragment = new HorizontalBarChartFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_horizontal_bar_chart, container, false);
        mChart = v.findViewById(R.id.horizontal_bar_chart);
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
        xAxis.setTextSize(7);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
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
    public void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }

    @Override
    public void updateData(ArrayList<RedEnvelope> redEnvelopes) {
        if (redEnvelopes != null) {
            sortByAmount(redEnvelopes);
            mChart.setData(generateBarData());
            mChart.invalidate();
            mChart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
            mChart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
        }
    }

    private void sortByAmount(final ArrayList<RedEnvelope> redEnvelopes) {
        mDisposable = Observable
                .create((ObservableEmitter<RedEnvelope> e) -> {
                    for (RedEnvelope redEnvelope : redEnvelopes) {
                        e.onNext(redEnvelope);
                    }
                    e.onComplete();
                })
                .toSortedList(Comparator.comparing(RedEnvelope::getMoneyInt))
                .subscribe((res) -> {
                    mRedEnvelopes = new ArrayList<>(res);
                });
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < mRedEnvelopes.size(); i++) {
            entries.add(new BarEntry(i, mRedEnvelopes.get(i).getMoneyInt()));
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_amount));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);

        return new BarData(sets);
    }
}
