package com.tmhnry.fitflex.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.AlarmManagerCompat;
import androidx.fragment.app.Fragment;

import com.tmhnry.fitflex.receiver.AlarmReceiver;
import com.tmhnry.fitflex.database.Firebase;
import com.tmhnry.fitflex.LoginRegister;
import com.tmhnry.fitflex.MainActivity;
import com.tmhnry.fitflex.MyDialog;
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.GenderActivity;
import com.tmhnry.fitflex.model.User;
import com.tmhnry.fitflex.databinding.FragmentSettingsBinding;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

public class SettingsFragment extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_SETTINGS";

    private static final String IS_DEFAULT = "IS_DEFAULT";
    private static boolean PREF_POSE_PERFOMANCE_ACCURATE;

    private FragmentSettingsBinding binding;

    private MyDialog mDialogCountdown;
    private MyDialog mDialogRest;
    private MyDialog mDialogUnits;
    private MyDialog mDialogPosePerformance;


    private MaterialTimePicker timePicker;
    private Calendar calendar;
    private AlarmManager alarmManager;

    private boolean isDefault;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment init(boolean isDefault) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_DEFAULT, isDefault);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initTrainDialog(context);
        initRestDialog(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isDefault = getArguments().getBoolean(IS_DEFAULT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);

        ((MainActivity) getActivity()).setToolbarAlpha(1);
        ((MainActivity) getActivity()).setMajorTitleText("SETTINGS");
        ((MainActivity) getActivity()).setMinorTitleText("");
        ((MainActivity) getActivity()).setMajorTitleColor(Color.BLACK);
        ((MainActivity) getActivity()).setAccountImageVisibility(View.INVISIBLE);

        if (!isDefault) {
            binding.getRoot().setPadding(0, 32 + ((MainActivity) getActivity()).getToolbarHeight(), 0, 52);
        } else {
            binding.getRoot().post(new Runnable() {
                @Override
                public void run() {
                    binding.getRoot().setPadding(0, 32 + ((MainActivity) getActivity()).getToolbarHeight(), 0, 52);
                }
            });
        }

        updateCountdownView();
        updateTrainRestView();
        updateCalendar();
        updateScreenSwitch();
        updateUnitsView();
        updatePerformanceView();
//        updateShowSkeletonSwitch();

        binding.cardWorkoutSchedule.setOnClickListener(view -> {
            showTimePicker();
        });


        // Alarm Management
        updateScheduleView();
        updateScheduleSwitch();
        binding.switchWorkoutEveryday.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b && !((boolean) MyPreferences.load(getActivity(),
                    MyPreferences.PREF_WORKOUT_TIME_EXISTS))) {
                showTimePicker();
            } else if (b) {
                cancelAlarm(false);
                setAlarm();
            } else if (!b) {
                cancelAlarm(true);
            }
        });

        binding.switchScreenOn.setOnCheckedChangeListener((compoundButton, b) -> {
            MyPreferences.save(getActivity(), MyPreferences.PREF_SCREEN_DISPLAY, b);
            updateScreenSwitch();
        });

//        binding.switchShowSkeleton.setOnCheckedChangeListener((compoundButton, b) -> {
//            MyPreferences.save(getActivity(), MyPreferences.PREF_SHOW_SKELETON, b);
//        });

        binding.cardGender.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), GenderActivity.class));
        });

        binding.cardUnits.setOnClickListener(view -> {
            mDialogUnits.open();
        });

        binding.cardRestTime.setOnClickListener(view -> {
            mDialogRest.open();
        });

        binding.cardCountdownTime.setOnClickListener(view -> {
            mDialogCountdown.open();
        });

//        binding.cardPosePerformance.setOnClickListener(view -> {
//            mDialogPosePerformance.open();
//        });

        binding.cardReset.setOnClickListener(view -> {
            cancelAlarm(false);
            MyPreferences.reset(getActivity());
            User.delete();
            startActivity(new Intent(getActivity(), LoginRegister.class));
            getActivity().finish();
        });

        binding.txtMetricImperial.setText("Metric & Imperial Units");

        binding.cardSignOut.setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Firebase.logout();
                Toast.makeText(getActivity(), "Logout successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), LoginRegister.class));
                getActivity().finish();
            } else {
                Toast.makeText(getActivity(), "Invalid Operation", Toast.LENGTH_SHORT).show();
            }
        });

        mDialogPosePerformance = MyDialog.fromSelection(getActivity(), ((MainActivity) getActivity()).getSelectPerformanceListener(), "Detection Performance");
        mDialogUnits = MyDialog.fromSelection(getActivity(), ((MainActivity) getActivity()).getSelectUnitsListener(), "Measurement Units");
        mDialogRest = MyDialog.fromSpinner(getActivity(), ((MainActivity) getActivity()).getSetRestTimeListener(), "Set duration (5 ~ 180 secs)");
        mDialogCountdown = MyDialog.fromSpinner(getActivity(), ((MainActivity) getActivity()).getSetCountdownTimeListener(), "Set duration (10 ~ 15 secs)");

        return binding.getRoot();
    }

