package com.tmhnry.fitflex.model;

import android.content.Context;

import com.tmhnry.fitflex.MyPreferences;
import com.google.mlkit.vision.label.ImageLabel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    public static String TYPE_A = "TYPE_A";
    public static String TYPE_B = "TYPE_B";
    public static String TYPE_C = "TYPE_C";

    private static User instance;
    private Sex sex;
    private int birthYear;
    private int bellyType;
    private List<ImageLabel> bellyTypes;
    private Map<DateKey, Float> weights;
    private float height;
    private float bmi;
    private float weight;
    private int totalWorkouts;
    private float totalCalories;
    private long totalDuration;
    private int squatsCount = 0;
    private DateKey lastWeightUpdate;

    private User(Map<String, Object> userData) {
        if (userData.containsKey(MyPreferences.PREF_GENDER)) {
            sex = (Sex) userData.get(MyPreferences.PREF_GENDER);
        } else {
            sex = Sex.MALE;
        }
        if (userData.containsKey(MyPreferences.PREF_BIRTH_YEAR)) {
            birthYear = (int) userData.get(MyPreferences.PREF_BIRTH_YEAR);
        } else {
            birthYear = 1990;
        }
        if (userData.containsKey(MyPreferences.PREF_WEIGHT)) {
            weight = (float) userData.get(MyPreferences.PREF_WEIGHT);
        } else {
            weight = 0f;
        }
        if (userData.containsKey(MyPreferences.PREF_HEIGHT)) {
            height = (float) userData.get(MyPreferences.PREF_HEIGHT);
        } else {
            height = 0f;
        }
        if (userData.containsKey(MyPreferences.PREF_BMI)) {
            bmi = (float) userData.get(MyPreferences.PREF_BMI);
        } else {
            bmi = 0f;
        }
        if (userData.containsKey(MyPreferences.PREF_TOTAL_WORKOUTS)) {
            totalWorkouts = (int) userData.get(MyPreferences.PREF_TOTAL_WORKOUTS);
        } else {
            totalWorkouts = 0;
        }
        if (userData.containsKey(MyPreferences.PREF_TOTAL_CALORIES)) {
            totalCalories = (float) userData.get(MyPreferences.PREF_TOTAL_CALORIES);
        } else {
            totalCalories = 0;
        }
        if (userData.containsKey(MyPreferences.PREF_TOTAL_DURATION)) {
            totalDuration = (long) userData.get(MyPreferences.PREF_TOTAL_DURATION);
        } else {
            totalDuration = 0;
        }
        if (userData.containsKey(MyPreferences.PREF_WEIGHTS)) {
            weights = (HashMap<DateKey, Float>) userData.get(MyPreferences.PREF_WEIGHTS);
        }
        if (userData.containsKey(MyPreferences.PREF_WEIGHTS) && userData.get(MyPreferences.PREF_WEIGHTS) == null) {
            weights = new HashMap<>();
        }
        if(!userData.containsKey(MyPreferences.PREF_WEIGHTS)){
            weights = new HashMap<>();
        }
        if (userData.containsKey(MyPreferences.PREF_BELLY_TYPE)) {
            bellyType = (int) userData.get(MyPreferences.PREF_BELLY_TYPE);
        }
    }

    public static User getUserData() {
        assert (instance != null);
        return instance;
    }

    public static User getUserData(Context context) {
        if (instance == null) {
            Map<String, Object> userData = new HashMap<>();

            int iSex = (int) MyPreferences.load(context, MyPreferences.PREF_GENDER);

            if (iSex == 0) {
                userData.put(MyPreferences.PREF_GENDER, Sex.MALE);
            } else {
                userData.put(MyPreferences.PREF_GENDER, Sex.FEMALE);
            }

            float totalCalories = (float) MyPreferences.load(context, MyPreferences.PREF_TOTAL_CALORIES);
            userData.put(MyPreferences.PREF_TOTAL_CALORIES, totalCalories);

            long totalDuration = (long) MyPreferences.load(context, MyPreferences.PREF_TOTAL_DURATION);
            userData.put(MyPreferences.PREF_TOTAL_DURATION, totalDuration);

            int totalWorkout = (int) MyPreferences.load(context, MyPreferences.PREF_TOTAL_WORKOUTS);
            userData.put(MyPreferences.PREF_TOTAL_WORKOUTS, totalWorkout);

            float height = (float) MyPreferences.load(context, MyPreferences.PREF_HEIGHT);
            userData.put(MyPreferences.PREF_HEIGHT, height);

            float weight = (float) MyPreferences.load(context, MyPreferences.PREF_WEIGHT);
            userData.put(MyPreferences.PREF_WEIGHT, weight);

            float bmi = (float) MyPreferences.load(context, MyPreferences.PREF_BMI);
            userData.put(MyPreferences.PREF_BMI, bmi);

            int birthYear = (int) MyPreferences.load(context, MyPreferences.PREF_BIRTH_YEAR);
            userData.put(MyPreferences.PREF_BIRTH_YEAR, birthYear);

            int bellyType = (int) MyPreferences.load(context, MyPreferences.PREF_BELLY_TYPE);
            userData.put(MyPreferences.PREF_BELLY_TYPE, bellyType);

            String weights = (String) MyPreferences.load(context, MyPreferences.PREF_WEIGHTS);

            if (weights.isEmpty()) {
                userData.put(MyPreferences.PREF_WEIGHTS, null);
            }

            instance = new User(userData);

        }
        return instance;
    }

    public static void delete() {
        instance = null;
    }

    public DateKey getLastWeightUpdate() {
        return lastWeightUpdate;
    }

    public void addCalories(Context context, float calories){
        totalCalories += calories;
        updateCalories(context, totalCalories);
    }

    public void addDuration(Context context, long duration){
        totalDuration += duration;
        updateDuration(context, totalDuration);
    }

    public void addWorkouts(Context context, int workouts){
        totalWorkouts += workouts;
        updateWorkouts(context, totalWorkouts);
    }

    private void updateWorkouts(Context context, int totalWorkouts){
        MyPreferences.save(context, MyPreferences.PREF_TOTAL_WORKOUTS, totalWorkouts);
    }

    private void updateCalories(Context context, float totalCalories){
        MyPreferences.save(context, MyPreferences.PREF_TOTAL_CALORIES, totalCalories);
    }
    
    private void updateDuration(Context context, long totalDuration){
        MyPreferences.save(context, MyPreferences.PREF_TOTAL_DURATION, totalDuration);
    }

    public void updateWeight(Context context, DateKey dateKey, Float weight) {
        weights.put(dateKey, weight);
        this.lastWeightUpdate = dateKey;
        this.weight = weight;
        MyPreferences.save(context, MyPreferences.PREF_WEIGHT, weight);
    }

    public void updateHeight(Context context, float height) {
        this.height = height;
        MyPreferences.save(context, MyPreferences.PREF_HEIGHT, height);
    }

    public void updateBmi(Context context, float bmi) {
        this.bmi = bmi;
        MyPreferences.save(context, MyPreferences.PREF_BMI, bmi);
    }

    public void updateSex(Context context, Sex sex) {
        this.sex = sex;
        if (sex.equals(Sex.MALE)) {
            MyPreferences.save(context, MyPreferences.PREF_GENDER, 0);
        } else {
            MyPreferences.save(context, MyPreferences.PREF_GENDER, 1);
        }
    }

    public void updateBirthYear(Context context, int birthYear) {
        this.birthYear = birthYear;
        MyPreferences.save(context, MyPreferences.PREF_BIRTH_YEAR, birthYear);
    }

    public void updateBelly(Context context, int bellyType) {
        this.bellyType = bellyType;
        MyPreferences.save(context, MyPreferences.PREF_BELLY_TYPE, bellyType);
    }

    public Map<DateKey, Float> getWeights() {
        return weights;
    }

    public float getWeight() {
        return weight;
    }

    public float getBmi() {
        return bmi;
    }

    public float getHeight() {
        return height;
    }

    public void incSquat() {
        squatsCount++;
    }

    public int getSquatsCount() {
        return squatsCount;
    }

    public Sex getSex() {
        return sex;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public float getTotalCalories() {
        return totalCalories;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public int getBellyType() {
        return bellyType;
    }

    public enum Sex {MALE, FEMALE}

}
