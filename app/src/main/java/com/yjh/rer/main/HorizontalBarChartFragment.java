package com.yjh.rer.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.yjh.rer.R;
import com.yjh.rer.base.BaseFragment;
import com.yjh.rer.base.custom.MyMarkerView;
import com.yjh.rer.room.entity.RedEnvelope;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class HorizontalBarChartFragment extends BaseFragment
        implements OnChartGestureListener, OnChartValueSelectedListener {

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
        mChart.setOnChartGestureListener(this);
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
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START");
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END");
//        mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1,
                             MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void updateData(ArrayList<RedEnvelope> redEnvelopes) {
        if (redEnvelopes != null) {
            sortByAmount(redEnvelopes);
            mChart.setData(generateBarData());
            mChart.invalidate();
            mChart.setVisibleXRangeMaximum(ChartFragment.CHART_PAGE_SIZE);
            mChart.moveViewTo(0, 0, YAxis.AxisDependency.LEFT);
//            mChart.notifyDataSetChanged();
        }
    }

    private void sortByAmount(final ArrayList<RedEnvelope> redEnvelopes) {
        mDisposable = Observable.create(new ObservableOnSubscribe<RedEnvelope>() {
            @Override
            public void subscribe(ObservableEmitter<RedEnvelope> e) throws Exception {
                for (RedEnvelope redEnvelope : redEnvelopes) {
                    e.onNext(redEnvelope);
                }
                e.onComplete();
            }
        }).toSortedList(new Comparator<RedEnvelope>() {
            @Override
            public int compare(RedEnvelope redEnvelope, RedEnvelope t1) {
                return redEnvelope.getMoneyInt() - t1.getMoneyInt();
            }
        }).subscribe(new Consumer<List<RedEnvelope>>() {
            @Override
            public void accept(List<RedEnvelope> redEnvelopes) throws Exception {
                mRedEnvelopes = new ArrayList<>(redEnvelopes);
            }
        });
    }

    private BarData generateBarData() {
        ArrayList<IBarDataSet> sets = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < mRedEnvelopes.size(); i++) {
            entries.add(new BarEntry(i, mRedEnvelopes.get(i).getMoneyInt()));
        }

        BarDataSet ds = new BarDataSet(entries, getString(R.string.action_sort_by_amount));
        // ds.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds.setColor(getActivity().getColor(R.color.colorPrimary));
        sets.add(ds);

        return new BarData(sets);
    }
}
