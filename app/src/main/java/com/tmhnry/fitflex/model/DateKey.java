package com.tmhnry.fitflex.model;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateKey {
    private Date date;

    DateKey(Date date) {
        this.date = date;
    }

    public static DateKey from(int year, int month, int day, int hour, int minute, int second, int millisecond){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millisecond);

        return new DateKey(calendar.getTime());
    }

    public static DateKey today() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = 23;
        int minute = 59;
        int second = 59;
        int millisecond = 0;

        return from(year, month, day, hour, minute, second, millisecond);
    }

    public int getDay(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + date.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null) return false;
        if(this.getClass() != obj.getClass()) return false;
        DateKey dateKey = (DateKey) obj;
        return date.equals(dateKey.date);
    }

}
