package com.blinkfrosty.medfinder;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.blinkfrosty.medfinder.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

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
            progressDialogHelper.showProgressDialog(MainActivity.this, "Logging out...");
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
            progressDialogHelper.dismissProgressDialog();
        });

        dialog.show();
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
}