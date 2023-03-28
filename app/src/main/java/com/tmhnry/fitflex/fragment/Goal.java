package com.tmhnry.fitflex.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tmhnry.fitflex.Diet;
import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.databinding.FragmentGoalBinding;
import com.tmhnry.fitflex.databinding.MyTextfieldBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Goal#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Goal extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.FRAGMENT_GOAL";
    FragmentGoalBinding b;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Goal() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Goal.
     */
    // TODO: Rename and change types and number of parameters
    public static Goal newInstance(String param1, String param2) {
        Goal fragment = new Goal();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentGoalBinding.inflate(inflater, container, false);

        validateWeight();
        validateTarget();
        validateMethod();
        validateActivity();

        b.btnSave.setOnClickListener(view -> {
            ((Diet) getActivity()).loadFragment(DietRoutine.FRAGMENT_ID);
        });

        return b.getRoot();
    }

    private String validate(int id) {
        String helperText = "";

//        if (id == R.string.id_email) {
//            String emailText = b.txtFieldEmail.txtFieldEditTxt.getText().toString();
//            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
//                return "must be a valid Email";
//            }
//        }
//        if (id == R.string.id_password) {
//            String passwordText = b.txtFieldPassword.txtFieldEditTxt.getText().toString();
//            if (passwordText.length() < 8) {
//                return "minimum of 8-Character Password";
//            }
//            if (!passwordText.matches(String.valueOf(new Regex(".*[A-Z].*")))) {
//                return "must contain 1 Upper-case Character";
//            }
//            if (!passwordText.matches(String.valueOf(new Regex(".*[a-z].*")))) {
//                return "must Contain 1 Lower-case Character";
//            }
//            if (!passwordText.matches(String.valueOf(new Regex(".*[@#\\$%^&+=].*")))) {
//                return "must Contain 1 Special Character (@#\\$%^&+=)";
//            }
//        }

        return helperText;
    }

    private void validateWeight() {
        MyTextfieldBinding _b = b.editWeight;
        _b.txtFieldContainer.setStartIconVisible(false);
        _b.txtFieldContainer.setEndIconVisible(false);
        _b.txtFieldContainer.setHint("Weight");
        _b.txtFieldEditTxt.setText("80 kg (2017-06-25)");
        _b.txtFieldEditTxt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        _b.txtFieldEditTxt.setKeyListener(null);
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_email));
            }
        });
    }

    private void validateTarget() {
        MyTextfieldBinding _b = b.editTarget;
        _b.txtFieldContainer.setStartIconVisible(false);
        _b.txtFieldContainer.setEndIconVisible(false);
        _b.txtFieldContainer.setHint("Target");
        _b.txtFieldEditTxt.setText("75 kg");
        _b.txtFieldEditTxt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        _b.txtFieldEditTxt.setKeyListener(null);
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_email));
            }
        });
    }

    private void validateMethod() {
        MyTextfieldBinding _b = b.editMethod;
        _b.txtFieldContainer.setStartIconVisible(false);
        _b.txtFieldContainer.setEndIconVisible(false);
        _b.txtFieldContainer.setHint("Method");
        _b.txtFieldEditTxt.setText("Loose 0.5 kg/week");
        _b.txtFieldEditTxt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        _b.txtFieldEditTxt.setKeyListener(null);
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_email));
            }
        });
    }

    private void validateActivity() {
        MyTextfieldBinding _b = b.editActivity;
        _b.txtFieldContainer.setStartIconVisible(false);
        _b.txtFieldContainer.setEndIconVisible(false);
        _b.txtFieldContainer.setHint("Goal");
        _b.txtFieldEditTxt.setText("Moderate exercise (3-5 days per week)");
        _b.txtFieldEditTxt.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        _b.txtFieldEditTxt.setKeyListener(null);
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_email));
            }
        });
    }
}