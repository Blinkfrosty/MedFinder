package com.blinkfrosty.medfinder.dataaccess;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;

import java.util.List;

public interface HospitalCallback {
    void onHospitalRetrieved(Hospital hospital);
    void onHospitalsRetrieved(List<Hospital> hospitals);
    void onError(Exception e);
}