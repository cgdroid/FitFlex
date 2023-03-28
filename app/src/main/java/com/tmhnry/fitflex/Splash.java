package com.tmhnry.fitflex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.tmhnry.fitflex.miscellaneous.Tools;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        ImageView sexSelectionImage = findViewById(R.id.sex_selection_image);
//        AnimationDrawable animationDrawable = (AnimationDrawable) sexSelectionImage.getBackground();

//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(2000);
//        animationDrawable.start();

        Tools.hideSystemBars(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                animationDrawable.stop();
                Intent intent = new Intent(Splash.this, LoginRegister.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}