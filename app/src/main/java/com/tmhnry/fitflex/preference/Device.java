package com.tmhnry.fitflex.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

public class Device {
    private static final String SHARED_PREFS = "shared-preferences";
    public static final String WIDTH = "device-width";
    public static final String HEIGHT = "device-height";

    public static int getWidth(Activity activity) {
        int width = (int) load(activity, WIDTH);

        if (width > 0) {
            return width;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        Resources resources = activity.getResources();

        if (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            width = hasNavigationBar(activity.getResources())
                    ? displayMetrics.widthPixels + getNavigationBarHeight(activity)
                    : displayMetrics.widthPixels;
        } else {
            width = displayMetrics.widthPixels;
        }

        save(activity, WIDTH, width);
        return width;
    }

    public static int getHeight(Activity activity) {
        int height = (int) load(activity, HEIGHT);

        if (height > 0) {
            return height;
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = hasNavigationBar(activity.getResources())
                ? displayMetrics.heightPixels + getNavigationBarHeight(activity)
                : displayMetrics.heightPixels;

        save(activity, HEIGHT, height);
        return height;
    }

    public static void save(Context context, String key, Object value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        switch (key) {
            case WIDTH:
                editor.putInt(WIDTH, (int) value);
            default:
                editor.putInt(HEIGHT, (int) value);
        }

        editor.apply();
    }

    public static Object load(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        switch (key) {
            case WIDTH:
                return preferences.getInt(WIDTH, 0);
            default:
                return preferences.getInt(HEIGHT, 0);
        }
    }

    private static boolean hasNavigationBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    private static int getNavigationBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            return 0;
        }
        return 0;
    }

}
