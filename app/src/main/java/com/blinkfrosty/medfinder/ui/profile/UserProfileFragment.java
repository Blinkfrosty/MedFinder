package com.blinkfrosty.medfinder.ui.profile;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.PhotoStorageHelper;
import com.blinkfrosty.medfinder.dataaccess.UserCallback;
import com.blinkfrosty.medfinder.dataaccess.UserDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.User;
import com.blinkfrosty.medfinder.mapper.GenderCodeMapper;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class UserProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText phoneNumberEditText;
    private RadioGroup genderRadioGroup;
    private ImageView profilePictureImageView;
    private UserDataAccessHelper userDataAccessHelper;
    private PhotoStorageHelper photoStorageHelper;
    private FirebaseUser currentUser;
    private String loadedProfilePictureUri = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        firstNameEditText = view.findViewById(R.id.first_name);
        lastNameEditText = view.findViewById(R.id.last_name);
        emailEditText = view.findViewById(R.id.email);
        phoneNumberEditText = view.findViewById(R.id.phone_number);
        genderRadioGroup = view.findViewById(R.id.gender_radio_group);
        profilePictureImageView = view.findViewById(R.id.profile_picture);

        userDataAccessHelper = UserDataAccessHelper.getInstance(requireContext());
        photoStorageHelper = new PhotoStorageHelper(requireContext());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        profilePictureImageView.setOnClickListener(v -> showProfilePictureMenu());
        view.findViewById(R.id.update_profile_button).setOnClickListener(v -> updateProfile());
        view.findViewById(R.id.change_password_button).setOnClickListener(v -> showChangePasswordDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userDataAccessHelper.getUserOnce(userId, new UserCallback() {
                @Override
                public void onUserRetrieved(User user) {
                    firstNameEditText.setText(user.getFirstName());
                    lastNameEditText.setText(user.getLastName());
                    emailEditText.setText(user.getEmail());
                    phoneNumberEditText.setText(user.getPhoneNumber());

                    String gender = GenderCodeMapper.getString(user.getGenderCode());
                    if (gender.equals(GenderCodeMapper.MALE)) {
                        genderRadioGroup.check(R.id.radio_male);
                    } else if (gender.equals(GenderCodeMapper.FEMALE)) {
                        genderRadioGroup.check(R.id.radio_female);
                    } else {
                        genderRadioGroup.check(R.id.radio_other);
                    }

                    if (user.getProfilePictureUri() != null && !user.getProfilePictureUri().isEmpty()) {
                        Glide.with(UserProfileFragment.this)
                                .load(user.getProfilePictureUri())
                                .into(profilePictureImageView);
                        loadedProfilePictureUri = user.getProfilePictureUri();
                    } else {
                        profilePictureImageView.setImageResource(R.mipmap.ic_generic_profile_img);
                    }

                    for (UserInfo userInfo : currentUser.getProviderData()) {
                        if (userInfo.getProviderId().equals("google.com")) {
                            emailEditText.setEnabled(false);
                            Log.d("UserProfileFragment", "Email disabled for google user");
                            break;
                        }
                    }
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getActivity(), "Failed to load user data", Toast.LENGTH_SHORT).show();
                    Log.e("UserProfileFragment", "Failed to load user data", e);
                }
            });
        }
    }

    private void showProfilePictureMenu() {
        PopupMenu popupMenu = new PopupMenu(requireContext(), profilePictureImageView);
        popupMenu.getMenuInflater().inflate(R.menu.profile_picture_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_change_profile_picture) {
                pickImage();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadProfilePicture(imageUri);
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            photoStorageHelper.storeLocalImage(userId, imageUri, uri -> {
                userDataAccessHelper.updateProfilePictureUri(userId, uri.toString());
                loadUserData();
            }, e -> {
                Toast.makeText(getActivity(), "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
                Log.e("UserProfileFragment", "Failed to upload profile picture", e);
            });
        }
    }

    private void updateProfile() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String firstName = firstNameEditText.getText().toString().trim();
            String lastName = lastNameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String phoneNumber = phoneNumberEditText.getText().toString().trim();
            String gender = GenderCodeMapper.getCode(((RadioButton) requireView().findViewById(genderRadioGroup.getCheckedRadioButtonId())).getText().toString());

            userDataAccessHelper.setUser(userId, firstName, lastName, email, phoneNumber, gender, loadedProfilePictureUri);

            if (emailEditText.isEnabled() && !email.equals(currentUser.getEmail())) {
                currentUser.updateEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("UserProfileFragment", "Email updated");
                    } else {
                        Log.e("UserProfileFragment", "Failed to update email", task.getException());
                    }
                });
            }

            Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
            Log.d("UserProfileFragment", "Profile updated for user: " + userId);
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText passwordEditText = dialogView.findViewById(R.id.new_password);
        EditText confirmPasswordEditText = dialogView.findViewById(R.id.confirm_password);

        setPasswordVisibilityToggle(passwordEditText);
        setPasswordVisibilityToggle(confirmPasswordEditText);

        dialogView.findViewById(R.id.change_password_button).setOnClickListener(view -> {
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            boolean is_valid = true;
            if (password.isEmpty()) {
                passwordEditText.setError(getString(R.string.password_required));
                is_valid = false;
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordEditText.setError(getString(R.string.confirm_password_required));
                is_valid = false;
            } else if (!password.equals(confirmPassword)) {
                confirmPasswordEditText.setError(getString(R.string.confirm_password_does_not_match));
                is_valid = false;
            }

            if (is_valid) {
                currentUser.updatePassword(password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), getString(R.string.password_changed_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Failed to change password", Toast.LENGTH_SHORT).show();
                        Log.e("UserProfileFragment", "Failed to change password", task.getException());
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPasswordVisibilityToggle(EditText passwordEditText) {
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