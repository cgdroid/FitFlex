package com.tmhnry.fitflex.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tmhnry.fitflex.database.Firebase;
import com.tmhnry.fitflex.LoginRegister;
import com.tmhnry.fitflex.R;
import com.tmhnry.fitflex.databinding.FragmentRegisterBinding;
import com.tmhnry.fitflex.databinding.MyTextfieldBinding;

import java.util.HashMap;
import java.util.Map;

import kotlin.text.Regex;

public class Register extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.REGISTER";

    private FragmentRegisterBinding b;
    private Dialog dAlert;
    private TextView vPos;
    private TextView vNeg;
    private TextView vTit;
    private TextView vMes;

    public Register() {
    }

    public static Register newInstance(String param1, String param2) {
        Register fragment = new Register();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentRegisterBinding.inflate(inflater, container, false);

        dAlert = new Dialog(getActivity());

        dAlert.setContentView(R.layout.dialog_alert_notification);

        dAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vPos = dAlert.findViewById(R.id.txt_positive);
        vNeg = dAlert.findViewById(R.id.txt_negative);
        vMes = dAlert.findViewById(R.id.txt_alert_message);
        vTit = dAlert.findViewById(R.id.dialog_title);

        vNeg.setVisibility(View.INVISIBLE);

        validateEmail();
        validateFirstName();
        validateLastName();
        validatePassword();

        b.btnRegister.setOnClickListener(v -> {
            submitForm();
        });


        b.btnLogin.setOnClickListener(v -> {
            ((LoginRegister) getActivity()).loadFragment(Login.FRAGMENT_ID);
        });

        return b.getRoot();
    }

    private void submitForm() {
        MyTextfieldBinding email = b.txtFieldEmail;
        MyTextfieldBinding password = b.txtFieldPassword;
        MyTextfieldBinding firstName = b.txtFieldFirstName;
        MyTextfieldBinding lastName = b.txtFieldLastName;

        email.txtFieldContainer.setHelperText(validate(R.string.id_email));
        password.txtFieldContainer.setHelperText(validate(R.string.id_password));
        firstName.txtFieldContainer.setHelperText(validate(R.string.id_first_name));
        lastName.txtFieldContainer.setHelperText(validate(R.string.id_last_name));
//
        boolean validEmail = email.txtFieldContainer.getHelperText() == null || email.txtFieldContainer.getHelperText().toString().isEmpty();
        boolean validPassword = password.txtFieldContainer.getHelperText() == null || password.txtFieldContainer.getHelperText().toString().isEmpty();
        boolean validFirstName = email.txtFieldContainer.getHelperText() == null || email.txtFieldContainer.getHelperText().toString().isEmpty();
        boolean validLastName = password.txtFieldContainer.getHelperText() == null || password.txtFieldContainer.getHelperText().toString().isEmpty();

        if (validEmail && validPassword) {
            String em = email.txtFieldEditTxt.getText().toString();
            String pw = password.txtFieldEditTxt.getText().toString();
            String fn = firstName.txtFieldEditTxt.getText().toString();
            String ln = lastName.txtFieldEditTxt.getText().toString();

            Map<String, Object> userCredentials = new HashMap<>();

            userCredentials.put(Firebase.KEY_EMAIL, em);
            userCredentials.put(Firebase.KEY_PASSWORD, pw);
            userCredentials.put(Firebase.KEY_FIRST_NAME, fn);
            userCredentials.put(Firebase.KEY_LAST_NAME, ln);
//
            Firebase.register(userCredentials, ((LoginRegister) getActivity()).getRegisterListener());
//            resetForm();
        } else
            invalidForm();
    }

    private void resetForm() {
        MyTextfieldBinding email = b.txtFieldEmail;
        MyTextfieldBinding password = b.txtFieldPassword;

        vTit.setText("Form submitted");
        vMes.setText("Processing");
        vPos.setText("Continue");
        vPos.setOnClickListener(view -> {
            email.txtFieldContainer.setHelperText("Required");
            password.txtFieldContainer.setHelperText("Required");
            dAlert.dismiss();
        });

    }

    private void invalidForm() {
        MyTextfieldBinding email = b.txtFieldEmail;
        MyTextfieldBinding password = b.txtFieldPassword;

        String message = "";

        if (email.txtFieldContainer.getHelperText() != null)
            message += "Email: " + email.txtFieldContainer.getHelperText();
        if (password.txtFieldContainer.getHelperText() != null)
            message += "\n\nPassword: " + password.txtFieldContainer.getHelperText();

        vTit.setText("Invalid Credentials");
        vMes.setText(message);
        vPos.setText("Okay");
        vPos.setOnClickListener(view -> {
            dAlert.dismiss();
        });

        dAlert.show();
    }

    private String validate(int id) {
        String helperText = "";
        if (id == R.string.id_email) {
            String emailText = b.txtFieldEmail.txtFieldEditTxt.getText().toString();
            if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                return "must be a valid Email";
            }
        }
        if (id == R.string.id_password) {
            String passwordText = b.txtFieldPassword.txtFieldEditTxt.getText().toString();
            if (passwordText.length() < 8) {
                return "minimum of 8-Character Password";
            }
            if (!passwordText.matches(String.valueOf(new Regex(".*[A-Z].*")))) {
                return "must contain 1 Upper-case Character";
            }
            if (!passwordText.matches(String.valueOf(new Regex(".*[a-z].*")))) {
                return "must Contain 1 Lower-case Character";
            }
            if (!passwordText.matches(String.valueOf(new Regex(".*[@#\\$%^&+=].*")))) {
                return "must Contain 1 Special Character (@#\\$%^&+=)";
            }
        }
        if (id == R.string.id_first_name) {
            String firstNameText = b.txtFieldFirstName.txtFieldEditTxt.getText().toString();
            if (firstNameText.length() < 3) {
                return "minimum of 3 Characters";
            }
            if (firstNameText.matches(String.valueOf(new Regex(".*[@#\\$%^&+=].*")))) {
                return "cannot contain Special Characters (@#\\$%^&+=)";
            }
            if (firstNameText.matches(String.valueOf(new Regex(".*[1-9].*")))) {
                return "can only accept letters";
            }
        }
        if (id == R.string.id_last_name) {
            String lastNameText = b.txtFieldLastName.txtFieldEditTxt.getText().toString();
            if (lastNameText.length() < 3) {
                return "minimum of 3 Characters";
            }
            if (lastNameText.matches(String.valueOf(new Regex(".*[@#\\$%^&+=].*")))) {
                return "cannot contain Special Characters (@#\\$%^&+=)";
            }
            if (lastNameText.matches(String.valueOf(new Regex(".*[1-9].*")))) {
                return "can only accept letters";
            }
        }
        return helperText;
    }

    private void validateEmail() {
        MyTextfieldBinding _b = b.txtFieldEmail;
        _b.txtFieldContainer.setStartIconDrawable(R.drawable.ic_baseline_mail_24);
        _b.txtFieldContainer.setEndIconVisible(false);
        _b.txtFieldContainer.setHint("Email Address");
        _b.txtFieldEditTxt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_email));
            }
        });
    }

    private void validatePassword() {
        MyTextfieldBinding _b = b.txtFieldPassword;
        _b.txtFieldContainer.setHint("Password");
        _b.txtFieldContainer.setStartIconDrawable(R.drawable.ic_baseline_lock_24);
//        _b.txtFieldContainer.setHelperText("must be a valid email");
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_password));
            }
        });
    }

    private void validateFirstName() {
        MyTextfieldBinding _b = b.txtFieldFirstName;
        _b.txtFieldContainer.setHint("First Name");
        _b.txtFieldContainer.setEndIconVisible(false);
        _b.txtFieldContainer.setStartIconDrawable(R.drawable.ic_baseline_account_circle_24);
        _b.txtFieldEditTxt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_first_name));
            }
        });
    }

    private void validateLastName() {
        MyTextfieldBinding _b = b.txtFieldLastName;
        _b.txtFieldContainer.setHint("Last Name");
        _b.txtFieldContainer.setEndIconVisible(false);
        _b.txtFieldContainer.setStartIconDrawable(R.drawable.ic_baseline_account_circle_24);
        _b.txtFieldEditTxt.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        _b.txtFieldEditTxt.setOnFocusChangeListener((v, focused) -> {
            if (!focused) {
                _b.txtFieldContainer.setHelperText(validate(R.string.id_last_name));
            }
        });
    }
}