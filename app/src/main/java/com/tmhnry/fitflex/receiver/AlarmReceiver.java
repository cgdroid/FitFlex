package com.tmhnry.fitflex.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.AlarmManagerCompat;

import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.R;

import java.util.Calendar;
import java.util.TimeZone;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmReceiver.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 200, i, PendingIntent.FLAG_MUTABLE);

        int workoutHr = (int) MyPreferences.load(context, MyPreferences.PREF_WORKOUT_TIME_HOUR);
        int workoutMn = (int) MyPreferences.load(context, MyPreferences.PREF_WORKOUT_TIME_MINUTE);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, workoutHr);
        calendar.set(Calendar.MINUTE, workoutMn);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "fitflex")
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setContentTitle("DailyFitness Workout Notification")
//                .setContentText("Start working out now!")
//                .setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent);
//
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        notificationManagerCompat.notify(123, builder.build());
    }
}
