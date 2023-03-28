package com.tmhnry.fitflex.fragment;

import android.content.Context;
import android.net.Uri;
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
import com.tmhnry.fitflex.model.User;
import com.tmhnry.fitflex.databinding.FragmentEntityMainBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import pl.droidsonroids.gif.GifDrawable;

public class EntityMainFragment extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_ENTITY_MAIN";
    private static final String CATEGORY_ID = "CATEGORY_ID";
    private static final String BATCH_ID = "BATCH_ID";
    private static final String POSITION = "POSITION";

    private FragmentEntityMainBinding binding;

    private OnEntityRunningListener listener;

    private Entity entity;

    private long millis;
    private long start;
    private long elapsed;

    private Entity.Type type;
    private _CountDownTimer timer;
    private GifDrawable gif;

    private int categoryId;
    private int batchId;
    private int position;

    private int count;
    private int maxCount;

    public EntityMainFragment() {
        // Required empty public constructor
    }

    public static EntityMainFragment init(int categoryId, int batchId, int position) {
        EntityMainFragment fragment = new EntityMainFragment();
        Bundle args = new Bundle();

        args.putInt(CATEGORY_ID, categoryId);
        args.putInt(BATCH_ID, batchId);
        args.putInt(POSITION, position);

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            categoryId = getArguments().getInt(CATEGORY_ID);
            batchId = getArguments().getInt(BATCH_ID);
            position = getArguments().getInt(POSITION);
        } else {
            categoryId = 0;
            batchId = 0;
            position = 0;
        }

        elapsed = 0;

        entity = Entities.getInstance().getEntities(categoryId).get(position);

        type = entity.getType();

        if (type.equals(Entity.Type.TIMED)) {
            millis = entity.getTypeCount() * 1000;
        } else {
            maxCount = entity.getTypeCount();
        }

//        String uri = "@drawable/" + entity.getImg();
//        String uriPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.mp4_jumping_jacks;
//        int gif = getResources().getIdentifier(uri, null, getPackageName());

        StringBuilder sb = new StringBuilder();
        sb.append("android.resource://");
        sb.append(getActivity().getPackageName());
        sb.append("/drawable/" + entity.getImg());

        Uri uri = Uri.parse(sb.toString());

        try {
            gif = new GifDrawable(getActivity().getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = ((EntityActivity) context).getOnEntityRunningListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentEntityMainBinding.inflate(inflater, container, false);

        binding.gifEntity.setImageDrawable(gif);

        binding.txtEntityName.setText(entity.getName());

        binding.btnPause.setOnClickListener(view -> {
            pauseSession();
        });

        binding.btnNextEntity.setOnClickListener(view -> {
            endSession(true);
            listener.onNextButtonPressed(position);
        });


        binding.btnPrevEntity.setOnClickListener(view -> {
            if (position == 0) {
                return;
            }
            endSession(true);
            listener.onPreviousButtonPressed(position);
        });

        startSession();

        return binding.getRoot();
    }

    public void pauseSession() {
        if (entity.getType().equals(Entity.Type.TIMED)) {
            timer.cancel();
        }

        long end = System.currentTimeMillis();
        elapsed = end - start;
        gif.pause();
        listener.onPauseButtonPressed(position);
    }

    public void endSession(boolean value) {

        if (entity.getType().equals(Entity.Type.TIMED)) {
            timer.cancel();
        }

        gif.pause();

        if (!value) {
            return;
        }

        long duration = System.currentTimeMillis() - start + elapsed;

        Map<Integer, List<Entity.Session>> batches = Entities.getInstance().getBatches();
        List<Entity.Session> sessions = batches.putIfAbsent(batchId, new ArrayList<>());

        Entity.Session session;

        if (type.equals(Entity.Type.TIMED)) {
            session = entity.new Session(batchId, Calendar.getInstance(TimeZone.getDefault()).getTime(), Math.min(duration, entity.getTypeCount() * 1000));
            User.getUserData().addDuration(getActivity(), duration);
        } else {
            int userCount = maxCount;
            session = entity.new Session(batchId,
                    Calendar.getInstance(TimeZone.getDefault()).getTime(),
                    entity.getTypeCount() * 1000L,
                    userCount);
            User.getUserData().addDuration(getActivity(),
                    entity.getTypeCount() * 1000L);
        }

        entity.getSessions().add(session);
        User.getUserData().addCalories(getActivity(), session.getCalories());

        if (sessions == null) {
            sessions = batches.get(batchId);
        }

        List<String> sessionNames = sessions.stream().map(s -> s.getName()).collect(Collectors.toList());

        if (!sessionNames.contains(session.getName())) {
            User.getUserData().addWorkouts(getActivity(), 1);
        }

        sessions.add(session);

    }

    public void startSession() {
        gif.start();

        if (type.equals(Entity.Type.TIMED)) {
            startTimer();
        }

        if (type.equals(Entity.Type.COUNT)) {
            binding.txtEntityCount.setText("x" + entity.getTypeCount());
        }

        start = System.currentTimeMillis();
    }

    public void restartSession() {
        if (entity.getType().equals(Entity.Type.TIMED)) {
            timer.cancel();
        }

        start = 0;
        elapsed = 0;

        if (entity.getType().equals(Entity.Type.TIMED)) {
            millis = entity.getTypeCount() * 1000;
        }

        startSession();
    }

    private void startTimer() {
//        timer = new _CountDownTimer(this, TIMER_COUNT, 1000);
        timer = new _CountDownTimer(new _CountDownTimer.onTickListener() {
            @Override
            public void onTick(int minute, int second, long l) {
                binding.txtEntityCount.setText(String.format("%02d:%02d", minute, second));
                millis = l;
            }

            @Override
            public void onFinish() {
                endSession(true);
                listener.onEntityFinished(position);
            }
        }, millis, 1000);
        timer.start();
    }

    public interface OnEntityRunningListener {
        void onPauseButtonPressed(int position);

        void onPreviousButtonPressed(int position);

        void onNextButtonPressed(int position);

        void onEntityFinished(int position);
    }
}