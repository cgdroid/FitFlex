package com.tmhnry.fitflex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tmhnry.fitflex.database.Firebase;
import com.tmhnry.fitflex.databinding.ActivityLoginRegisterBinding;
import com.tmhnry.fitflex.fragment.Login;
import com.tmhnry.fitflex.fragment.Register;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.tmhnry.fitflex.model.User;

import java.util.Map;

public class LoginRegister extends AppCompatActivity {

    private Dialog dLoading;
    private Dialog dAlert;
    private TextView vPos;
    private TextView vNeg;
    private TextView vMes;
    private TextView vTit;
    private ActivityLoginRegisterBinding binding;


    private final Firebase.OnLoginListener loginListener = new Firebase.OnLoginListener() {
        @Override
        public void onStart() {
            onShowDialog("Logging In...");
        }

        @Override
        public void onSuccess(Map<String, Object> userCredentials) {

            if (!MyPreferences.load(LoginRegister.this, MyPreferences.PREF_UID)
                    .equals(Firebase.getCurrentUser().getUid())) {
                MyPreferences.reset(LoginRegister.this);
                User.delete();
            }

            MyPreferences.save(LoginRegister.this, MyPreferences.PREF_UID, Firebase.getCurrentUser().getUid());
            MyPreferences.save(LoginRegister.this, MyPreferences.PREF_EMAIL_ADDRESS, userCredentials.get(Firebase.KEY_EMAIL));
            MyPreferences.save(LoginRegister.this, MyPreferences.PREF_FIRST_NAME, userCredentials.get(Firebase.KEY_FIRST_NAME));
            MyPreferences.save(LoginRegister.this, MyPreferences.PREF_LAST_NAME, userCredentials.get(Firebase.KEY_LAST_NAME));

            a();

            Toast.makeText(LoginRegister.this, "Welcome to Daily Fitness", Toast.LENGTH_SHORT).show();
            dLoading.dismiss();
        }

        @Override
        public void onFailed() {
            dLoading.dismiss();
            vTit.setText("Invalid Credentials");
            vMes.setText("Your email address or password is incorrect.");
            vPos.setText("Okay");
            vPos.setOnClickListener(view -> {
                dAlert.dismiss();
            });
            vNeg.setVisibility(View.INVISIBLE);
            dAlert.show();
        }
    };
    private final Firebase.OnRegisterListener registerListener = new Firebase.OnRegisterListener() {
        @Override
        public void onStart() {
            onShowDialog("Please Wait...");
        }

        @Override
        public void onSuccess(Map<String, Object> userCredentials) {
            dLoading.dismiss();
            vTit.setText("Registration Successful");
            vMes.setText("Do you want to log in with these credentials?");
            vPos.setText("Yes");
            vPos.setOnClickListener(view -> {
                dAlert.dismiss();
                Firebase.login(userCredentials, loginListener);
            });
            vNeg.setVisibility(View.VISIBLE);
            vNeg.setText("No");
            vNeg.setOnClickListener(view -> {
                dAlert.dismiss();
            });
            dAlert.show();
        }

        @Override
        public void onUserExists() {
            dLoading.dismiss();
            vTit.setText("Email Already Exists");
            vMes.setText("A user with that email has already existed. Please provide a different email address.");
            vPos.setText("Okay");
            vPos.setOnClickListener(view -> {
                dAlert.dismiss();
            });
            vNeg.setVisibility(View.INVISIBLE);
            dAlert.show();
        }

        @Override
        public void onFailed() {
            dLoading.dismiss();
            vTit.setText("Processing Failed");
            vMes.setText("An error occurred during registration. Please try again.");
            vPos.setText("Okay");
            vPos.setOnClickListener(view -> {
                dAlert.dismiss();
            });
            vNeg.setVisibility(View.INVISIBLE);
            dAlert.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Tools.hideSystemBars(this);

        dLoading = new Dialog(this);

        dLoading.setCancelable(false);

        dLoading.setContentView(R.layout.dialog_loading_indicator);

        dLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dAlert = new Dialog(this);

        dAlert.setContentView(R.layout.dialog_alert_notification);

        dAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vPos = dAlert.findViewById(R.id.txt_positive);
        vNeg = dAlert.findViewById(R.id.txt_negative);
        vMes = dAlert.findViewById(R.id.txt_alert_message);
        vTit = dAlert.findViewById(R.id.dialog_title);

        vNeg.setVisibility(View.INVISIBLE);

        loadFragment(Login.FRAGMENT_ID);

        Firebase.init();

        if (Firebase.getCurrentUser() == null) {
            loadFragment(Login.FRAGMENT_ID);
            return;
        }

        a();

    }

    private void a() {
        Intent i;

        i = new Intent(this, MainActivity.class);

        if (!(boolean) MyPreferences.load(this, MyPreferences.PREF_HAS_GENDER)) {
            i = null;
            i = new Intent(this, GenderSelection.class);
            startActivity(i);
            finish();
            return;
        }

        if (!(boolean) MyPreferences.load(this, MyPreferences.PREF_HAS_BELLY)) {
            //
            i = null;
            i = new Intent(this, StillImageActivity.class);
            startActivity(i);
            finish();
            return;
        }

        startActivity(i);
        finish();
        return;
    }

    public void onShowDialog(String message) {
        ((TextView) dLoading.findViewById(R.id.txt_loading_message)).setText(message);
        dLoading.show();
    }

    public void loadFragment(String fragmentId) {
        Fragment fragment;

        if (fragmentId == Login.FRAGMENT_ID) {
            fragment = Login.newInstance();
        } else {
            fragment = Register.newInstance("", "");
        }
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(binding.fragment.getId(), fragment)
                .commit();
    }

    public Firebase.OnRegisterListener getRegisterListener() {
        return registerListener;
    }

    public Firebase.OnLoginListener getLoginListener() {
        return loginListener;
    }

    @Override
    public void onBackPressed() {

    }
}