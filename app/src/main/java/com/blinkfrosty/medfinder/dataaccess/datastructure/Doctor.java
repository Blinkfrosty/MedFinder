package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Doctor implements Serializable {

    private String id;
    private String name;
    private String degrees;
    private String phoneNumber;
    private String profilePictureUri;
    private String departmentId;
    private String hospitalId;
    private OfficeHours officeHours;

    public Doctor() { }

    public Doctor(String id, String name, String degrees, String phoneNumber, String profilePictureUri,
                  String departmentId, String hospitalId, OfficeHours officeHours) {
        this.id = id;
        this.name = name;
        this.degrees = degrees;
        this.phoneNumber = phoneNumber;
        this.profilePictureUri = profilePictureUri;
        this.departmentId = departmentId;
        this.hospitalId = hospitalId;
        this.officeHours = officeHours;
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

    public OfficeHours getOfficeHours() {
        return officeHours;
    }
}
