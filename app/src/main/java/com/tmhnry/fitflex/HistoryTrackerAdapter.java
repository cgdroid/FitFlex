package com.tmhnry.fitflex;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.TimeZone;

public class HistoryTrackerAdapter extends RecyclerView.Adapter<HistoryTrackerAdapter.ViewHolder> {

    private enum Day {
        SUNDAY(1),
        MONDAY(2),
        TUESDAY(3),
        WEDNESDAY(4),
        THURSDAY(5),
        FRIDAY(6),
        SATURDAY(7);

        int code;

        private Day(int code) {
            this.code = code;
        }

        public String getAbbreviation() {
            return this.name().substring(0, 1);
        }
    }

    private static boolean isLeap;
    private UnitListener listener;
    private Context context;
    private final Day[] units = new Day[7];

    private int dayOfWeek;
    private int dayOfMonth;
    private int month;

    public interface UnitListener {
        void openDay(int position);
    }


    public HistoryTrackerAdapter(Context context) {
        this.context = context;
        try {
            listener = (UnitListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement UnitListener");
        }
        this.listener = listener;
        units[0] = Day.SUNDAY;
        units[1] = Day.MONDAY;
        units[2] = Day.TUESDAY;
        units[3] = Day.WEDNESDAY;
        units[4] = Day.THURSDAY;
        units[5] = Day.FRIDAY;
        units[6] = Day.SATURDAY;

        isLeap = false;

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
    }

    public HistoryTrackerAdapter(Context context, UnitListener listener) {
        this.context = context;
        this.listener = listener;

        units[0] = Day.SUNDAY;
        units[1] = Day.MONDAY;
        units[2] = Day.TUESDAY;
        units[3] = Day.WEDNESDAY;
        units[4] = Day.THURSDAY;
        units[5] = Day.FRIDAY;
        units[6] = Day.SATURDAY;

        isLeap = false;

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflate = LayoutInflater.from(context);
        View view = inflate.inflate(R.layout.history_tracker_unit, parent, false);
        return new ViewHolder(view, viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        int absPos = position;

        holder.mAbv.setText(units[absPos].getAbbreviation());

        if (units[absPos].code == dayOfWeek) {
            holder.mNum.setText(String.format("%d", dayOfMonth));
            holder.mDay.setImageResource(R.drawable.ic_baseline_check_circle_24);
            holder.mDay.setColorFilter(Color.argb(0XFF, 0X00, 0X6a, 0XFF));
            holder.mDay.setVisibility(View.VISIBLE);
        } else {
            int d = units[absPos].code - dayOfWeek;

            int day = dayOfMonth + d;

            if (day < 1) {
                day = maxDays(month - 1) + day;
            }
            if (day > maxDays(month)) {
                day = day - maxDays(month);
            }

            holder.mNum.setText(String.format("%d", day));
        }

        holder.mSel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.openDay(absPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return units.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mAbv, mNum;
        ImageView mDay;
        View mSel;

        public ViewHolder(@NonNull View view, int viewType) {
            super(view);
            mAbv = view.findViewById(R.id.day_abbreviation);
            mNum = view.findViewById(R.id.day_number);
            mSel = view.findViewById(R.id.day_selector);
            mDay = view.findViewById(R.id.check_day);
        }
    }

    private int maxDays(int month) {
        switch (month) {
            // -1 included because the compiler computes -1 % 12 as -1, case 12 is optional if arg
            // month is modded
            case -1:
            case 12:
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            default:
                return isLeap ? 28 : 27;
        }
    }
}
