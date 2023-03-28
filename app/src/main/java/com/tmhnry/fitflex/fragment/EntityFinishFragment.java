package com.tmhnry.fitflex.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tmhnry.fitflex.model.DateKey;
import com.tmhnry.fitflex.EntityActivity;
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.databinding.FragmentEntityFinishBinding;
import com.tmhnry.fitflex.model.Entities;
import com.tmhnry.fitflex.model.Entity;
import com.tmhnry.fitflex.model.User;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityFinishFragment extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_ENTITY_FINISH";
    private static final String BATCH_ID = "BATCH_ID";

    private FragmentEntityFinishBinding b;
    private TextInputEditText weightEditText;
    private OnFinishRunningListener listener;
    private int batchId;
    private Dialog dialog;

    public EntityFinishFragment() {
        // Required empty public constructor
    }

    public static EntityFinishFragment init(int batchId) {
        EntityFinishFragment fragment = new EntityFinishFragment();

        Bundle args = new Bundle();
        args.putInt(BATCH_ID, batchId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = ((EntityActivity) context).getOnFinishRunningListener();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            batchId = getArguments().getInt(BATCH_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        b = FragmentEntityFinishBinding.inflate(inflater, container, false);

        updateCalories(batchId);
        updateDuration(batchId);
        updateWorkouts(batchId);

        dialog = new Dialog(getActivity());

        dialog.setContentView(R.layout.new_weight_dialog);

        dialog.setOnCancelListener(dialogInterface -> {
            b.imgFinish.animate().translationY(2700).setDuration(1200).setStartDelay(0);
        });

        b.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "WORKING", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.findViewById(R.id.txt_cancel).setOnClickListener(view -> {
            b.imgFinish.animate().translationY(2700).setDuration(1200).setStartDelay(0);
            dialog.cancel();
            listener.onNextButtonPressed();
        });

        weightEditText = ((TextInputEditText) dialog.findViewById(R.id.edit_new_weight));

        String units = "";
        float weight = 0f;

        if (((Integer) MyPreferences.load(getActivity(), MyPreferences.PREF_UNITS)).equals(0)) {
            units = "kg";
            weight = User.getUserData().getWeight();
        } else {
            weight = User.getUserData().getWeight() * 2.205f;
            units = "lb";
        }

        weightEditText.setHint("Weight must be in " + units);
        weightEditText.setText(String.format("%.2f", weight));

        dialog.findViewById(R.id.txt_confirm).setOnClickListener(view -> {
            String sWeight = weightEditText.getText().toString();
            Float w = Float.parseFloat(sWeight);
            if (w < 30f || w > 200f) {
                Toast.makeText(getActivity(), "Please put a valid weight", Toast.LENGTH_SHORT).show();
                return;
            }
            DateKey dk = DateKey.today();
            User.getUserData().updateWeight(getActivity(), dk, w);
            dialog.dismiss();
            listener.onNextButtonPressed();
        });

        Animation animateCongratulations = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out_200);

        b.congratulationsSplash.getRoot().setAnimation(animateCongratulations);

        b.lottieCongrats.pauseAnimation();

        b.congratulationsSplash.txtCaloriesBurned.animate().translationX(0).setDuration(700).setStartDelay(0);

        b.congratulationsSplash.txtBatchDuration.animate().translationX(0).setDuration(700).setStartDelay(700);

        animateCongratulations();

        b.congratulationsSection.txtFinishRepeat.setOnClickListener(view -> {
            listener.onRepeatButtonPressed();
        });

        b.congratulationsSection.txtFinishNext.setOnClickListener(view -> {
            onNextPressed();
        });

        b.btnNext.setOnClickListener(view -> {
            onNextPressed();
        });

        turnOffButtons();

        turnOnButtons();

        return b.getRoot();
    }

    private void turnOffButtons(){
        b.btnNext.setClickable(false);
        b.congratulationsSection.txtFinishNext.setClickable(false);
        b.congratulationsSection.txtFinishRepeat.setClickable(false);
    }

    private void turnOnButtons(){
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b.btnNext.setClickable(true);
                b.congratulationsSection.txtFinishNext.setClickable(true);
                b.congratulationsSection.txtFinishRepeat.setClickable(true);
            }
        }, 3500);

    }

    private void onNextPressed() {
        dialog.show();
        b.imgFinish.animate().translationY(0).setDuration(1200).setStartDelay(0);
    }

    private void animateCongratulations() {
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b.lottieCongrats.playAnimation();
            }
        }, 4500);
    }

    private void updateWorkouts(int batchId) {
        List<Entity.Session> sessions = Entities.getInstance().getSessions(batchId);
        List<String> names = sessions.stream().map(session -> session.getName()).collect(Collectors.toList());
        List<String> unique = new ArrayList<>();
        for (String name : names) {
            if (unique.contains(name)) {
                continue;
            }
            unique.add(name);
        }
        b.congratulationsSection.txtTotalBatchWorkouts.setText(String.valueOf(unique.size()));
    }

    private void updateCalories(int batchId) {
        float calories = 0;

        List<Entity.Session> sessions = Entities.getInstance().getSessions(batchId);
        for (Entity.Session session : sessions) {
            calories += session.getCalories();
        }

        StringBuilder sb = new StringBuilder();

        sb.append("CALORIES BURNED: ");

        sb.append(String.format("%.2f", calories));

        b.congratulationsSplash.txtCaloriesBurned.setText(sb.toString());

        b.congratulationsSection.txtTotalCalories.setText(String.format("%.2f", calories));
    }

    private void updateDuration(int batchId) {
        List<Entity.Session> sessions = Entities.getInstance().getSessions(batchId);

        long duration = 0;

        for (Entity.Session session : sessions) {
            duration += session.getDuration();
        }

        duration = duration / 1000;

        StringBuilder sb = new StringBuilder();

        sb.append("TOTAL DURATION: ");

        if (duration / 60 != 0) {
            sb.append(duration / 60);
            sb.append(" Minutes");
        }
        if (duration % 60 != 0 && duration / 60 != 0) {
            sb.append(" and ");
        }
        if (duration % 60 != 0) {
            sb.append(duration % 60);
            sb.append(" Seconds");
        }
        if (duration % 60 == 0) {
            sb.append(0);
        }

        b.congratulationsSplash.txtBatchDuration.setText(sb.toString());

        b.congratulationsSection.txtTotalDuration.setText(String.format("%02d:%02d", duration / 60, duration % 60));
    }

    public interface OnFinishRunningListener {
        void onNextButtonPressed();

        void onRepeatButtonPressed();

        void onShareButtonPressed();
    }
}