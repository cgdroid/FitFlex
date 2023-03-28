package com.tmhnry.fitflex;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.view.MotionEvent;

import com.tmhnry.fitflex.model.DateKey;
import com.tmhnry.fitflex.model.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
;

public class WeightChart {
    private static WeightChart instance;
    private LineChart chart;

    private WeightChart(LineChart chart) {
        this.chart = chart;
    }

    private static OnChartGestureListener chartGestureListener = new OnChartGestureListener() {
        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        }

        @Override
        public void onChartLongPressed(MotionEvent me) {
        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {
        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {
        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            if (velocityX > 0) {
                // fling to left
//                showPrevPage(); // Your own implementation method.
            } else {
                // fling to right
//                showNextPage(); // Your own implementation method.
            }
        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
        }
    };


    public static void destroy() {
        if (instance != null) {
            instance.chart.destroyDrawingCache();
            instance = null;
        }
    }


    public static WeightChart getInstance(LineChart chart) {
        if (instance == null) {
            instance = new WeightChart(chart);
            instance.chart.setOnChartGestureListener(chartGestureListener);
        }
        return instance;
    }

    public static void setMonth(LocalDate date) {
        assert (instance != null);
        LineChart chart = instance.chart;
        Description desc = new Description();
        desc.setText(date.getMonth().name());
        chart.setDescription(desc);
    }

    public static void addEntry(int day, float weight) {
        assert (instance != null);
        List<Entry> pnt = new ArrayList<>();
        pnt.add(new Entry(day, weight));
        instance.addEntries(pnt);
    }

    public void update() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = 23;
        int minute = 59;
        int second = 59;
        int millisecond = 0;

        String monthName = "";
        switch (month) {
            case Calendar.FEBRUARY:
                monthName = "FEB";
                break;
            case Calendar.MARCH:
                monthName = "MAR";
                break;
            case Calendar.APRIL:
                monthName = "APR";
                break;
            case Calendar.MAY:
                monthName = "MAY";
                break;
            case Calendar.JUNE:
                monthName = "JUN";
                break;
            case Calendar.JULY:
                monthName = "JUL";
                break;
            case Calendar.AUGUST:
                monthName = "AUG";
                break;
            case Calendar.SEPTEMBER:
                monthName = "SEP";
                break;
            case Calendar.OCTOBER:
                monthName = "OCT";
                break;
            case Calendar.NOVEMBER:
                monthName = "NOV";
                break;
            case Calendar.DECEMBER:
                monthName = "DEC";
                break;
            default:
                monthName = "JAN";
        }

        Description desc = new Description();
        desc.setText(monthName);
        chart.setDescription(desc);

        Map<DateKey, Float> weights = User.getUserData().getWeights();
        List<Entry> entries = new ArrayList<>();

        for (int i = 1; i <= day; i++) {
            DateKey dateKey = DateKey.from(year, month, i, hour, minute, second, millisecond);
            if (weights.containsKey(dateKey)) {
                entries.add(new Entry(dateKey.getDay(), weights.get(dateKey)));
            } else if (User.getUserData().getLastWeightUpdate() == null) {
                entries.add(new Entry(dateKey.getDay(), 0));
            } else {
                DateKey lastUpdateKey = User.getUserData().getLastWeightUpdate();
                entries.add(new Entry(lastUpdateKey.getDay(), weights.get(lastUpdateKey)));
            }
        }
//
//        for (int i = day + 1; i <= 7; i++) {
//            entries.add(new Entry(i, 0));
//        }

        addEntries(entries);
    }

    private void addEntries(List<Entry> entries) {
        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            LineDataSet set = (LineDataSet) chart.getData().getDataSetByIndex(0);
            for (Entry ety : entries) {
                set.addEntry(ety);
            }
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            LineDataSet set = new LineDataSet(entries, "Weight");
            set.setDrawIcons(false);
            set.enableDashedLine(10f, 5f, 0f);
            set.enableDashedHighlightLine(10f, 5f, 0f);
            set.setCircleColor(Color.DKGRAY);
            set.setCircleColor(Color.DKGRAY);
            set.setLineWidth(1f);
            set.setCircleRadius(3f);
            set.setDrawCircleHole(false);
            set.setValueTextSize(9f);
            set.setDrawFilled(true);
            set.setFormLineWidth(1f);
            set.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
//                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_blue);
//                set.setFillDrawable(drawable);
            } else {
                set.setFillColor(Color.DKGRAY);
            }
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set);
            LineData data = new LineData(dataSets);
            chart.setData(data);
        }
    }
}
