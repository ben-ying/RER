package com.yjh.rer.main.chart;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseDaggerFragment;
import com.yjh.rer.custom.MyMarkerView;
import com.yjh.rer.databinding.FragmentRedEnvelopesBinding;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;

public class HorizontalBarChartFragment extends BaseDaggerFragment<FragmentRedEnvelopesBinding> {

    @BindView(R.id.horizontal_bar_chart)
    HorizontalBarChart chart;

    private Disposable mDisposable;

    public static HorizontalBarChartFragment newInstance() {
        return new HorizontalBarChartFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_horizontal_bar_chart;
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
    public void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
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
        mDisposable = Observable
                .create((ObservableEmitter<RedEnvelope> e) -> {
                    for (RedEnvelope redEnvelope : redEnvelopes) {
                        e.onNext(redEnvelope);
                    }
                    e.onComplete();
                })
                .toSortedList(Comparator.comparing(RedEnvelope::getMoneyInt))
                .subscribe((res) -> this.redEnvelopes = res);
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < redEnvelopes.size(); i++) {
            RedEnvelope redEnvelope = redEnvelopes.get(i);
            BarEntry barEntry = new BarEntry(i, redEnvelope.getMoneyInt());
            barEntry.setData(redEnvelope.getCreatedDate() + "\n"
                    + redEnvelope.getMoneyFrom()
                    + ": " + Utils.formatNumber(
                    redEnvelope.getMoneyInt(), 0, true));
            entries.add(barEntry);
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sorted_by_amount));
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);

        return new BarData(sets);
    }
}
