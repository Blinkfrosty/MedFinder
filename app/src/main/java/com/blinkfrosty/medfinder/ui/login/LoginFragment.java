package com.blinkfrosty.medfinder.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.blinkfrosty.medfinder.MainActivity;
import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.PhotoStorageHelper;
import com.blinkfrosty.medfinder.dataaccess.UserDataAccessHelper;
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
import com.blinkfrosty.medfinder.helpers.SharedPreferenceHelper;
import com.blinkfrosty.medfinder.mapper.GenderCodeMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private ProgressDialogHelper progressDialogHelper;
    private SharedPreferenceHelper preferenceHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        rememberMeCheckBox = view.findViewById(R.id.remember_me);
        progressDialogHelper = new ProgressDialogHelper();
        preferenceHelper = new SharedPreferenceHelper(requireContext());

        setPasswordVisibilityToggle();
        view.findViewById(R.id.login_button).setOnClickListener(v -> loginUser());
        view.findViewById(R.id.google_sign_in_button).setOnClickListener(v -> signInWithGoogle());
        view.findViewById(R.id.forgot_password).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_reset_password)
        );
        view.findViewById(R.id.create_account).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_sign_up)
        );

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        return view;
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!validateInput(email, password)) return;

        progressDialogHelper.showProgressDialog(getActivity(), getString(R.string.logging_in_progress_text));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialogHelper.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        if (rememberMeCheckBox.isChecked()) {
                            preferenceHelper.setLoggedIn(true);
                        }
                        Log.d("LoginFragment", "User logged in with email successfully");
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                        Log.e("LoginFragment", "User login with email failed", task.getException());
                    }
                });
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.email_required));
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.password_required));
            isValid = false;
        }
        return isValid;
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(getActivity(), R.string.google_sign_in_failed, Toast.LENGTH_SHORT).show();
                Log.e("LoginFragment", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        progressDialogHelper.showProgressDialog(getActivity(), getString(R.string.logging_in_progress_text));
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    progressDialogHelper.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        if (Objects.requireNonNull(task.getResult().getAdditionalUserInfo()).isNewUser()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                storeNewGoogleUserDataInDatabase(firebaseUser, acct);
                            }
                        }
                        if (rememberMeCheckBox.isChecked()) {
                            preferenceHelper.setLoggedIn(true);
                        }
                        Log.d("LoginFragment", "Firebase auth login successful for Google account");
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                        Log.e("LoginFragment", "Firebase auth with credential failed for Google acocunt", task.getException());
                    }
                });
    }

    private void storeNewGoogleUserDataInDatabase(FirebaseUser firebaseUser, GoogleSignInAccount acct) {
        PhotoStorageHelper photoStorageHelper = new PhotoStorageHelper(requireContext());
        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String firstName = acct.getGivenName();
        String lastName = acct.getFamilyName();
        String phoneNumber = firebaseUser.getPhoneNumber();
        String genderCode = GenderCodeMapper.OTHER_CODE;
        Uri googlePhotoUri = acct.getPhotoUrl();

        UserDataAccessHelper userDataAccessHelper = new UserDataAccessHelper(requireContext());
        try {
            userDataAccessHelper.setUser(userId, firstName, lastName, email, phoneNumber, genderCode);
            Toast.makeText(requireContext(), getString(R.string.account_created_successfully), Toast.LENGTH_LONG).show();
            Log.d("LoginFragment", "User data saved successfully for new Google user");
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to save user data", Toast.LENGTH_LONG).show();
            Log.e("LoginFragment", "Failed to save user data for new Google user", e);
        }

        if (googlePhotoUri != null) {
            photoStorageHelper.storeRemoteImage(userId, googlePhotoUri, uri -> {
                userDataAccessHelper.updateProfilePictureUri(userId, uri.toString());
                Log.d("LoginFragment", "Google photo uploaded successfully for new Google user");
            }, e -> {
                Log.e("LoginFragment", "Failed to upload photo for new Google user", e);
            });
        }
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
}
