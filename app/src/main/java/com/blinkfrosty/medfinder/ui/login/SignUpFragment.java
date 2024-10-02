package com.blinkfrosty.medfinder.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blinkfrosty.medfinder.MainActivity;
import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.UserDataAccessHelper;
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
import com.blinkfrosty.medfinder.helpers.SharedPreferenceHelper;
import com.blinkfrosty.medfinder.mapper.GenderCodeMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUpFragment extends Fragment {

    private FirebaseAuth mAuth;
    private UserDataAccessHelper userDataAccessHelper;
    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneNumberEditText,
            passwordEditText, confirmPasswordEditText;
    private RadioGroup genderRadioGroup;
    private ProgressDialogHelper progressDialogHelper;
    private SharedPreferenceHelper preferenceHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        userDataAccessHelper = new UserDataAccessHelper(requireContext());

        firstNameEditText = view.findViewById(R.id.first_name);
        lastNameEditText = view.findViewById(R.id.last_name);
        emailEditText = view.findViewById(R.id.email);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        passwordEditText = view.findViewById(R.id.password);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        genderRadioGroup = view.findViewById(R.id.gender_radio_group);

        view.findViewById(R.id.sign_up_button).setOnClickListener(v -> signUpUser());
        setPasswordVisibilityToggle();
        setConfirmPasswordVisibilityToggle();
        progressDialogHelper = new ProgressDialogHelper();
        preferenceHelper = new SharedPreferenceHelper(requireContext());

        return view;
    }

    private void signUpUser() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = genderRadioGroup.findViewById(selectedGenderId);
        String genderCode = selectedGenderButton != null ? GenderCodeMapper.getCode(selectedGenderButton.getText().toString()) : "";

        if (!validateInputs(firstName, lastName, email, password, confirmPassword, genderCode)) return;

        progressDialogHelper.showProgressDialog(getActivity(), getString(R.string.signing_up_progress_text));

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    try {
                        userDataAccessHelper.setUser(userId, firstName, lastName, email, phoneNumber, genderCode);
                        progressDialogHelper.dismissProgressDialog();
                        preferenceHelper.setLoggedIn(true); // Note: Enabling auto login for new users by default
                        Toast.makeText(requireContext(), getString(R.string.account_created_successfully), Toast.LENGTH_LONG).show();
                        Log.d("SignUpFragment", "User created with email: " + email);

                        startActivity(new Intent(getActivity(), MainActivity.class));
                        requireActivity().finish();
                    } catch (Exception e) {
                        progressDialogHelper.dismissProgressDialog();
                        Toast.makeText(requireContext(), "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("SignUpFragment", "Failed to save user data", e);
                    }
                }
            } else {
                progressDialogHelper.dismissProgressDialog();
                Toast.makeText(requireContext(), "Sign Up Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                Log.e("SignUpFragment", "Failed to register user with email", task.getException());
            }
        });
    }

    private boolean validateInputs(String firstName, String lastName, String email, String password, String confirmPassword, String genderCode) {
        boolean isValid = true;
        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError(getString(R.string.first_name_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError(getString(R.string.last_name_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.email_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.password_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError(getString(R.string.confirm_password_required));
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordEditText.setError(getString(R.string.confirm_password_does_not_match));
            isValid = false;
        }
        if (TextUtils.isEmpty(genderCode)) {
            Toast.makeText(requireContext(), R.string.gender_required, Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPasswordVisibilityToggle() {
        passwordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Right drawable
                if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[drawableEnd].getBounds().width())) {
                    if (passwordEditText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                        passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                    } else {
                        passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    }
                    passwordEditText.setSelection(passwordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setConfirmPasswordVisibilityToggle() {
        confirmPasswordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int drawableEnd = 2; // Right drawable
                if (event.getRawX() >= (confirmPasswordEditText.getRight() - confirmPasswordEditText.getCompoundDrawables()[drawableEnd].getBounds().width())) {
                    if (confirmPasswordEditText.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                        confirmPasswordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_on, 0);
                    } else {
                        confirmPasswordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        confirmPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    }
                    confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });
    }
}
