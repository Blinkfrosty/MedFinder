package com.blinkfrosty.medfinder;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
import com.blinkfrosty.medfinder.helpers.SharedPreferenceHelper;
import com.blinkfrosty.medfinder.ui.login.LoginFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        SharedPreferenceHelper preferenceHelper = new SharedPreferenceHelper(this);
        ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper();

        if (preferenceHelper.isLoggedIn()) {
            progressDialogHelper.showProgressDialog(this, getString(R.string.logging_in_progress_text));
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                mAuth.signOut();
                loadFragment(new LoginFragment());
            }
            progressDialogHelper.dismissProgressDialog();
        } else {
            loadFragment(new LoginFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
