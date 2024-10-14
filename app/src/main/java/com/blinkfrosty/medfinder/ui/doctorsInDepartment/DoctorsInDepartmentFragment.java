package com.blinkfrosty.medfinder.ui.doctorsInDepartment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.ui.doctor.SearchByDoctorFragment;

import java.util.ArrayList;
import java.util.List;

public class DoctorsInDepartmentFragment extends SearchByDoctorFragment {

    private String departmentId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            departmentId = getArguments().getString("departmentId");
        }
    }

    @Override
    protected void filterDoctors() {
        String query = searchDoctorEditText.getText().toString().toLowerCase();
        String selectedHospital = hospitalSpinner.getSelectedItem() != null && !SPINNER_DEFAULT_ITEM.equals(hospitalSpinner.getSelectedItem().toString())
                ? hospitalSpinner.getSelectedItem().toString() : "";
        String selectedNeighborhood = neighborhoodSpinner.getSelectedItem() != null && !SPINNER_DEFAULT_ITEM.equals(neighborhoodSpinner.getSelectedItem().toString())
                ? neighborhoodSpinner.getSelectedItem().toString() : "";

        List<Doctor> filteredDoctors = new ArrayList<>();
        for (Doctor doctor : allDoctors) {
            boolean matchesQuery = doctor.getName().toLowerCase().contains(query);
            boolean matchesHospital = selectedHospital.isEmpty() || getHospitalNameById(doctor.getHospitalId()).equals(selectedHospital);
            boolean matchesNeighborhood = selectedNeighborhood.isEmpty() || getHospitalNeighborhoodById(doctor.getHospitalId()).equals(selectedNeighborhood);
            boolean matchesDepartment = doctor.getDepartmentId().equals(departmentId);

            if (matchesQuery && matchesHospital && matchesNeighborhood && matchesDepartment) {
                filteredDoctors.add(doctor);
            }
        }
        doctorRecyclerView.setAdapter(new DoctorInDepartmentAdapter(filteredDoctors, allHospitals, NavHostFragment.findNavController(this)));
    }
}