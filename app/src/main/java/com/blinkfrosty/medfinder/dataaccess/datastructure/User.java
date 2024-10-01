package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String firstName;
    public String lastName;
    public String email;
    public String phoneNumber;
    public String genderCode;
    public String profilePictureUrl;

    public User(String firstName, String lastName, String email, String phoneNumber,
                String genderCode, String profilePictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.genderCode = genderCode;
        this.profilePictureUrl = profilePictureUrl;
    }
}
