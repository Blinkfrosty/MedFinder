package com.blinkfrosty.medfinder.ui.doctorsInDepartment;

import android.os.Bundle;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;
import com.blinkfrosty.medfinder.ui.doctor.DoctorAdapter;

import androidx.navigation.NavController;

import java.util.List;

public class DoctorInDepartmentAdapter extends DoctorAdapter {

    public DoctorInDepartmentAdapter(List<Doctor> doctors, List<Hospital> allHospitals, NavController navController) {
        super(doctors, allHospitals, navController);
    }

    @Override
    protected void navigateToDoctorProfile(String doctorId) {
        Bundle bundle = new Bundle();
        bundle.putString("doctorId", doctorId);
        navController.navigate(R.id.action_nav_doctors_in_department_to_nav_doctor_profile, bundle);
    }
}