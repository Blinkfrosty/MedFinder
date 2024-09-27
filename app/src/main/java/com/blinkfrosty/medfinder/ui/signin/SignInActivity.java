package com.blinkfrosty.medfinder.ui.signin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blinkfrosty.medfinder.MainActivity;
import com.blinkfrosty.medfinder.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            launchMainActivity();
        } else {
            // Start sign-in process
            startSignIn();
        }
    }

    private void startSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        startActivity(signInIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IdpResponse response = IdpResponse.fromResultIntent(data);
        if (resultCode == RESULT_OK) {
            // Successfully signed in
            launchMainActivity();
        } else {
            // Sign in failed, handle error
            if (response == null) {
                // User pressed back button
                showSnackbar(String.valueOf(R.string.sign_in_cancelled));
                finish();
            } else {
                // Handle other errors
                int errorCode = Objects.requireNonNull(response.getError()).getErrorCode();
                switch (errorCode) {
                    case ErrorCodes.NO_NETWORK:
                        // Show no network error message
                        showSnackbar(String.valueOf(R.string.no_internet_connection));
                        break; case ErrorCodes.UNKNOWN_ERROR:// Show unknown error message
                        showSnackbar("Unknown error occurred");
                        break; default:// Show generic error message
                        showSnackbar("Sign-in failed " + errorCode);
                        break;
                }
            }
        }
    }

    private void launchMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showSnackbar(String message) {
        // Find the root view to anchor the Snackbar
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}
