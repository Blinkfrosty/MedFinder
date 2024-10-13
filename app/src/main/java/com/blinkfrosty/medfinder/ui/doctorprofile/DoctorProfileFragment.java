package com.blinkfrosty.medfinder.ui.doctorprofile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blinkfrosty.medfinder.R;

public class DoctorProfileFragment extends Fragment {

    private String doctorId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        if (getArguments() != null) {
            doctorId = getArguments().getString("doctorId");
            loadDoctorData(doctorId);
        }

        return view;
    }

    private void loadDoctorData(String doctorId) {
        // TODO: Implement the logic to load doctor data using the doctorId
    }
}
