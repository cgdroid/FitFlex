package com.tmhnry.fitflex.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.SuperscriptSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tmhnry.fitflex.MainActivity;
import com.tmhnry.fitflex.MyDialog;
import com.tmhnry.fitflex.MyPreferences;
import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.StillImageActivity;
import com.tmhnry.fitflex.databinding.FragmentGenderBinding;

public class Gender extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.GENDER";

    private MyDialog bmiDialog;
    TextView proceedBtn;
    TextView skipBtn;
    EditText editBmi;


    FragmentGenderBinding b;

    public Gender() {
    }

    public static Gender newInstance() {
        Gender fragment = new Gender();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        bmiDialog = MyDialog.fromEdit(context, editBmiListener, "BMI");
    }

    private final MyDialog.DialogListener editBmiListener = new MyDialog.DialogListener() {
        @Override
        public void onCreate(View view) {
            Context context = getContext();
            Toast.makeText(context, String.valueOf(bmiDialog), Toast.LENGTH_SHORT).show();
//            proceedBtn = bmiDialog.findViewById(R.id.txt_confirm);
//            skipBtn = bmiDialog.findViewById(R.id.txt_cancel);
//            proceedBtn.setText("Proceed");
//            skipBtn.setText("Skip");
            EditText editText = (EditText) view;
            editText.setTextColor(context.getColor(R.color.gray));

            editText.setTextSize(15f);
            ColorStateList color = ColorStateList.valueOf(context.getColor(R.color.light_100));
            ViewCompat.setBackgroundTintList(editText, color);

            String units = "";
            float bmi = 0f;

            if (((Integer) MyPreferences.load(context, MyPreferences.PREF_UNITS)).equals(0)) {
                units = "kg/m";
                bmi = (Float) MyPreferences.load(context, MyPreferences.PREF_BMI);
            } else {
                units = "lb/ft";
                bmi = (Float) MyPreferences.load(context, MyPreferences.PREF_BMI) * 0.062428f;
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
            Context context = getContext();
            EditText editText = (EditText) view;

            String sBmi = editText.getText().toString().trim();
            boolean confirm = false;
            float bmi;

            try {
                bmi = Float.parseFloat(sBmi);

                if (bmi < 0) {
                    throw new Exception();
                }
                if (((Integer) MyPreferences.load(context, MyPreferences.PREF_UNITS)).equals(0)) {
                    MyPreferences.save(context, MyPreferences.PREF_BMI, bmi);
                } else {
                    MyPreferences.save(context, MyPreferences.PREF_BMI, bmi * 1 / 0.062428f);
                }

                editText.setText(String.format("%.2f", bmi));
                confirm = true;

            } catch (Exception e) {
                Toast.makeText(context, "Cannot parse BMI", Toast.LENGTH_SHORT).show();
            }
            if(confirm){
                startActivity(new Intent(getActivity(), StillImageActivity.class));
                getActivity().finish();
            }
            return confirm;
        }

        @Override
        public void onCancel(View view) {
            Context context = getContext();
            EditText editText = (EditText) view;
            float bmi = 0f;

            if (((Integer) MyPreferences.load(context, MyPreferences.PREF_UNITS)).equals(0)) {
                bmi = (Float) MyPreferences.load(context, MyPreferences.PREF_BMI);
            } else {
                bmi = (Float) MyPreferences.load(context, MyPreferences.PREF_BMI) * 0.062428f;
            }
            editText.setText(String.format("%.2f", bmi));
            MyPreferences.save(context, MyPreferences.PREF_HAS_BELLY, true);
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentGenderBinding.inflate(inflater, container, false);

        b.cardSelectFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPreferences.save(getActivity(), MyPreferences.PREF_GENDER, 1);
                onCardSelect();
            }
        });

        b.cardSelectMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyPreferences.save(getActivity(), MyPreferences.PREF_GENDER, 0);
                onCardSelect();
            }
        });

        return b.getRoot();
    }

    private void onCardSelect() {
        MyPreferences.save(getActivity(), MyPreferences.PREF_HAS_GENDER, true);
        if (!(boolean) MyPreferences.load(getActivity(), MyPreferences.PREF_HAS_BELLY)) {
            bmiDialog.open();
        }
    }

}