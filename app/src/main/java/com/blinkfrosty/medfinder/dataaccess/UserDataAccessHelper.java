package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserDataAccessHelper extends DatabaseHelperBase {

    private static UserDataAccessHelper instance;
    private final DatabaseReference usersReference;
    private static final String USERS = "users";
    private final Map<String, ValueEventListener> userListenersMap;

    private UserDataAccessHelper(Context context) {
        super(context);
        usersReference = getDatabaseReference().child(USERS);
        userListenersMap = new HashMap<>();
    }

    public static synchronized UserDataAccessHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserDataAccessHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void setUser(String userId, String firstName, String lastName, String email, String phoneNumber,
                        String gender, String profilePictureUri) {
        User user = new User(userId, firstName, lastName, email, phoneNumber, gender, profilePictureUri,
                true, false, false);
        usersReference.child(userId).setValue(user);
        Log.d("UserDataAccessHelper", "User set. User ID: " + userId);
    }

    public void getUserOnce(String userId, UserCallback callback) {
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

    public void addUserDataChangeListener(String userId, UserCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
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
        };

        usersReference.child(userId).addValueEventListener(listener);
        userListenersMap.put(userId, listener);
    }

    public void removeUserDataChangeListeners() {
        for (Map.Entry<String, ValueEventListener> entry : userListenersMap.entrySet()) {
            usersReference.child(entry.getKey()).removeEventListener(entry.getValue());
        }
        userListenersMap.clear();
    }

    public void updateProfilePictureUri(String userId, String profilePictureUri) {
        usersReference.child(userId).child("profilePictureUri").setValue(profilePictureUri);
        Log.d("UserDataAccessHelper", "Profile picture URI updated. User ID: " + userId);
    }
}