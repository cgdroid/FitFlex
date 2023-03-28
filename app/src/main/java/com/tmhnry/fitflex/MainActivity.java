package com.tmhnry.fitflex;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.tmhnry.fitflex.database.Firebase;
import com.tmhnry.fitflex.databinding.ActivityMainBinding;
import com.tmhnry.fitflex.fragment.HomeFragment;
import com.tmhnry.fitflex.fragment.ReportsFragment;
import com.tmhnry.fitflex.fragment.SettingsFragment;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.tmhnry.fitflex.model.DateKey;
import com.tmhnry.fitflex.model.Entities;
import com.tmhnry.fitflex.model.Foods;
import com.tmhnry.fitflex.model.User;
import com.tmhnry.fitflex.view.ViewUnit;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity implements HomeFragment.Listener {
    public static final String ROUTE_ID = "com.example.fitflex.ACTIVITY_MAIN";

    public final HistoryTrackerAdapter.UnitListener openHistoryDayListener = new HistoryTrackerAdapter.UnitListener() {
        @Override
        public void openDay(int position) {

        }
    };
    private final MyDialog.DialogListener selectPerformanceListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {

            RadioGroup radioGroup = (RadioGroup) view;

            for (int i = 0; i < 2; i++) {
                String label = i == 0 ? "Accurate" : "Fast";
                RadioButton button = new RadioButton(MainActivity.this);

                button.setId(i);

                button.setButtonDrawable(R.drawable.style_radio_button);
                button.setPadding(20, 0, 0, 0);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 18, 15, 15);
                button.setTextColor(MainActivity.this.getColor(R.color.gray));
                button.setLayoutParams(params);

                button.setText(label);
                radioGroup.addView(button);

                if (i == (int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_POSE_PERFORMANCE)) {
                    radioGroup.check(i);
                }
            }
        }

        @Override
        public boolean onConfirm(View view) {

            RadioGroup radioGroup = (RadioGroup) view;

            User user = User.getUserData();

            int checkedId = radioGroup.getCheckedRadioButtonId();

            MyPreferences.save(MainActivity.this, MyPreferences.PREF_POSE_PERFORMANCE, checkedId);

            SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.FRAGMENT_ID);
            fragment.updatePerformanceView();

            return true;
        }

        @Override
        public void onCancel(View view) {
            RadioGroup radioGroup = (RadioGroup) view;
            radioGroup.clearCheck();
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if (i == (int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)) {
                    radioGroup.check(i);
                }
            }
        }
    };
    private final MyDialog.DialogListener selectUnitsListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {

            RadioGroup radioGroup = (RadioGroup) view;

            for (int i = 0; i < 2; i++) {
                String label = i == 0 ? "Metric" : "Imperial";
                RadioButton button = new RadioButton(MainActivity.this);

                button.setId(i);

                button.setButtonDrawable(R.drawable.style_radio_button);
                button.setPadding(20, 0, 0, 0);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 18, 15, 15);
                button.setTextColor(MainActivity.this.getColor(R.color.gray));
                button.setLayoutParams(params);

                button.setText(label);
                radioGroup.addView(button);
                if (i == (int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)) {
                    radioGroup.check(i);
                }
            }
        }

        @Override
        public boolean onConfirm(View view) {

            RadioGroup radioGroup = (RadioGroup) view;

            User user = User.getUserData();

            int checkedId = radioGroup.getCheckedRadioButtonId();

            MyPreferences.save(MainActivity.this, MyPreferences.PREF_UNITS, checkedId);

            SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.FRAGMENT_ID);
            fragment.updateUnitsView();

            return true;
        }

        @Override
        public void onCancel(View view) {
            RadioGroup radioGroup = (RadioGroup) view;
            radioGroup.clearCheck();
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if (i == (int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)) {
                    radioGroup.check(i);
                }
            }
        }
    };
    private final MyDialog.DialogListener editWeightListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {
            EditText editText = (EditText) view;

            editText.setTextColor(MainActivity.this.getColor(R.color.gray));
            editText.setTextSize(15f);

            ColorStateList color = ColorStateList.valueOf(MainActivity.this.getColor(R.color.light_100));
            ViewCompat.setBackgroundTintList(editText, color);

            String units = "";
            float weight = 0f;

            if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                units = "kg";
                weight = User.getUserData().getWeight();
            } else {
                weight = User.getUserData().getWeight() * 2.205f;
                units = "lb";
            }

            editText.setHint("Weight must be in " + units);
            editText.setText(String.format("%.2f", weight));
        }

        @Override
        public boolean onConfirm(View view) {
            boolean confirm = false;

            EditText editText = (EditText) view;
            String sWeight = editText.getText().toString().trim();
            float weight;

            try {
                weight = Float.parseFloat(sWeight);

                if (weight < 0) {
                    throw new Exception();
                }
                if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                    User.getUserData().updateWeight(MainActivity.this, DateKey.today(), weight);
                } else {
                    User.getUserData().updateWeight(MainActivity.this, DateKey.today(), weight * 1 / 2.205f);
                }

                editText.setText(String.format("%.2f", weight));
                confirm = true;

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Invalid Weight", Toast.LENGTH_SHORT).show();
            }

            ReportsFragment fragment = (ReportsFragment) getSupportFragmentManager().findFragmentByTag(ReportsFragment.FRAGMENT_ID);
            fragment.updateChart();
            return confirm;
        }

        @Override
        public void onCancel(View view) {
            EditText editText = (EditText) view;
            Float weight = 0f;

            if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                weight = User.getUserData().getWeight();
            } else {
                weight = User.getUserData().getWeight() * 2.205f;
            }

            editText.setText(String.format("%.2f", weight));

        }
    };

    private final MyDialog.DialogListener editBmiListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {
            EditText editText = (EditText) view;
            editText.setTextColor(MainActivity.this.getColor(R.color.gray));

            editText.setTextSize(15f);
            ColorStateList color = ColorStateList.valueOf(MainActivity.this.getColor(R.color.light_100));
            ViewCompat.setBackgroundTintList(editText, color);

            String units = "";
            float bmi = 0f;

            if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                units = "kg/m";
                bmi = User.getUserData().getBmi();
            } else {
                units = "lb/ft";
                bmi = User.getUserData().getBmi() * 0.062428f;
            }

            SpannableString span = new SpannableString("3");
            span.setSpan(new SuperscriptSpan(), 0, 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

            SpannableStringBuilder spanBuilder = new SpannableStringBuilder();
            spanBuilder.append("BMI must be in " + units);
            spanBuilder.append(span);

            editText.setHint(spanBuilder);
            editText.setText(String.format("%.2f", bmi));
        }

        @Override
        public boolean onConfirm(View view) {
            EditText editText = (EditText) view;

            String sBmi = editText.getText().toString().trim();
            boolean confirm = false;
            float bmi;

            try {
                bmi = Float.parseFloat(sBmi);

                if (bmi < 0) {
                    throw new Exception();
                }
                if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                    User.getUserData().updateBmi(MainActivity.this, bmi);
                } else {
                    User.getUserData().updateBmi(MainActivity.this, bmi * 1 / 0.062428f);
                }

                editText.setText(String.format("%.2f", bmi));
                confirm = true;

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Cannot parse BMI", Toast.LENGTH_SHORT).show();
            }

            ReportsFragment fragment = (ReportsFragment) getSupportFragmentManager().findFragmentByTag(ReportsFragment.FRAGMENT_ID);
            fragment.updateBmiView();

            return confirm;
        }

        @Override
        public void onCancel(View view) {
            EditText editText = (EditText) view;
            float bmi = 0f;

            if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                bmi = User.getUserData().getBmi();
            } else {
                bmi = User.getUserData().getBmi() * 0.062428f;
            }

            editText.setText(String.format("%.2f", bmi));
        }
    };
    private final MyDialog.DialogListener editHeightListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {
            EditText editText = (EditText) view;

            editText.setTextColor(MainActivity.this.getColor(R.color.gray));
            editText.setTextSize(15f);

            ColorStateList color = ColorStateList.valueOf(MainActivity.this.getColor(R.color.light_100));
            ViewCompat.setBackgroundTintList(editText, color);

            String units = "";
            float height = 0f;

            if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                units = "cm";
                height = User.getUserData().getHeight();
            } else {
                height = User.getUserData().getHeight() * 3.281f / 100;
                units = "ft";
            }

            editText.setHint("Height must be in " + units);
            editText.setText(String.format("%.2f", height));
        }

        @Override
        public boolean onConfirm(View view) {
            EditText editText = (EditText) view;
            String sHeight = editText.getText().toString().trim();

            float height = User.getUserData().getHeight();
            boolean confirm = false;

            try {
                height = Float.parseFloat(sHeight);

                if (height < 0) {
                    throw new Exception();
                }
                if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                    User.getUserData().updateHeight(MainActivity.this, height);
                } else {
                    User.getUserData().updateHeight(MainActivity.this, height * 100 / 3.281f);
                }

                editText.setText(String.format("%.2f", height));
                confirm = true;
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Invalid Height", Toast.LENGTH_SHORT).show();
            }

            ReportsFragment fragment = (ReportsFragment) getSupportFragmentManager().findFragmentByTag(ReportsFragment.FRAGMENT_ID);
            fragment.updateHeightView();
            return confirm;
        }

        @Override
        public void onCancel(View view) {

            EditText editText = (EditText) view;
            float height = 0f;

            if (((Integer) MyPreferences.load(MainActivity.this, MyPreferences.PREF_UNITS)).equals(0)) {
                height = User.getUserData().getHeight();
            } else {
                height = User.getUserData().getHeight() * 3.281f / 100;
            }

            editText.setText(String.format("%.2f", height));
        }
    };

    private ViewUnit.ImageButton vAddRest;
    private ViewUnit.ImageButton vSubRest;
    private ViewUnit.ImageButton vAddCountdown;
    private ViewUnit.ImageButton vSubCountdown;
    private AtomicInteger timeRest;
    private AtomicInteger timeCountdown;
    private TextView vRestTime;
    private final ViewUnit.ImageButton.MotionEventListener subRestTimeListener = new ViewUnit.ImageButton.MotionEventListener() {
        private final AtomicBoolean isActionDown = new AtomicBoolean(false);

        @Override
        public void onActionDown() {
            isActionDown.set(true);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isActionDown.get()) {
                        return;
                    }
                    if (timeRest.get() == 5) {
                        return;
                    }
                    timeRest.getAndDecrement();
                    vRestTime.setText(String.valueOf(timeRest.get()));
                    new Handler().postDelayed(this, 150);
                }
            };

            runOnUiThread(runnable);
        }

        @Override
        public void onActionUp() {
            isActionDown.set(false);
        }
    };
    private final ViewUnit.ImageButton.MotionEventListener addRestTimeListener = new ViewUnit.ImageButton.MotionEventListener() {
        private final AtomicBoolean isActionDown = new AtomicBoolean(false);

        @Override
        public void onActionDown() {
            isActionDown.set(true);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isActionDown.get()) {
                        return;
                    }
                    if (timeRest.get() == 180) {
                        return;
                    }
                    timeRest.getAndIncrement();
                    vRestTime.setText(String.valueOf(timeRest.get()));
                    new Handler().postDelayed(this, 150);
                }
            };

            runOnUiThread(runnable);
        }

        @Override
        public void onActionUp() {
            isActionDown.set(false);
        }
    };
    private final MyDialog.DialogListener setRestTimeListener = new MyDialog.DialogListener() {
        @SuppressLint("ClickableViewAccessibility")

        @Override
        public void onCreate(View view) {
            LinearLayout layout = (LinearLayout) view;
            timeRest = new AtomicInteger((int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_REST_TIME));

            vSubRest = new ViewUnit.ImageButton(MainActivity.this, subRestTimeListener);
            vSubRest.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24);
            vSubRest.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.transparent)));
            vSubRest.setImageTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.light_100)));

            layout.addView(vSubRest);

            vRestTime = new TextView(MainActivity.this);
            vRestTime.setText(String.valueOf(timeRest.get()));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(190, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(65, 0, 65, 0);
            vRestTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            vRestTime.setLayoutParams(params);
            vRestTime.setTextSize(30);
            layout.addView(vRestTime);

//            runOnUiThread(runnable);
//            thread = new Thread(runnable);

            vAddRest = new ViewUnit.ImageButton(MainActivity.this, addRestTimeListener);
            vAddRest.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
            vAddRest.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.transparent)));
            vAddRest.setImageTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.light_100)));

            layout.addView(vAddRest);
        }


        @Override
        public boolean onConfirm(View view) {

            LinearLayout layout = (LinearLayout) view;
            MyPreferences.save(MainActivity.this, MyPreferences.PREF_REST_TIME, timeRest.get());
            ((SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.FRAGMENT_ID))
                    .updateTrainRestView();

            return true;

        }

        @Override
        public void onCancel(View view) {

            LinearLayout layout = (LinearLayout) view;
            vRestTime.setText(String.valueOf((int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_REST_TIME)));

        }
    };
    private TextView vCountdownTime;
    private final ViewUnit.ImageButton.MotionEventListener subCountdownTimeListener = new ViewUnit.ImageButton.MotionEventListener() {
        private final AtomicBoolean isActionDown = new AtomicBoolean(false);

        @Override
        public void onActionDown() {
            isActionDown.set(true);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isActionDown.get()) {
                        return;
                    }
                    if (timeCountdown.get() == 10) {
                        return;
                    }
                    timeCountdown.getAndDecrement();
                    vCountdownTime.setText(String.valueOf(timeCountdown.get()));
                    new Handler().postDelayed(this, 300);
                }
            };

            runOnUiThread(runnable);
        }

        @Override
        public void onActionUp() {
            isActionDown.set(false);
        }
    };
    private final ViewUnit.ImageButton.MotionEventListener addCountdownTimeListener = new ViewUnit.ImageButton.MotionEventListener() {
        private final AtomicBoolean isActionDown = new AtomicBoolean(false);

        @Override
        public void onActionDown() {
            isActionDown.set(true);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (!isActionDown.get()) {
                        return;
                    }
                    if (timeCountdown.get() == 15) {
                        return;
                    }
                    timeCountdown.getAndIncrement();
                    vCountdownTime.setText(String.valueOf(timeCountdown.get()));
                    new Handler().postDelayed(this, 300);
                }
            };

            runOnUiThread(runnable);
        }

        @Override
        public void onActionUp() {
            isActionDown.set(false);
        }
    };
    private final MyDialog.DialogListener setCountdownTimeListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {

            LinearLayout layout = (LinearLayout) view;
            timeCountdown = new AtomicInteger((int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_COUNTDOWN_TIME));

            vSubCountdown = new ViewUnit.ImageButton(MainActivity.this, subCountdownTimeListener);
            vSubCountdown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_left_24);
            vSubCountdown.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.transparent)));
            vSubCountdown.setImageTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.light_100)));

            layout.addView(vSubCountdown);

            vCountdownTime = new TextView(MainActivity.this);
            vCountdownTime.setText(String.valueOf(timeCountdown.get()));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(190, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(65, 0, 65, 0);
            vCountdownTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            vCountdownTime.setLayoutParams(params);
            vCountdownTime.setTextSize(30);
            layout.addView(vCountdownTime);

//            runOnUiThread(runnable);
//            thread = new Thread(runnable);

            vAddCountdown = new ViewUnit.ImageButton(MainActivity.this, addCountdownTimeListener);
            vAddCountdown.setImageResource(R.drawable.ic_baseline_keyboard_arrow_right_24);
            vAddCountdown.setBackgroundTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.transparent)));
            vAddCountdown.setImageTintList(ColorStateList.valueOf(MainActivity.this.getColor(R.color.light_100)));

            layout.addView(vAddCountdown);

        }

        @Override
        public boolean onConfirm(View view) {

            LinearLayout layout = (LinearLayout) view;
            MyPreferences.save(MainActivity.this, MyPreferences.PREF_COUNTDOWN_TIME, timeCountdown.get());
            ((SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.FRAGMENT_ID))
                    .updateCountdownView();
            return true;

        }

        @Override
        public void onCancel(View view) {

            LinearLayout layout = (LinearLayout) view;
            vRestTime.setText(String.valueOf((int) MyPreferences.load(MainActivity.this, MyPreferences.PREF_REST_TIME)));

        }
    };
    private ActivityMainBinding binding;
    private MeowBottomNavigation bottomNavigation;
    private TextView vMajor;
    private TextView vMinor;
    private Toolbar vToolbar;
    private Thread thread;
    private Dialog dialogAccount;

    public MyDialog.DialogListener getSelectPerformanceListener() {
        return selectPerformanceListener;
    }

    String fragmentId;

    public void setAccountImageVisibility(int visibility) {
        binding.imgAccount.setVisibility(visibility);
    }


    private void loadFragment(String fragmentId) {
        Fragment fragment;
        this.fragmentId = fragmentId;
        switch (fragmentId) {
            case ReportsFragment.FRAGMENT_ID:
                fragment = ReportsFragment.init(true);
                break;
            case SettingsFragment.FRAGMENT_ID:
                fragment = SettingsFragment.init(true);
                break;
            default:
                fragment = HomeFragment.init(true);
        }
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.main_frame, fragment, fragmentId).commit();
    }

    @Override
    public void onScrollChange(int trackerHeight, int scrollX, int scrollY) {
        scrollY = Math.min(scrollY, trackerHeight - vToolbar.getHeight());
        float percentage = (float) (trackerHeight - scrollY - vToolbar.getHeight()) / (trackerHeight - vToolbar.getHeight());
        vToolbar.setAlpha((float) 1 - percentage);
        if (scrollY > 50) {
            vMajor.setTextColor(Color.BLUE);
            vMinor.setTextColor(Color.BLACK);
        } else {
            vMajor.setTextColor(Color.WHITE);
            vMinor.setTextColor(Color.WHITE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Tools.hideSystemBars(this);

        String id = getIntent().getStringExtra(ROUTE_ID);

        Entities.getInstance(this);

        User.getUserData(this);

        thread = new Thread(new Foods
                .Retriever(this)
                .setData(R.raw.nutrients_csvfile));

        thread.start();

        createNotificationChannel();

        MyPreferences.save(this, MyPreferences.PREF_EXERCISE_HOURS_PER_WEEK, 1000);

        if (id == null) {
            id = HomeFragment.FRAGMENT_ID;
        }

        int btmNavId = 0;

        if (id.equals(ReportsFragment.FRAGMENT_ID)) {
            btmNavId = 1;
        } else if (id.equals(SettingsFragment.FRAGMENT_ID)) {
            btmNavId = 2;
        }

        vToolbar = binding.toolbar;
        vMajor = binding.major;
        vMinor = binding.minor;

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_account_circle_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(0, R.drawable.ic_baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_baseline_settings_applications_24));

        dialogAccount = new Dialog(this);
        dialogAccount.setContentView(R.layout.dialog_account_preferences);
        dialogAccount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        String email = (String) MyPreferences.load(MainActivity.this,
                MyPreferences.PREF_EMAIL_ADDRESS);

        String firstName = (String) MyPreferences.load(MainActivity.this,
                MyPreferences.PREF_FIRST_NAME);

        String lastName = (String) MyPreferences.load(MainActivity.this,
                MyPreferences.PREF_LAST_NAME);

        ((TextView) dialogAccount.findViewById(R.id.txt_account_email))
                .setText(email);

        ((TextView) dialogAccount.findViewById(R.id.txt_account_first_name))
                .setText("First Name: " + firstName);

        ((TextView) dialogAccount.findViewById(R.id.txt_account_last_name))
                .setText("Last Name: " + lastName);

        ((TextView) dialogAccount.findViewById(R.id.txt_log_out)).setOnClickListener(view -> {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Firebase.logout();
                Toast.makeText(MainActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginRegister.class));
                MainActivity.this.finish();
            } else {
                Toast.makeText(MainActivity.this, "Invalid Operation", Toast.LENGTH_SHORT).show();
            }
            dialogAccount.dismiss();
        });

        ((TextView) dialogAccount.findViewById(R.id.txt_confirm)).setOnClickListener(view -> {
            dialogAccount.dismiss();
        });

        binding.imgAccount.setOnClickListener(view -> {
            dialogAccount.show();
        });

        loadFragment(id);

        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {

            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                String id = HomeFragment.FRAGMENT_ID;

                if (model.getId() == 1) {
                    id = ReportsFragment.FRAGMENT_ID;
                } else if (model.getId() == 2) {
                    id = SettingsFragment.FRAGMENT_ID;
                }

                loadFragment(id);
                return null;
            }
        });

        bottomNavigation.show(btmNavId, true);

        if ((int) MyPreferences.load(this, MyPreferences.PREF_EXERCISE_HOURS_PER_WEEK) == 0) {
            bottomNavigation.setCount(0, "1");
            bottomNavigation.setCountBackgroundColor(getColor(R.color.custom_red));
        }
    }

    public void updateBottomNavigationCount() {
        if ((int) MyPreferences.load(this, MyPreferences.PREF_EXERCISE_HOURS_PER_WEEK) == 0) {
            bottomNavigation.setCount(0, "1");
            bottomNavigation.setCountBackgroundColor(getColor(R.color.custom_red));
            return;
        }
        bottomNavigation.setCountBackgroundColor(getColor(R.color.transparent));
        bottomNavigation.setCount(0, "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        binding = null;
    }

    @Override
    public void onBackPressed() {
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Daily Fitness";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("fitflex", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public int getToolbarHeight() {
        return vToolbar.getHeight();
    }

    public void setToolbarAlpha(float alpha) {
        vToolbar.setAlpha(alpha);
    }

    public void setMajorTitleColor(int color) {
        vMajor.setTextColor(color);
    }

    public void setMinorTitleColor(int color) {
        vMinor.setTextColor(color);
    }

    public void setMajorTitleText(String title) {
        vMajor.setText(title);
    }

    public void setMinorTitleText(String title) {
        vMinor.setText(title);
    }

    public MyDialog.DialogListener getEditBmiListener() {
        return editBmiListener;
    }

    public MyDialog.DialogListener getEditHeightListener() {
        return editHeightListener;
    }

    public MyDialog.DialogListener getEditWeightListener() {
        return editWeightListener;
    }

    public MyDialog.DialogListener getSelectUnitsListener() {
        return selectUnitsListener;
    }

    public MyDialog.DialogListener getSetRestTimeListener() {
        return setRestTimeListener;
    }

    public MyDialog.DialogListener getSetCountdownTimeListener() {
        return setCountdownTimeListener;
    }

    public HistoryTrackerAdapter.UnitListener getOpenHistoryDayListener() {
        return openHistoryDayListener;
    }
}