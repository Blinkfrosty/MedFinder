package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Doctor {

    private String id;
    private String name;
    private String degrees;
    private String phoneNumber;
    private String profilePictureUri;
    private String departmentId;
    private String hospitalId;

    public Doctor() { }

    public Doctor(String id, String name, String degrees, String phoneNumber, String profilePictureUri,
                  String departmentId, String hospitalId) {
        this.id = id;
        this.name = name;
        this.degrees = degrees;
        this.phoneNumber = phoneNumber;
        this.profilePictureUri = profilePictureUri;
        this.departmentId = departmentId;
        this.hospitalId = hospitalId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDegrees() {
        return degrees;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public String getHospitalId() {
        return hospitalId;
    }
}