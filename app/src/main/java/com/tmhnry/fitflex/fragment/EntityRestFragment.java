package com.tmhnry.fitflex.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tmhnry.fitflex.extension._CountDownTimer;
import com.tmhnry.fitflex.model.Entities;
import com.tmhnry.fitflex.model.Entity;
import com.tmhnry.fitflex.EntityActivity;
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.databinding.FragmentEntityRestBinding;

public class EntityRestFragment extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_ENTITY_REST";

    private static final String CATEGORY_ID = "CATEGORY_ID";
    private static final String POSITION = "POSITION";

    private OnRestRunningListener listener;
    private FragmentEntityRestBinding binding;

    private Entity entity;

    private long millis;

    private Entity.Type type;
    private _CountDownTimer timer;

    private int categoryId;
    private int position;

    public EntityRestFragment() {
        // Required empty public constructor
    }

    public static EntityRestFragment init(int categoryId, int position) {
        EntityRestFragment fragment = new EntityRestFragment();
        Bundle args = new Bundle();

        args.putInt(CATEGORY_ID, categoryId);
        args.putInt(POSITION, position);

        fragment.setArguments(args);
        return fragment;
    }

    public void addTimer(long millisToAdd) {
        if (millis < (60 * 60 * 1000 - millisToAdd)) {
            millis += millisToAdd;
        } else {
            millis = 60 * 60 * 1000;
        }
        timer.cancel();
        startTimer();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        millis = (long) (((int) MyPreferences.load(context, MyPreferences.PREF_REST_TIME)) * 1000);
        listener = ((EntityActivity) context).getOnRestRunningListener();
    }

    private void startTimer() {
//        timer = new _CountDownTimer(this, TIMER_COUNT, 1000);
        timer = new _CountDownTimer(new _CountDownTimer.onTickListener() {
            @Override
            public void onTick(int minute, int second, long l) {
                binding.txtRestTimer.setText(String.format("%02d:%02d", minute, second));
                millis = l;
            }

            @Override
            public void onFinish() {
                timer.cancel();
                listener.onRestFinished();
            }
        }, millis, 1000);
        timer.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            categoryId = getArguments().getInt(CATEGORY_ID);
            position = getArguments().getInt(POSITION);
        } else {
            categoryId = 0;
            position = 0;
        }

        entity = Entities.getInstance().getEntities(categoryId).get(position);

        type = entity.getType();

//        String uri = "@drawable/" + entity.getImg();
//        int gif = getResources().getIdentifier(uri, null, getPackageName());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEntityRestBinding.inflate(inflater, container, false);

        String uri = "@drawable/" + entity.getImg();

        int gif = getResources().getIdentifier(uri, null, getActivity().getPackageName());

        binding.gifNextEntity.setImageResource(gif);

        binding.txtNextEntityName.setText(entity.getName());

        int entitiesLength = Entities.getInstance().getEntities(categoryId).size();

        StringBuilder sb = new StringBuilder();
        sb.append(position + 1);
        sb.append("/");
        sb.append(entitiesLength);

        binding.txtRelativePosition.setText(sb.toString());

        if (type.equals(Entity.Type.TIMED)) {
            int duration = entity.getTypeCount();
            binding.txtNextEntityCount.setText(String.format("%02d:%02d", duration / 60, duration % 60));
        }
        if (type.equals(Entity.Type.COUNT)) {
            StringBuilder sbCount = new StringBuilder();

            sbCount.append("x");
            sbCount.append(entity.getTypeCount());

            binding.txtNextEntityCount.setText(sbCount.toString());
        }

        binding.btnAddTimer.setOnClickListener(view -> {
            addTimer(20000);
        });

        binding.btnSkipRest.setOnClickListener(view -> {
            timer.cancel();
            listener.onSkipButtonPressed();
        });

        startTimer();

        return binding.getRoot();
    }

    public interface OnRestRunningListener {

        void onSkipButtonPressed();

        void onRestFinished();
    }
}