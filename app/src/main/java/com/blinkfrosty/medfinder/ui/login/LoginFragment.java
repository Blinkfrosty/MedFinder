package com.blinkfrosty.medfinder.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blinkfrosty.medfinder.MainActivity;
import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
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

public class LoginFragment extends Fragment {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText emailEditText, passwordEditText;
    private CheckBox rememberMeCheckBox;
    private ProgressDialogHelper progressDialogHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        rememberMeCheckBox = view.findViewById(R.id.remember_me);
        progressDialogHelper = new ProgressDialogHelper();

        view.findViewById(R.id.login_button).setOnClickListener(v -> loginUser());
        view.findViewById(R.id.google_sign_in_button).setOnClickListener(v -> signInWithGoogle());
        // TODO: Implement the forgot password and create account functionality
//        view.findViewById(R.id.forgot_password).setOnClickListener(v -> ((SignInActivity) getActivity()).loadFragment(new ResetPasswordFragment()));
//        view.findViewById(R.id.create_account).setOnClickListener(v -> ((SignInActivity) getActivity()).loadFragment(new SignUpFragment()));

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

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError(getString(R.string.email_required));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError(getString(R.string.password_required));
            return;
        }

        progressDialogHelper.showProgressDialog(getActivity(), String.valueOf(R.string.signing_in_progress_text));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressDialogHelper.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (rememberMeCheckBox.isChecked()) {
                            // TODO: Implement session persistence
                        }
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
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
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        progressDialogHelper.showProgressDialog(getActivity(), getString(R.string.signing_in_progress_text));
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity(), task -> {
                    progressDialogHelper.dismissProgressDialog();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                        requireActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), R.string.authentication_failed, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
