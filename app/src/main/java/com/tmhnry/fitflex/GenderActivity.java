package com.tmhnry.fitflex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tmhnry.fitflex.databinding.ActivityGenderBinding;
import com.tmhnry.fitflex.fragment.SettingsFragment;
import com.tmhnry.fitflex.miscellaneous.Tools;
import com.tmhnry.fitflex.model.User;
import com.tmhnry.fitflex.viewmodel.Main;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GenderActivity extends AppCompatActivity {

    public static final String ROUTE_ID = "com.example.fitflex.ACTIVITY_SEX";

    ActivityGenderBinding binding;
    MyDialog mDialogSex;
    MyDialog mDialogBY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenderBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        Tools.hideSystemBars(this);

        binding.genderTxt.setText(User.getUserData().getSex().name());
        binding.birthYearTxt.setText(String.format("%d", User.getUserData().getBirthYear()));

        Main.ViewModel viewModel = new Main().getModel(this);

        binding.genderCard.setOnClickListener(v -> {
            mDialogSex.open();
        });

        binding.birthYearCard.setOnClickListener(v -> {
            mDialogBY.open();
        });

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GenderActivity.this, MainActivity.class);
                intent.putExtra(MainActivity.ROUTE_ID, SettingsFragment.FRAGMENT_ID);
                startActivity(intent);
                finish();
            }
        });

        mDialogSex = MyDialog.fromSelection(this, selectSexListener, "Gender");
        mDialogBY = MyDialog.fromEdit(this, editBirthYearListener, "Birth Year");


//        mDialogSex = new DialogFactory.Unique(this, User.getUserData().getSex().name(), sexes.stream().map(sex -> sex.name()).collect(Collectors.toList()));
    }

    private final MyDialog.DialogListener selectSexListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {

            RadioGroup radioGroup = (RadioGroup) view;
            List<User.Sex> sexes = new ArrayList<>();
            sexes.add(User.Sex.MALE);
            sexes.add(User.Sex.FEMALE);

            for (int i = 0; i < sexes.size(); i++) {
                String label = sexes.get(i).name();
                RadioButton button = new RadioButton(GenderActivity.this);
                // tag and id are different
                button.setId(i);
//            button.setTag(i, sexes.get(i));
                button.setTag(sexes.get(i));
                button.setButtonDrawable(R.drawable.style_radio_button);
                button.setPadding(20, 0, 0, 0);
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(15, 18, 15, 15);
                button.setTextColor(GenderActivity.this.getColor(R.color.gray));
                button.setLayoutParams(params);
//            button.setHeight(16);
//            button.setWidth(16);
                button.setText(label.substring(0, 1).toUpperCase() + label.substring(1, label.length()).toLowerCase());
                radioGroup.addView(button);
                if (label.equals(User.getUserData().getSex().name())) {
                    radioGroup.check(i);
                }
            }

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                User user = User.getUserData();
//                user.setSex(sexes.get(i));
//                binding.genderTxt.setText(user.getSex().name());
//                mDialogSex.pop();
                }
            });
        }

        @Override
        public boolean onConfirm(View view) {
            RadioGroup radioGroup = (RadioGroup) view;
            RadioButton button = (RadioButton) radioGroup.getChildAt(radioGroup.getCheckedRadioButtonId());

            User user = User.getUserData();
            user.updateSex(GenderActivity.this, (User.Sex) button.getTag());

            binding.genderTxt.setText(user.getSex().name());

            return true;
        }

        @Override
        public void onCancel(View view) {
            RadioGroup radioGroup = (RadioGroup) view;
            String sex = User.getUserData().getSex().name();
            radioGroup.clearCheck();
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                if (((User.Sex) radioGroup.getChildAt(i).getTag()).equals(User.getUserData().getSex())) {
                    radioGroup.check(i);
                }
            }
        }

    };

    private final MyDialog.DialogListener editBirthYearListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {

            EditText editText = (EditText) view;
            LocalDate date = LocalDate.now();
            editText.setTextColor(GenderActivity.this.getColor(R.color.gray));
            editText.setTextSize(15f);

            ColorStateList color = ColorStateList.valueOf(GenderActivity.this.getColor(R.color.light_100));

            ViewCompat.setBackgroundTintList(editText, color);
            editText.setHint(String.format("Please enter from 1901 to %d", date.getYear()));
            editText.setText(String.valueOf(User.getUserData().getBirthYear()));

        }

        @Override
        public boolean onConfirm(View view) {

            EditText editText = (EditText) view;
            String sYear = editText.getText().toString().trim();

            User user = User.getUserData();
            int year = user.getBirthYear();

            boolean confirm = false;
            try {
                year = Integer.parseInt(sYear);
                LocalDate date = LocalDate.now();
                if (year < 1901 || LocalDate.now().getYear() < year) {
                    throw new Exception();
                }
                confirm = true;
            } catch (Exception e) {
                Toast.makeText(GenderActivity.this, "Invalid Birth Year", Toast.LENGTH_SHORT).show();
            }

            user.updateBirthYear(GenderActivity.this, year);
            binding.birthYearTxt.setText(String.format("%d", User.getUserData().getBirthYear()));

            return confirm;
        }

        @Override
        public void onCancel(View view) {
            EditText editText = (EditText) view;
            editText.setText(String.valueOf(User.getUserData().getBirthYear()));
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GenderActivity.this, MainActivity.class);
        intent.putExtra(MainActivity.ROUTE_ID, SettingsFragment.FRAGMENT_ID);
        startActivity(intent);
        finish();
    }
}