package com.blinkfrosty.medfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.blinkfrosty.medfinder.dataaccess.DepartmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.DoctorDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.HospitalDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.UserCallback;
import com.blinkfrosty.medfinder.dataaccess.UserDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.User;
import com.blinkfrosty.medfinder.databinding.ActivityMainBinding;
import com.blinkfrosty.medfinder.helpers.EmergencyDialogHelper;
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
import com.blinkfrosty.medfinder.helpers.SharedPreferenceHelper;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView headerFullName;
    private TextView headerEmail;
    private ImageView headerProfilePhoto;

    private UserDataAccessHelper userDataAccessHelper;
    private DepartmentDataAccessHelper departmentDataAccessHelper;
    private DoctorDataAccessHelper doctorDataAccessHelper;
    private HospitalDataAccessHelper hospitalDataAccessHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        setupDrawerMenu(drawer, navigationView, binding);
        setupViewProfileMenu(navigationView, binding);

        // Initialize nav header views and helpers
        headerFullName = navigationView.getHeaderView(0).findViewById(R.id.header_full_name);
        headerEmail = navigationView.getHeaderView(0).findViewById(R.id.header_email);
        headerProfilePhoto = navigationView.getHeaderView(0).findViewById(R.id.header_profile_photo);

        // Initialize helpers
        userDataAccessHelper = UserDataAccessHelper.getInstance(this);
        departmentDataAccessHelper = DepartmentDataAccessHelper.getInstance(this);
        doctorDataAccessHelper = DoctorDataAccessHelper.getInstance(this);
        hospitalDataAccessHelper = HospitalDataAccessHelper.getInstance(this);

        // Update views with user data listener
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            addUserDataChangeListener(userId);
        }

        // Set up FAB click listener
        findViewById(R.id.fab_emergency_call).setOnClickListener(v -> EmergencyDialogHelper.showEmergencyCallDialog(MainActivity.this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Setup the drawer menu navigation and handle the logout action
    private void setupDrawerMenu(DrawerLayout drawer, NavigationView navigationView, ActivityMainBinding binding) {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_search_by_department,
                R.id.nav_search_by_doctor,
                R.id.nav_upcoming_appointments,
                R.id.nav_appointment_history,
                R.id.nav_logout)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_logout) {
                showLogoutDialog();
                return true;
            } else {
                boolean handled = NavigationUI.onNavDestinationSelected(item, navController)
                        || MainActivity.super.onOptionsItemSelected(item);
                if (handled) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                }
                return handled;
            }
        });
    }

    // Show logout dialog
    private void showLogoutDialog() {
        SharedPreferenceHelper preferenceHelper = new SharedPreferenceHelper(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_logout_confirmation, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button buttonNo = dialogView.findViewById(R.id.logout_dialog_button_no);
        buttonNo.setOnClickListener(v -> dialog.dismiss());

        Button buttonYes = dialogView.findViewById(R.id.logout_dialog_button_yes);
        buttonYes.setOnClickListener(v -> {
            dialog.dismiss();
            ProgressDialogHelper progressDialogHelper = new ProgressDialogHelper();
            progressDialogHelper.showProgressDialog(MainActivity.this, getString(R.string.logging_out_progress_text));

            // Dispose data listeners
            disposeDataListeners();

            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Sign out from Google
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this,
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build());
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                preferenceHelper.setLoggedIn(false);
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
                progressDialogHelper.dismissProgressDialog();
            });
        });

        dialog.show();
    }

    // Dispose data listeners
    private void disposeDataListeners() {
        userDataAccessHelper.removeUserDataChangeListeners();
        departmentDataAccessHelper.removeDepartmentDataChangeListeners();
        doctorDataAccessHelper.removeDoctorDataChangeListeners();
        hospitalDataAccessHelper.removeHospitalDataChangeListeners();
    }

    // Setup the profile image view to show a popup menu with the option to view the user profile
    private void setupViewProfileMenu(NavigationView navigationView, ActivityMainBinding binding) {
        ImageView profileImageView = navigationView.getHeaderView(0).findViewById(R.id.header_profile_photo);
        profileImageView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
            popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_view_profile) {
                    NavController navController = Navigation.findNavController(MainActivity.this, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.nav_user_profile);
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void addUserDataChangeListener(String userId) {
        userDataAccessHelper.addUserDataChangeListener(userId, new UserCallback() {
            @Override
            public void onUserRetrieved(User user) {
                // Update UI with user data
                headerFullName.setText(user.getFirstName() + " " + user.getLastName());
                headerEmail.setText(user.getEmail());

                // Load profile photo
                if (user.getProfilePictureUri() != null && !user.getProfilePictureUri().isEmpty()) {
                    Glide.with(MainActivity.this)
                            .load(user.getProfilePictureUri())
                            .into(headerProfilePhoto);
                } else {
                    headerProfilePhoto.setImageResource(R.mipmap.ic_generic_profile_img);
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("MainActivity", "Failed to retrieve user data", e);
            }
        });
    }
}