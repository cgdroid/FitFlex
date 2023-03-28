package com.tmhnry.fitflex;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.tmhnry.fitflex.databinding.ActivityGenderSelectionBinding;
import com.tmhnry.fitflex.fragment.Feature;
import com.tmhnry.fitflex.fragment.Gender;
import com.tmhnry.fitflex.miscellaneous.Tools;

public class GenderSelection extends AppCompatActivity {

    private ActivityGenderSelectionBinding b;
    private int progress;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityGenderSelectionBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        Tools.hideSystemBars(this);
        loadFragment(Feature.FRAGMENT_ID);
    }


    public void loadFragment(String fragmentId) {
        Fragment fragment;

        switch (fragmentId) {
            case Gender.FRAGMENT_ID:
                fragment = Gender.newInstance();
                break;
            default:
                fragment = Feature.newInstance();
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(b.fragment.getId(), fragment)
                .commit();
    }
}
