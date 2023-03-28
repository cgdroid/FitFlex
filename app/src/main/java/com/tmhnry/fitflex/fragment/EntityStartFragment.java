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
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.databinding.FragmentEntityStartBinding;

import pl.droidsonroids.gif.GifDrawable;

public class EntityStartFragment extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_ENTITY_START";

    private static final String CATEGORY_ID = "CATEGORY_ID";
    private static final String POSITION = "POSITION";

    private FragmentEntityStartBinding binding;
    private OnStartRunningListener listener;

    private int categoryId;
    private int position;
    private GifDrawable gif;
    private Entity entity;
    private _CountDownTimer timer;
    private long duration;

    public EntityStartFragment() {
    }

    public static EntityStartFragment init(int categoryId, int position) {
        EntityStartFragment fragment = new EntityStartFragment();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_ID, categoryId);
        args.putInt(POSITION, position);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getInt(CATEGORY_ID);
            position = getArguments().getInt(POSITION);
        }

        duration = 1000 * (int) MyPreferences.load(getActivity(), MyPreferences.PREF_COUNTDOWN_TIME);

        entity = Entities.getInstance().getEntities(categoryId).get(position);

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
        listener = ((EntityActivity) context).getOnStartRunningListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEntityStartBinding.inflate(inflater, container, false);

        binding.txtStartEntityName.setText(entity.getName().toUpperCase());

        binding.gifEntity.setImageDrawable(gif);

        binding.imgStartEntity.setOnClickListener(view -> {
                    gif.stop();
                    timer.cancel();
                    listener.onStartButtonPressed();
                }
        );

        gif.start();

        startTimer();

        return binding.getRoot();
    }

    private void startTimer() {
//        timer = new _CountDownTimer(this, TIMER_COUNT, 1000);
        int iDuration = (int) duration / 1000;

        timer = new _CountDownTimer(new _CountDownTimer.onTickListener() {
            @Override
            public void onTick(int minute, int second, long l) {
                binding.txtCountdownTime.setText(String.valueOf(second));
                binding.progressCircular.setProgress(100 - 100 * second / iDuration);
            }

            @Override
            public void onFinish() {
                timer.cancel();
                gif.stop();
                listener.onCountdownFinished();
            }

        }, duration, 1000);
        timer.start();
    }

    public interface OnStartRunningListener {

        void onCountdownFinished();

        void onStartButtonPressed();

    }
}