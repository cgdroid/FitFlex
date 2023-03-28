package com.tmhnry.fitflex;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tmhnry.fitflex.databinding.ActivityEntityBinding;
import com.tmhnry.fitflex.databinding.EntityActivityOverlayBinding;
import com.tmhnry.fitflex.fragment.EntityFinishFragment;
import com.tmhnry.fitflex.fragment.EntityMainFragment;
import com.tmhnry.fitflex.fragment.EntityRestFragment;
import com.tmhnry.fitflex.fragment.EntityStartFragment;
import com.tmhnry.fitflex.fragment.ReportsFragment;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.tmhnry.fitflex.model.Entities;
import com.tmhnry.fitflex.model.Entity;

import java.util.Calendar;
import java.util.TimeZone;

public class EntityActivity extends AppCompatActivity {
    public static final String ROUTE_ID = "com.example.fitflex.ACTIVITY_ENTITY";
    public static final String ARG1 = "_ARG1";
    public static final String ARG2 = "_ARG2";

    private String currentFragmentId;
    private ActivityEntityBinding binding;
    private int categoryId;
    private int batchId;
    private int position;
    private final EntityStartFragment.OnStartRunningListener onStartRunningListener = new EntityStartFragment.OnStartRunningListener() {
        @Override
        public void onCountdownFinished() {

//            if (isEntityAutomated()) {
//                Intent intent = new Intent(EntityActivity.this, LivePreviewActivity.class);
//                int[] args = new int[4];
//                args[0] = categoryId;
//                args[1] = batchId;
//                args[2] = position;
//                args[3] = entitiesLength;
//                intent.putExtra(LivePreviewActivity.ROUTE_ID, args);
//                startActivity(intent);
//                return;
//            }
            loadFragment(EntityMainFragment.FRAGMENT_ID);
        }

        @Override
        public void onStartButtonPressed() {

//            if (isEntityAutomated()) {
//                Intent intent = new Intent(EntityActivity.this, LivePreviewActivity.class);
//                int[] args = new int[4];
//                args[0] = categoryId;
//                args[1] = batchId;
//                args[2] = position;
//                args[3] = entitiesLength;
//                intent.putExtra(LivePreviewActivity.ROUTE_ID, args);
//                startActivity(intent);
//                return;
//            }
            loadFragment(EntityMainFragment.FRAGMENT_ID);
        }
    };
    private final EntityFinishFragment.OnFinishRunningListener onFinishRunningListener = new EntityFinishFragment.OnFinishRunningListener() {
        @Override
        public void onNextButtonPressed() {
            Intent intent = new Intent(EntityActivity.this, MainActivity.class);
            intent.putExtra(MainActivity.ROUTE_ID, ReportsFragment.FRAGMENT_ID);
            startActivity(intent);
            finish();
        }

        @Override
        public void onRepeatButtonPressed() {
            position = 0;
            batchId = Calendar.getInstance(TimeZone.getDefault()).getTime().hashCode();
            loadFragment(EntityStartFragment.FRAGMENT_ID);
        }

        @Override
        public void onShareButtonPressed() {

        }

    };
    private final EntityRestFragment.OnRestRunningListener onRestRunningListener = new EntityRestFragment.OnRestRunningListener() {
        @Override
        public void onSkipButtonPressed() {
//            if (isEntityAutomated()) {
//                Intent intent = new Intent(EntityActivity.this, LivePreviewActivity.class);
//                int[] args = new int[4];
//                args[0] = categoryId;
//                args[1] = batchId;
//                args[2] = position;
//                args[3] = entitiesLength;
//                intent.putExtra(LivePreviewActivity.ROUTE_ID, args);
//                startActivity(intent);
//                return;
//            }
            loadFragment(EntityMainFragment.FRAGMENT_ID);
        }

        @Override
        public void onRestFinished() {
//            Intent intent = new Intent(EntityActivity.this, LivePreviewActivity.class);
//            startActivity(intent);
//            if (isEntityAutomated()) {
//                Intent intent = new Intent(EntityActivity.this, LivePreviewActivity.class);
//                int[] args = new int[4];
//                args[0] = categoryId;
//                args[1] = batchId;
//                args[2] = position;
//                args[3] = entitiesLength;
//                intent.putExtra(LivePreviewActivity.ROUTE_ID, args);
//                startActivity(intent);
//                return;
//            }
            loadFragment(EntityMainFragment.FRAGMENT_ID);
        }
    };

    private boolean isEntityAutomated() {
        return Entities.getInstance().getEntities(categoryId).get(position).getIsAuto();
    }

