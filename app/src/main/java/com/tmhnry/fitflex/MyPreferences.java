package com.tmhnry.fitflex;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
    public static final String SHARED_PREFS = "shared-preferences";
    public static final String PREF_UID = "uid";

    public static final String PREF_EMAIL_ADDRESS = "email-address";
    public static final String PREF_FIRST_NAME = "first-name";
    public static final String PREF_LAST_NAME = "last-name";
    public static final String PREF_EXERCISE_HOURS_PER_WEEK = "exercise-hours-per-week";

    //    Settings
    public static final String PREF_SCREEN_DISPLAY = "screen-display";
    public static final String PREF_COUNTDOWN_TIME = "countdown-time";
    public static final String PREF_REST_TIME = "rest-time";
    public static final String PREF_UNITS = "units";
    public static final String PREF_POSE_PERFORMANCE = "pose-performance";
    public static final String PREF_SHOW_SKELETON = "show-skeleton";

    //    Tracker
    public static final String PREF_TOTAL_WORKOUTS = "total-workouts";
    public static final String PREF_TOTAL_CALORIES = "total-calories";
    public static final String PREF_TOTAL_DURATION = "total-duration";

    //
    public static final String PREF_WORKOUT_TIME_EXISTS = "workout-time-exists";
    public static final String PREF_WORKOUT_EVERYDAY = "workout-everyday";
    public static final String PREF_WORKOUT_TIME_HOUR = "workout-time-hour";
    public static final String PREF_WORKOUT_TIME_MINUTE = "workout-time-minute";

    //    User Physical Data
    public static final String PREF_BIRTH_YEAR = "birth-year";
    public static final String PREF_GENDER = "gender";
    public static final String PREF_HAS_GENDER = "has-gender";
    public static final String PREF_HEIGHT = "height";
    public static final String PREF_WEIGHT = "weight";
    public static final String PREF_BMI = "bmi";
    public static final String PREF_WEIGHTS = "weights";
    public static final String PREF_BELLY_TYPE = "belly-type";
    public static final String PREF_BELLY_CONFIDENCE = "belly-confidence";
    public static final String PREF_HAS_BELLY = "has-belly";

    public static void save(Context context, String key, Object value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (key) {
            case PREF_GENDER:
                editor.putInt(PREF_GENDER, (int) value);
            case PREF_UNITS:
                editor.putInt(PREF_UNITS, (int) value);
                break;
            case PREF_COUNTDOWN_TIME:
                editor.putInt(PREF_COUNTDOWN_TIME, (int) value);
                break;
            case PREF_REST_TIME:
                editor.putInt(PREF_REST_TIME, (int) value);
                break;
            case PREF_TOTAL_WORKOUTS:
                editor.putInt(PREF_TOTAL_WORKOUTS, (int) value);
                break;
            case PREF_TOTAL_CALORIES:
                editor.putFloat(PREF_TOTAL_CALORIES, (float) value);
                break;
            case PREF_TOTAL_DURATION:
                editor.putLong(PREF_TOTAL_DURATION, (long) value);
                break;
            case PREF_WEIGHT:
                editor.putFloat(PREF_WEIGHT, (float) value);
                break;
            case PREF_HEIGHT:
                editor.putFloat(PREF_HEIGHT, (float) value);
                break;
            case PREF_BMI:
                editor.putFloat(PREF_BMI, (float) value);
                break;
            case PREF_WEIGHTS:
                editor.putString(PREF_WEIGHTS, (String) value);
                break;
            case PREF_BIRTH_YEAR:
                editor.putInt(PREF_BIRTH_YEAR, (int) value);
                break;
            case PREF_WORKOUT_EVERYDAY:
                editor.putBoolean(PREF_WORKOUT_EVERYDAY, (boolean) value);
                break;
            case PREF_WORKOUT_TIME_EXISTS:
                editor.putBoolean(PREF_WORKOUT_TIME_EXISTS, (boolean) value);
                break;
            case PREF_WORKOUT_TIME_HOUR:
                editor.putInt(PREF_WORKOUT_TIME_HOUR, (int) value);
                break;
            case PREF_WORKOUT_TIME_MINUTE:
                editor.putInt(PREF_WORKOUT_TIME_MINUTE, (int) value);
                break;
            case PREF_BELLY_TYPE:
                editor.putInt(PREF_BELLY_TYPE, (int) value);
                break;
            case PREF_BELLY_CONFIDENCE:
                editor.putFloat(PREF_BELLY_CONFIDENCE, (float) value);
                break;
            case PREF_SCREEN_DISPLAY:
                editor.putBoolean(PREF_SCREEN_DISPLAY, (boolean) value);
                break;
            case PREF_SHOW_SKELETON:
                editor.putBoolean(PREF_SHOW_SKELETON, (boolean) value);
                break;
            case PREF_POSE_PERFORMANCE:
                editor.putInt(PREF_POSE_PERFORMANCE, (int) value);
                break;
            case PREF_HAS_GENDER:
                editor.putBoolean(PREF_HAS_GENDER, (boolean) value);
                break;
            case PREF_HAS_BELLY:
                editor.putBoolean(PREF_HAS_BELLY, (boolean) value);
                break;
            case PREF_EMAIL_ADDRESS:
                editor.putString(PREF_EMAIL_ADDRESS, (String) value);
                break;
            case PREF_FIRST_NAME:
                editor.putString(PREF_FIRST_NAME, (String) value);
                break;
            case PREF_LAST_NAME:
                editor.putString(PREF_LAST_NAME, (String) value);
                break;
            case PREF_EXERCISE_HOURS_PER_WEEK:
                editor.putInt(PREF_EXERCISE_HOURS_PER_WEEK, (int) value);
                break;
            default:
                editor.putString(PREF_UID, (String) value);
        }

        editor.apply();
    }

    public static void reset(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(PREF_UNITS, 0);

        editor.putFloat(PREF_BMI, 0f);

        editor.putFloat(PREF_HEIGHT, 0f);

        editor.putFloat(PREF_WEIGHT, 0f);

        editor.putFloat(PREF_TOTAL_CALORIES, 0f);

        editor.putLong(PREF_TOTAL_DURATION, 0);

        editor.putInt(PREF_TOTAL_WORKOUTS, 0);

        editor.putInt(PREF_REST_TIME, 25);

        editor.putInt(PREF_COUNTDOWN_TIME, 15);

        editor.putInt(PREF_BIRTH_YEAR, 1990);

        editor.putInt(PREF_GENDER, 0);

        editor.putBoolean(PREF_SHOW_SKELETON, false);

        editor.putInt(PREF_POSE_PERFORMANCE, 0);

        editor.putString(PREF_WEIGHTS, "");

        editor.putBoolean(PREF_WORKOUT_TIME_EXISTS, false);

        editor.putBoolean(PREF_WORKOUT_EVERYDAY, false);

        editor.putInt(PREF_WORKOUT_TIME_HOUR, 0);

        editor.putInt(PREF_WORKOUT_TIME_MINUTE, 0);

        editor.putInt(PREF_BELLY_TYPE, -1);

        editor.putFloat(PREF_BELLY_CONFIDENCE, -1f);

        editor.putBoolean(PREF_SCREEN_DISPLAY, true);

        editor.putBoolean(PREF_HAS_GENDER, false);

        editor.putBoolean(PREF_HAS_BELLY, false);

//        editor.putString(PREF_EMAIL_ADDRESS, "");

//        editor.putString(PREF_FIRST_NAME, "");
//
//        editor.putString(PREF_LAST_NAME, "");

        editor.putInt(PREF_EXERCISE_HOURS_PER_WEEK, 0);

        editor.apply();
    }


    public static Object load(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        switch (key) {
            case PREF_UNITS:
                return preferences.getInt(PREF_UNITS, 0);
            case PREF_BMI:
                return preferences.getFloat(PREF_BMI, 0f);
            case PREF_HEIGHT:
                return preferences.getFloat(PREF_HEIGHT, 0f);
            case PREF_WEIGHT:
                return preferences.getFloat(PREF_WEIGHT, 0f);
            case PREF_TOTAL_CALORIES:
                return preferences.getFloat(PREF_TOTAL_CALORIES, 0f);
            case PREF_TOTAL_DURATION:
                return preferences.getLong(PREF_TOTAL_DURATION, 0);
            case PREF_TOTAL_WORKOUTS:
                return preferences.getInt(PREF_TOTAL_WORKOUTS, 0);
            case PREF_REST_TIME:
                return preferences.getInt(PREF_REST_TIME, 25);
            case PREF_COUNTDOWN_TIME:
                return preferences.getInt(PREF_COUNTDOWN_TIME, 15);
            case PREF_BIRTH_YEAR:
                return preferences.getInt(PREF_BIRTH_YEAR, 1990);
            case PREF_GENDER:
                return preferences.getInt(PREF_GENDER, 0);
            case PREF_WEIGHTS:
                return preferences.getString(PREF_WEIGHTS, "");
            case PREF_WORKOUT_TIME_EXISTS:
                return preferences.getBoolean(PREF_WORKOUT_TIME_EXISTS, false);
            case PREF_WORKOUT_EVERYDAY:
                return preferences.getBoolean(PREF_WORKOUT_EVERYDAY, false);
            case PREF_WORKOUT_TIME_HOUR:
                return preferences.getInt(PREF_WORKOUT_TIME_HOUR, 0);
            case PREF_WORKOUT_TIME_MINUTE:
                return preferences.getInt(PREF_WORKOUT_TIME_MINUTE, 0);
            case PREF_BELLY_TYPE:
                return preferences.getInt(PREF_BELLY_TYPE, -1);
            case PREF_BELLY_CONFIDENCE:
                return preferences.getFloat(PREF_BELLY_CONFIDENCE, -1);
            case PREF_SCREEN_DISPLAY:
                return preferences.getBoolean(PREF_SCREEN_DISPLAY, true);
            case PREF_POSE_PERFORMANCE:
                return preferences.getInt(PREF_POSE_PERFORMANCE, 0);
            case PREF_SHOW_SKELETON:
                return preferences.getBoolean(PREF_SHOW_SKELETON, false);
            case PREF_HAS_GENDER:
                return preferences.getBoolean(PREF_HAS_GENDER, false);
            case PREF_HAS_BELLY:
                return preferences.getBoolean(PREF_HAS_BELLY, false);
            case PREF_EMAIL_ADDRESS:
                return preferences.getString(PREF_EMAIL_ADDRESS, "");
            case PREF_FIRST_NAME:
                return preferences.getString(PREF_FIRST_NAME, "");
            case PREF_LAST_NAME:
                return preferences.getString(PREF_LAST_NAME, "");
            case PREF_EXERCISE_HOURS_PER_WEEK:
                return preferences.getInt(PREF_EXERCISE_HOURS_PER_WEEK, 0);
            default:
                return preferences.getString(PREF_UID, "");
        }
    }
}
