package com.tmhnry.fitflex.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tmhnry.fitflex.HistoryTrackerAdapter;
import com.tmhnry.fitflex.MainActivity;
import com.tmhnry.fitflex.MyDialog;
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.model.User;
import com.tmhnry.fitflex.WeightChart;
import com.tmhnry.fitflex.databinding.FragmentReportsBinding;
import com.tmhnry.fitflex.databinding.HistoryTrackerBinding;
import com.tmhnry.fitflex.databinding.WorkoutTrackerBinding;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

public class ReportsFragment extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_REPORTS";
    private static final String IS_DEFAULT = "IS_DEFAULT";

    private static final int VALUE_COLOR = 0XFF0000FF;
    private static final int LABEL_COLOR = 0XFFCCCCCC;

    private HistoryTrackerAdapter adapter;

    private FragmentReportsBinding binding;

    private MyDialog mDialogBmi;
    private MyDialog mDialogHeight;
    private MyDialog mDialogWeight;

    private boolean isDefault;

    public ReportsFragment() {
    }

    public static ReportsFragment init(boolean isDefault) {
        ReportsFragment fragment = new ReportsFragment();
        Bundle args = new Bundle();
        args.putBoolean(IS_DEFAULT, isDefault);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        adapter = new HistoryTrackerAdapter(context, ((MainActivity) context).getOpenHistoryDayListener());
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
        // Inflate the layout for this fragment
        binding = FragmentReportsBinding.inflate(inflater, container, false);

        if (!isDefault) {
            binding.workoutTracker.getRoot().setPadding(0, 32 + ((MainActivity) getActivity()).getToolbarHeight(), 0, 52);
        } else {
            binding.getRoot().post(new Runnable() {
                @Override
                public void run() {
                    binding.workoutTracker.getRoot().setPadding(0, 32 + ((MainActivity) getActivity()).getToolbarHeight(), 0, 52);
                }
            });
        }

        ((MainActivity) getActivity()).setAccountImageVisibility(View.VISIBLE);

        WorkoutTrackerBinding workoutTrackerBinding = binding.workoutTracker;
        HistoryTrackerBinding historyTrackerBinding = binding.historyTracker;

        RecyclerView recyclerView = historyTrackerBinding.recyclerView;
        recyclerView.setAdapter(adapter);

        FlexboxLayoutManager manager = new FlexboxLayoutManager(getContext());
        manager.setJustifyContent(JustifyContent.SPACE_AROUND);
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setFlexWrap(FlexWrap.WRAP);

        recyclerView.setLayoutManager(manager);

        updateChart();

        String sDuration = "";
        int duration = (int) User.getUserData().getTotalDuration() / 1000;
        if (duration > 60) {
            sDuration = String.valueOf(duration / 60);
            workoutTrackerBinding.txtDuration.setText("MINUTES");
        } else {
            sDuration = String.valueOf(duration);
            workoutTrackerBinding.txtDuration.setText("SECONDS");
        }
        workoutTrackerBinding.txtTotalDuration.setText(sDuration);

        String sCalories = "";
        float calories = (float) User.getUserData().getTotalCalories();

        if(calories == 0f){
            sCalories = String.format("%d", 0);
            workoutTrackerBinding.txtCalories.setText("CALORIES");
        }
        else if (calories > 1000f) {
            sCalories = String.format("%.2f", calories / 1000f);
            workoutTrackerBinding.txtCalories.setText("KCALS");
        } else {
            sCalories = String.format("%.2f", calories);
            workoutTrackerBinding.txtCalories.setText("CALORIES");
        }
        workoutTrackerBinding.txtTotalCalories.setText(sCalories);

        int workouts = (int) User.getUserData().getTotalWorkouts();
        workoutTrackerBinding.txtTotalWorkouts.setText(String.valueOf(workouts));

        ((MainActivity) getActivity()).setToolbarAlpha(1);
        ((MainActivity) getActivity()).setMajorTitleText("REPORTS");
        ((MainActivity) getActivity()).setMinorTitleText("");
        ((MainActivity) getActivity()).setMajorTitleColor(Color.BLACK);

        updateBmiUnitsView();
        updateHeightView();
        updateBmiView();

        mDialogHeight = MyDialog.fromEdit(getActivity(), ((MainActivity) getActivity()).getEditHeightListener(), "Height");
        mDialogBmi = MyDialog.fromEdit(getActivity(), ((MainActivity) getActivity()).getEditBmiListener(), "BMI");
        mDialogWeight = MyDialog.fromEdit(getActivity(), ((MainActivity) getActivity()).getEditWeightListener(), "Edit Weight");

        binding.editHeight.setOnClickListener(view -> {
            mDialogHeight.open();
        });

        binding.imgAddWeight.setOnClickListener(view -> {
            mDialogWeight.open();
        });

        binding.editBmi.setOnClickListener(view -> {
            mDialogBmi.open();
        });

        workoutTrackerBinding.txtTotalDuration.setTextColor(VALUE_COLOR);
        workoutTrackerBinding.txtTotalCalories.setTextColor(VALUE_COLOR);
        workoutTrackerBinding.txtTotalWorkouts.setTextColor(VALUE_COLOR);

        workoutTrackerBinding.txtTotalDuration.setTypeface(Typeface.DEFAULT_BOLD);
        workoutTrackerBinding.txtTotalCalories.setTypeface(Typeface.DEFAULT_BOLD);
        workoutTrackerBinding.txtTotalWorkouts.setTypeface(Typeface.DEFAULT_BOLD);

        workoutTrackerBinding.txtDuration.setTextColor(LABEL_COLOR);
        workoutTrackerBinding.txtCalories.setTextColor(LABEL_COLOR);
        workoutTrackerBinding.txtWorkouts.setTextColor(LABEL_COLOR);

        return binding.getRoot();

    }

    public void updateChart(){
        LineChart mChart = binding.weightChart.lineChart;
        WeightChart.getInstance(mChart).update();
    }

    public void updateHeightView() {
        String units = "";
        float height = 0f;

        if (((Integer) MyPreferences.load(getActivity(), MyPreferences.PREF_UNITS)).equals(0)) {
            units = "cm";
            height = User.getUserData().getHeight();
        } else {
            height = User.getUserData().getHeight() * 3.281f / 100;
            units = "ft";
        }

        binding.txtCurrentHeight.setText(String.format("%.2f %s", height, units));
    }

    public void updateBmiUnitsView() {
        String units = "BMI (";

        if (((Integer) MyPreferences.load(getActivity(), MyPreferences.PREF_UNITS)).equals(0)) {
            units += "kg/m";
        } else {
            units += "lb/ft";
        }

        SpannableString span = new SpannableString("3");
        span.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
        spanBuilder.append(units);
        spanBuilder.append(span);
        spanBuilder.append(")");

        binding.bmi.setText(spanBuilder);
    }

    public void updateBmiView() {
        String units = "";
        float bmi = 0f;

        if (((Integer) MyPreferences.load(getActivity(), MyPreferences.PREF_UNITS)).equals(0)) {
            units = "kg/m";
            bmi = User.getUserData().getBmi();
        } else {
            units = "lb/ft";
            bmi = User.getUserData().getBmi() * 0.062428f;
        }

        SpannableString span = new SpannableString("3");
        span.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
        spanBuilder.append(String.format("%.2f ", bmi));
        spanBuilder.append(units);
        spanBuilder.append(span);

        binding.txtCurrentBmi.setText(spanBuilder);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        WeightChart.destroy();
    }
}