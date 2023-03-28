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
import com.tmhnry.fitflex.databinding.FragmentLoginBinding;
import com.tmhnry.fitflex.databinding.MyTextfieldBinding;

import java.util.HashMap;
import java.util.Map;

import kotlin.text.Regex;

public class Login extends Fragment {
    public static final String FRAGMENT_ID = "com.example.fitflex.LOGIN";
    private FragmentLoginBinding b;
    private Dialog dAlert;
    private TextView vPos;
    private TextView vNeg;
    private TextView vTit;
    private TextView vMes;
    public Login() {
        // Required empty public constructor
    }

    public static Login newInstance() {
        Login fragment = new Login();
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

        b = FragmentLoginBinding.inflate(inflater, container, false);

        validateEmail();
        validatePassword();

        Map<String, Object> users = new HashMap<>();

        dAlert = new Dialog(getActivity());

        dAlert.setContentView(R.layout.dialog_alert_notification);

        dAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        vPos = dAlert.findViewById(R.id.txt_positive);
        vNeg = dAlert.findViewById(R.id.txt_negative);
        vMes = dAlert.findViewById(R.id.txt_alert_message);
        vTit = dAlert.findViewById(R.id.dialog_title);

        vNeg.setVisibility(View.INVISIBLE);

//        inputLayout = inflate.findViewById(R.id.txt_field_container);
//        users.put("table-name", "users");
//        users.put("table-size", 0);

        b.btnRegister.setOnClickListener(v -> {
//            FirebaseFirestore.getInstance().collection("table-properties").add(users).addOnSuccessListener(documentReference -> {
//                Toast.makeText(getActivity(), documentReference.getId(), Toast.LENGTH_SHORT);
//            });
//            FirebaseFirestore.getInstance().collection("table-properties")
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d(TAG, document.getId() + " => " + document.getData());
//                                }
//                            } else {
//                                Log.w(TAG, "Error getting documents.", task.getException());
//                            }
//                        }
//                    });

//            FirebaseFirestore.getInstance().collection("properties").document("users").get().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    Toast.makeText(getActivity(), doc.toString(), Toast.LENGTH_SHORT).show();
//                }
//            });
            ((LoginRegister) getActivity()).loadFragment(Register.FRAGMENT_ID);
        });

        b.btnLogin.setOnClickListener(v -> {
            submitForm();
        });

//        vEmail.setPaintFlags(View.INVISIBLE);
//        vPass.setPaintFlags(View.INVISIBLE);

        return b.getRoot();
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

    //
    private void submitForm() {
        MyTextfieldBinding email = b.txtFieldEmail;
        MyTextfieldBinding password = b.txtFieldPassword;
        email.txtFieldContainer.setHelperText(validate(R.string.id_email));
        password.txtFieldContainer.setHelperText(validate(R.string.id_password));

        boolean validEmail = email.txtFieldContainer.getHelperText() == null || email.txtFieldContainer.getHelperText().toString().isEmpty();
        boolean validPassword = password.txtFieldContainer.getHelperText() == null || password.txtFieldContainer.getHelperText().toString().isEmpty();

        if (validEmail && validPassword) {
            Map<String, Object> userCredentials = new HashMap<>();

            String em = email.txtFieldEditTxt.getText().toString();
            userCredentials.put(Firebase.KEY_EMAIL, em);

            String pw = password.txtFieldEditTxt.getText().toString();
            userCredentials.put(Firebase.KEY_PASSWORD, pw);

            Firebase.login(userCredentials, ((LoginRegister) getActivity()).getLoginListener());
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

    //
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

    //
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
}