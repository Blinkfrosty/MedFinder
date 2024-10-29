package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String genderCode;
    private String profilePictureUri;
    private Boolean isPatient;
    private Boolean isHospitalAdmin;
    private Boolean isSystemAdmin;

    public User() { }

    public User(String id, String firstName, String lastName, String email, String phoneNumber,
                String genderCode, String profilePictureUrl, Boolean isPatient, Boolean isHospitalAdmin, Boolean isSystemAdmin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.genderCode = genderCode;
        this.profilePictureUri = profilePictureUrl;
        this.isPatient = isPatient;
        this.isHospitalAdmin = isHospitalAdmin;
        this.isSystemAdmin = isSystemAdmin;
    }

    public String getId() {
        return id;
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

    public Boolean getIsPatient() {
        return isPatient;
    }

    public Boolean getIsHospitalAdmin() {
        return isHospitalAdmin;
    }

    public Boolean getIsSystemAdmin() {
        return isSystemAdmin;
    }
}
