package com.blinkfrosty.medfinder.ui.doctor;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.DoctorCallback;
import com.blinkfrosty.medfinder.dataaccess.DoctorDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.HospitalCallback;
import com.blinkfrosty.medfinder.dataaccess.HospitalDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchByDoctorFragment extends Fragment {

    protected DoctorDataAccessHelper doctorDataAccessHelper;
    protected HospitalDataAccessHelper hospitalDataAccessHelper;
    protected RecyclerView doctorRecyclerView;
    protected EditText searchDoctorEditText;
    protected Spinner hospitalSpinner;
    protected Spinner neighborhoodSpinner;
    protected List<Doctor> allDoctors = new ArrayList<>();
    protected List<Hospital> allHospitals = new ArrayList<>();
    protected List<String> allNeighborhoods = new ArrayList<>();
    protected ProgressDialogHelper progressDialogHelper;

    protected static final String SPINNER_DEFAULT_ITEM = "All";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_by_doctor, container, false);

        doctorDataAccessHelper = DoctorDataAccessHelper.getInstance(getContext());
        hospitalDataAccessHelper = HospitalDataAccessHelper.getInstance(getContext());

        doctorRecyclerView = view.findViewById(R.id.doctor_list);
        doctorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchDoctorEditText = view.findViewById(R.id.doctor_search);
        hospitalSpinner = view.findViewById(R.id.hospital_spinner);
        neighborhoodSpinner = view.findViewById(R.id.neighborhood_spinner);

        progressDialogHelper = new ProgressDialogHelper();
        progressDialogHelper.showProgressDialog(requireContext(), "Loading...");

        searchDoctorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDoctors();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });

        hospitalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDoctors();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        neighborhoodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterDoctors();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });

        loadHospitalData();
        loadDoctorList();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allDoctors.clear();
        allHospitals.clear();
        allNeighborhoods.clear();
    }

    protected void loadHospitalData() {
        hospitalDataAccessHelper.getAllHospitals(new HospitalCallback() {
            @Override
            public void onHospitalsRetrieved(List<Hospital> hospitals) {
                allHospitals = hospitals;

                List<String> hospitalNames = new ArrayList<>();
                hospitalNames.add(SPINNER_DEFAULT_ITEM); // Add default item
                for (Hospital hospital : allHospitals) {
                    hospitalNames.add(hospital.getName());
                }

                Set<String> neighborhoods = new HashSet<>();
                neighborhoods.add(SPINNER_DEFAULT_ITEM); // Add default item
                for (Hospital hospital : hospitals) {
                    neighborhoods.add(hospital.getNeighborhood());
                }
                allNeighborhoods = new ArrayList<>(neighborhoods);

                ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, hospitalNames);
                hospitalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                hospitalSpinner.setAdapter(hospitalAdapter);

                ArrayAdapter<String> neighborhoodAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, allNeighborhoods);
                neighborhoodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                neighborhoodSpinner.setAdapter(neighborhoodAdapter);
            }

            @Override
            public void onHospitalRetrieved(Hospital hospital) {
                // Not used in this context
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "An error occurred while retrieving hospitals", Toast.LENGTH_SHORT).show();
                Log.e("SearchByDoctorFragment", "An error occurred while retrieving hospitals", e);
            }
        });
    }

    protected void loadDoctorList() {
        doctorDataAccessHelper.getAllDoctors(new DoctorCallback() {
            @Override
            public void onDoctorsRetrieved(List<Doctor> doctors) {
                allDoctors = doctors;
                filterDoctors();
                progressDialogHelper.dismissProgressDialog();
            }

            @Override
            public void onDoctorRetrieved(Doctor doctor) {
                // Not used in this context
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "An error occurred while retrieving doctors", Toast.LENGTH_SHORT).show();
                Log.e("SearchByDoctorFragment", "An error occurred while retrieving doctors", e);
                progressDialogHelper.dismissProgressDialog();
            }
        });
    }

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

            if (matchesQuery && matchesHospital && matchesNeighborhood) {
                filteredDoctors.add(doctor);
            }
        }

        // Sort the filtered doctors by name
        filteredDoctors.sort((doctor1, doctor2) -> doctor1.getName().compareToIgnoreCase(doctor2.getName()));

        doctorRecyclerView.setAdapter(new DoctorAdapter(filteredDoctors, allHospitals, NavHostFragment.findNavController(this)));
        doctorRecyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down));
        doctorRecyclerView.scheduleLayoutAnimation();
    }

    protected String getHospitalNameById(String hospitalId) {
        for (Hospital hospital : allHospitals) {
            if (hospital.getId().equals(hospitalId)) {
                return hospital.getName();
            }
        }
        return "";
    }

    protected String getHospitalNeighborhoodById(String hospitalId) {
        for (Hospital hospital : allHospitals) {
            if (hospital.getId().equals(hospitalId)) {
                return hospital.getNeighborhood();
            }
        }
        return "";
    }
}