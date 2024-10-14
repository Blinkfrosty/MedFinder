package com.blinkfrosty.medfinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.blinkfrosty.medfinder.helpers.EmergencyDialogHelper;
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
import com.blinkfrosty.medfinder.helpers.SharedPreferenceHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

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
            }
            progressDialogHelper.dismissProgressDialog();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_sign_in);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Set up FAB click listener
        findViewById(R.id.fab_emergency_call).setOnClickListener(v -> EmergencyDialogHelper.showEmergencyCallDialog(SignInActivity.this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_sign_in);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }
}
