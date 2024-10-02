package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class UserDataAccessHelper extends DatabaseHelperBase{

    private final DatabaseReference usersReference;
    private static final String USERS = "users";

    public UserDataAccessHelper(Context context) {
        super(context);
        usersReference = getDatabaseReference().child(USERS);
    }

    public void setUser(String userId, String firstName, String lastName, String email, String phoneNumber, String gender) {
        User user = new User(firstName, lastName, email, phoneNumber, gender, "");
        usersReference.child(userId).setValue(user);
        Log.d("UserDataAccessHelper", "User set. User ID: " + userId);
    }

    public void getUser(String userId, UserCallback callback) {
        usersReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    callback.onUserRetrieved(user);
                } else {
                    callback.onError(new Exception("User not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void updateProfilePictureUri(String userId, String profilePictureUri) {
        usersReference.child(userId).child("profilePictureUri").setValue(profilePictureUri);
        Log.d("UserDataAccessHelper", "Profile picture URI updated. User ID: " + userId);
    }
}
