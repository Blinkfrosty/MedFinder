package com.blinkfrosty.medfinder.ui.finddoctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.blinkfrosty.medfinder.R;

public class FindDoctorFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_doctor, container, false);

        Button exploreDepartmentsButton = view.findViewById(R.id.explore_departments_button);
        Button searchForDoctorsButton = view.findViewById(R.id.search_for_doctors_button);

        exploreDepartmentsButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_find_doctor_to_search_by_department));

        searchForDoctorsButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_find_doctor_to_search_by_doctor));

        return view;
    }
}