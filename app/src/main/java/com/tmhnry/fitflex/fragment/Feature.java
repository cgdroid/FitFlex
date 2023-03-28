package com.tmhnry.fitflex.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.tmhnry.fitflex.GenderSelection;
import com.tmhnry.fitflex.IntroViewPagerAdapter;
import com.tmhnry.fitflex.MainActivity;
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.ScreenItem;
import com.tmhnry.fitflex.StillImageActivity;
import com.tmhnry.fitflex.databinding.FragmentFeatureBinding;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Feature#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Feature extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FEATURE";

    FragmentFeatureBinding b;
    Animation btnAnim;
    int position = 0;
    List<ScreenItem> screens;

    public Feature() {
    }


    public static Feature newInstance() {
        Feature fragment = new Feature();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        screens = new ArrayList<>();
        screens.add(new ScreenItem("Keep Fit", "DailyFitness provides daily workout routines for all your main muscle groups. In just a few minutes a day, you can build muscles and keep fitness at home without having to go to the gym. No equipment or coach needed, all exercises can be performed with just your body weight.", R.drawable.img_intro1));
        screens.add(new ScreenItem("Full Body Exercise", "The app has workouts for your abs, chest, legs, arms and butt as well as full body workouts. All the workouts are designed by experts. None of them need equipment, so there's no need to go to the gym. Even though it just takes a few minutes a day, it can effectively tone your muscles and help you get six pack abs at home.", R.drawable.img_intro2));
        screens.add(new ScreenItem("Animated Videos", "The warm-up and stretching routines are designed to make sure you exercise in a scientific way. With animations and video guidance for each exercise, you can make sure you use the right form during each exercise.", R.drawable.img_intro3));
        screens.add(new ScreenItem("Goodluck!", "Stick with our home workouts, and you will notice a change in your body in just a few short weeks.", R.drawable.img_intro4));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentFeatureBinding.inflate(inflater, container, false);

        // setup viewpager
        IntroViewPagerAdapter introViewPagerAdapter = new IntroViewPagerAdapter(getActivity(), screens);

        b.screenViewpager.setAdapter(introViewPagerAdapter);

        btnAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.button_animation);

        b.tabIndicator.setupWithViewPager(b.screenViewpager);

        b.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = b.screenViewpager.getCurrentItem();
                if (position < screens.size()) {

                    position++;
                    b.screenViewpager.setCurrentItem(position);

                }

                if (position == screens.size() - 1) { // when we rech to the last screen

                    loaddLastScreen();

                }
            }
        });

        b.tabIndicator.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(b.screenViewpager) {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                if (tab.getPosition() == screens.size() - 1) {
                    super.onTabSelected(tab);
                    loaddLastScreen();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Get Started button click listener

        b.btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_HAS_GENDER )){
                    ((GenderSelection) getActivity()).loadFragment(Gender.FRAGMENT_ID);
                    return;
                }

                if(!(boolean)MyPreferences.load(getActivity(), MyPreferences.PREF_HAS_BELLY )){
                    startActivity(new Intent(getActivity(), StillImageActivity.class));
                    return;
                }

                Intent mainActivity = new Intent(getActivity(), MainActivity.class);
                startActivity(mainActivity);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
            }
        });

        // skip button click listener

        b.tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b.screenViewpager.setCurrentItem(screens.size());
            }
        });

        return b.getRoot();
    }

    private boolean restorePrefData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend", false);
        return isIntroActivityOpnendBefore;

    }

    private void savePrefsData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend", true);
        editor.commit();

    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

        b.btnNext.setVisibility(View.INVISIBLE);

        b.btnGetStarted.setVisibility(View.VISIBLE);
        b.tvSkip.setVisibility(View.INVISIBLE);
        b.tabIndicator.setVisibility(View.INVISIBLE);
        // TODO : ADD an animation the getstarted button
        // setup animation
        b.btnGetStarted.setAnimation(btnAnim);

    }

}