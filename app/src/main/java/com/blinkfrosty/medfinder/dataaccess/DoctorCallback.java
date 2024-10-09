package com.blinkfrosty.medfinder.dataaccess;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;

import java.util.List;

public interface DoctorCallback {
    void onDoctorRetrieved(Doctor doctor);
    void onDoctorsRetrieved(List<Doctor> doctors);
    void onError(Exception e);
}