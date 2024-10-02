package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String genderCode;
    private String profilePictureUri;

    public User() { }

    public User(String firstName, String lastName, String email, String phoneNumber,
                String genderCode, String profilePictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.genderCode = genderCode;
        this.profilePictureUri = profilePictureUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGenderCode() {
        return genderCode;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }
}