    private int entitiesLength;
    private final EntityMainFragment.OnEntityRunningListener onEntityRunningListener = new EntityMainFragment.OnEntityRunningListener() {
        @Override
        public void onPauseButtonPressed(int position) {

            Entity entity = Entities.getInstance().getEntities(categoryId).get(position);
            String uri = "@drawable/" + entity.getImg();
            int gifId = getResources().getIdentifier(uri, null, getPackageName());

            binding.entityActivityOverlay.txtOverlayEntityName
                    .setText(entity.getName());
            binding.entityActivityOverlay
                    .gifOverlayEntity
                    .setImageResource(gifId);
            binding.entityActivityOverlay
                    .getRoot()
                    .setVisibility(View.VISIBLE);
        }

        @Override
        public void onPreviousButtonPressed(int position) {
            if (position == 0) {
                return;
            }
            updatePosition(position - 1);
            loadFragment(EntityRestFragment.FRAGMENT_ID);
        }

        @Override
        public void onNextButtonPressed(int position) {
            if (position == entitiesLength - 1) {
                loadFragment(EntityFinishFragment.FRAGMENT_ID);
                return;
            }
            updatePosition(position + 1);
            loadFragment(EntityRestFragment.FRAGMENT_ID);
        }

        @Override
        public void onEntityFinished(int position) {
            if (position == entitiesLength - 1) {
                loadFragment(EntityFinishFragment.FRAGMENT_ID);
                return;
            }
            updatePosition(position + 1);
            loadFragment(EntityRestFragment.FRAGMENT_ID);
        }
    };

    public EntityStartFragment.OnStartRunningListener getOnStartRunningListener() {
        return onStartRunningListener;
    }

    public EntityFinishFragment.OnFinishRunningListener getOnFinishRunningListener() {
        return onFinishRunningListener;
    }

    private void updatePosition(int position) {
        this.position = position;
    }

    public EntityRestFragment.OnRestRunningListener getOnRestRunningListener() {
        return onRestRunningListener;
    }

    public EntityMainFragment.OnEntityRunningListener getOnEntityRunningListener() {
        return onEntityRunningListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntityBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        Tools.hideSystemBars(this);

        categoryId = getIntent().getIntArrayExtra(ROUTE_ID + ARG1)[0];
        batchId = getIntent().getIntArrayExtra(ROUTE_ID + ARG1)[1];
        position = getIntent().getIntArrayExtra(ROUTE_ID + ARG1)[2];
        entitiesLength = getIntent().getIntArrayExtra(ROUTE_ID + ARG1)[3];

        String mainFragmentId = getIntent().getStringExtra(ROUTE_ID + ARG2);
        if (mainFragmentId == null) {
            mainFragmentId = EntityStartFragment.FRAGMENT_ID;
        }

        EntityActivityOverlayBinding overlayBinding = binding.entityActivityOverlay;

        overlayBinding.btnQuitEntity.setOnClickListener(view ->
                onQuitButtonPressed()
        );

        overlayBinding.btnRestartEntity.setOnClickListener(view ->
                onRestartButtonPressed()
        );

        overlayBinding.btnResumeEntity.setOnClickListener(view ->
                onResumeButtonPressed()
        );

        overlayBinding.imgBackEntity.setOnClickListener(view ->
                onResumeButtonPressed()
        );

        loadFragment(mainFragmentId);
    }

    public void loadFragment(String fragmentId) {
        Fragment fragment;
        this.currentFragmentId = fragmentId;

        switch (fragmentId) {
            case EntityMainFragment.FRAGMENT_ID:
                fragment = EntityMainFragment.init(categoryId, batchId, position);
                break;

            case EntityRestFragment.FRAGMENT_ID:
                fragment = EntityRestFragment.init(categoryId, position);
                break;

            case EntityFinishFragment.FRAGMENT_ID:
                fragment = EntityFinishFragment.init(batchId);
                break;

            default:
                fragment = EntityStartFragment.init(categoryId, position);
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(binding.frame.getId(), fragment, fragmentId)
                .commit();
    }

    @Override
    public void onBackPressed() {

        switch (currentFragmentId) {

            case EntityMainFragment.FRAGMENT_ID:
                EntityMainFragment fragment = (EntityMainFragment) getSupportFragmentManager()
                        .findFragmentByTag(EntityMainFragment.FRAGMENT_ID);

                if (fragment != null) {
                    fragment.pauseSession();
                }
                break;
            case EntityRestFragment.FRAGMENT_ID:
            case EntityStartFragment.FRAGMENT_ID:
                break;
            default:
                startActivity(new Intent(this, MainActivity.class));
        }

    }

    private void onQuitButtonPressed() {
        EntityMainFragment fragment = (EntityMainFragment) getSupportFragmentManager()
                .findFragmentByTag(EntityMainFragment.FRAGMENT_ID);

        if (fragment != null) {
            fragment.endSession(false);
        }

        binding.entityActivityOverlay.getRoot().setVisibility(View.GONE);

        Intent intent = new Intent(EntityActivity.this, EntityCategoryActivity.class);
        intent.putExtra(EntityCategoryActivity.ROUTE_ID, categoryId);
        startActivity(intent);
    }

    private void onRestartButtonPressed() {
        EntityMainFragment fragment = (EntityMainFragment) getSupportFragmentManager()
                .findFragmentByTag(EntityMainFragment.FRAGMENT_ID);

        if (fragment != null) {
            fragment.restartSession();
        }
        binding.entityActivityOverlay.getRoot().setVisibility(View.GONE);
    }

    private void onResumeButtonPressed() {
        EntityMainFragment fragment = (EntityMainFragment) getSupportFragmentManager()
                .findFragmentByTag(EntityMainFragment.FRAGMENT_ID);

        if (fragment != null) {
            fragment.startSession();
        }
        binding.entityActivityOverlay.getRoot().setVisibility(View.GONE);
    }
}