//    private void updateShowSkeletonSwitch(){
//        binding.switchShowSkeleton
//                .setChecked((boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_SHOW_SKELETON));
//    }

    private void updateCalendar() {
        if (!(boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_EXISTS)) {
            return;
        }

        int workoutHr = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_HOUR);
        int workoutMn = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_MINUTE);

        calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, workoutHr);
        calendar.set(Calendar.MINUTE, workoutMn);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void showTimePicker() {
        updateTimePicker();
        timePicker.show(getActivity().getSupportFragmentManager(), "fragment_tag");
        AtomicReference<String> time = new AtomicReference<>();

        timePicker.addOnPositiveButtonClickListener(view -> {
            cancelAlarm(false);

            MyPreferences.save(getActivity(), MyPreferences.PREF_WORKOUT_EVERYDAY, true);
            MyPreferences.save(getActivity(), MyPreferences.PREF_WORKOUT_TIME_EXISTS, true);
            MyPreferences.save(getActivity(), MyPreferences.PREF_WORKOUT_TIME_HOUR, timePicker.getHour());
            MyPreferences.save(getActivity(), MyPreferences.PREF_WORKOUT_TIME_MINUTE, timePicker.getMinute());

            updateScheduleSwitch();
            updateCalendar();

            if ((boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_EVERYDAY)) {
                setAlarm();
            }

            updateScheduleView();
        });

        timePicker.addOnNegativeButtonClickListener(view -> {
            if (!((boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_EXISTS))) {
                binding.switchWorkoutEveryday.setChecked(false);
            }
        });
    }

    private void updateTimePicker() {
        int workoutHr = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_HOUR);
        int workoutMn = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_MINUTE);

        timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(workoutHr)
                .setMinute(workoutMn).setTitleText("Select Workout Time")
                .build();
    }

    private void setAlarm() {
        // Load existing Hour and Minute Workout Schedule
        int workoutHr = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_HOUR);
        int workoutMn = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_MINUTE);

        // Check if Calendar in returned in method == Calendar declared or initialized here
        calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, workoutHr);
        calendar.set(Calendar.MINUTE, workoutMn);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

        Intent intent = new Intent(getActivity(), AlarmReceiver.class);

        PendingIntent myIntent = PendingIntent.getBroadcast(getActivity(), 200, intent, PendingIntent.FLAG_MUTABLE);
//        int delay = 2000;
        long interval = 1000 * 60;
        AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), myIntent);
//        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, myIntent);

//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, delay, AlarmManager.INTERVAL_DAY, myIntent);

        Toast.makeText(getActivity(), "Alarm set Successfully", Toast.LENGTH_SHORT).show();
    }

    private void cancelAlarm(boolean show) {
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);

        PendingIntent myIntent = PendingIntent.getBroadcast(getActivity(), 200, intent, PendingIntent.FLAG_MUTABLE);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        }

        alarmManager.cancel(myIntent);

        if (show) {
            Toast.makeText(getActivity(), "Alarm Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void updatePerformanceView() {
        String performance = "Accurate";

        if ((int) MyPreferences.load(getActivity(), MyPreferences.PREF_POSE_PERFORMANCE) == 1) {
            performance = "Fast";
        }

//        binding.txtPosePerformance.setText(performance);
    }

    public void updateCountdownView() {
        int countdownDuration = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_COUNTDOWN_TIME);
        binding.txtPreferredCountdownTime.setText(String.valueOf(countdownDuration) + " secs");
    }

    public void updateTrainRestView() {
        int restDuration = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_REST_TIME);
        binding.txtPreferredTrainRestTime.setText(String.valueOf(restDuration) + " secs");
    }

    public void updateUnitsView() {
        String units = "Metric";

        if ((int) MyPreferences.load(getActivity(), MyPreferences.PREF_UNITS) == 1) {
            units = "Imperial";
        }

        binding.txtUnits.setText(units);
    }

    public void updateScheduleView() {
        if (!(boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_EXISTS)) {
            binding.txtWorkoutSchedule.setText("");
            return;
        }

        String time = "";

        int workoutHr = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_HOUR);
        int workoutMn = (int) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_TIME_MINUTE);
        if (workoutHr > 12) {
            time = String.format("%02d", workoutHr - 12) + ":" + String.format("%02d", workoutMn) + " PM";
        } else {
            time = String.format("%02d", workoutHr) + ":" + String.format("%02d", workoutMn) + " AM";
        }

        binding.txtWorkoutSchedule.setText(time);
    }

    public void updateScreenSwitch() {
        boolean keepScreenOn = (boolean) MyPreferences.load(getActivity(),
                MyPreferences.PREF_SCREEN_DISPLAY);
        binding.switchScreenOn.setChecked(keepScreenOn);
        if (keepScreenOn) {
            getActivity()
                    .getWindow()
                    .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else {
            getActivity()
                    .getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    public void updateScheduleSwitch() {
        binding.switchWorkoutEveryday.setChecked((boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_WORKOUT_EVERYDAY));
    }

    private void initTrainDialog(Context context) {
        int min = 5;
        int max = 180;
        String sub = String.format("Set duration (%d ~ %d secs)", min, max);
//        mDialogTrain = MyDialog.fromSpinner(context, min, max, sub);
    }

    private void initRestDialog(Context context) {
        int min = 10;
        int max = 15;
        String sub = String.format("Set duration (%d ~ %d secs)", min, max);
//        mDialogTrain = MyDialog.fromSpinner(context, min, max, sub);
    }
}