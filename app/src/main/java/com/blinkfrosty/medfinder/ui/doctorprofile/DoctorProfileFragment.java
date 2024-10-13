package com.blinkfrosty.medfinder.ui.doctorprofile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.DepartmentCallback;
import com.blinkfrosty.medfinder.dataaccess.DepartmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.DoctorCallback;
import com.blinkfrosty.medfinder.dataaccess.DoctorDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.HospitalCallback;
import com.blinkfrosty.medfinder.dataaccess.HospitalDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;
import com.bumptech.glide.Glide;

import java.util.List;

public class DoctorProfileFragment extends Fragment {

    private TextView doctorNameTextView;
    private TextView doctorDegreesTextView;
    private TextView departmentNameTextView;
    private TextView locationNameTextView;
    private TextView phoneNumberTextView;
    private ImageView doctorProfilePictureImageView;
    private Button callButton;
    private Button scheduleOnlineButton;
    private Button directionsButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        doctorNameTextView = view.findViewById(R.id.doctor_name);
        doctorDegreesTextView = view.findViewById(R.id.doctor_degrees);
        departmentNameTextView = view.findViewById(R.id.department_name);
        locationNameTextView = view.findViewById(R.id.location_name);
        phoneNumberTextView = view.findViewById(R.id.phone_number);
        doctorProfilePictureImageView = view.findViewById(R.id.doctor_profile_picture);
        callButton = view.findViewById(R.id.call_button);
        scheduleOnlineButton = view.findViewById(R.id.schedule_online_button);
        directionsButton = view.findViewById(R.id.directions_button);

        if (getArguments() != null) {
            String doctorId = getArguments().getString("doctorId");
            loadDoctorData(doctorId);
        }

        return view;
    }

    private void loadDoctorData(String doctorId) {
        DoctorDataAccessHelper.getInstance(getContext()).getDoctorOnce(doctorId, new DoctorCallback() {
            @Override
            public void onDoctorRetrieved(Doctor doctor) {
                doctorNameTextView.setText(doctor.getName());
                doctorDegreesTextView.setText(doctor.getDegrees());
                phoneNumberTextView.setText(doctor.getPhoneNumber());

                // Load the profile picture using Glide
                String profilePictureUri = doctor.getProfilePictureUri();
                if (profilePictureUri == null || profilePictureUri.isEmpty()) {
                    doctorProfilePictureImageView.setImageResource(R.mipmap.ic_generic_profile_img);
                } else {
                    Glide.with(requireContext())
                            .load(profilePictureUri)
                            .placeholder(R.mipmap.ic_generic_profile_img) // Placeholder image
                            .into(doctorProfilePictureImageView);
                }

                // Load the hospital data
                HospitalDataAccessHelper.getInstance(getContext()).getHospitalOnce(doctor.getHospitalId(), new HospitalCallback() {
                    @Override
                    public void onHospitalRetrieved(Hospital hospital) {
                        String location = hospital.getName() + ", " + hospital.getNeighborhood() + ", " + hospital.getCity();
                        locationNameTextView.setText(location);

                        // Set up the call button
                        if (doctor.getPhoneNumber() != null && !doctor.getPhoneNumber().isEmpty()) {
                            callButton.setEnabled(true);
                            callButton.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:" + doctor.getPhoneNumber()));
                                startActivity(intent);
                            });
                        } else {
                            callButton.setEnabled(false);
                        }

                        // Set up the schedule online button
                        if (hospital.getAppointmentLink() != null && !hospital.getAppointmentLink().isEmpty()) {
                            scheduleOnlineButton.setEnabled(true);
                            scheduleOnlineButton.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(hospital.getAppointmentLink()));
                                startActivity(intent);
                            });
                        } else {
                            scheduleOnlineButton.setEnabled(false);
                        }

                        // Set up the directions button
                        String address = hospital.getName() + ", " + hospital.getStreetAddress() + ", " +
                                hospital.getNeighborhood() + ", " + hospital.getCity() + " " + hospital.getPostalCode() +
                                ", " + hospital.getCountry();
                        directionsButton.setOnClickListener(v -> {
                            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        });
                    }

                    @Override
                    public void onHospitalsRetrieved(List<Hospital> hospitals) {
                        // Not used in this context
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("DoctorProfileFragment", "An error occurred while retrieving hospital data", e);
                    }
                });

                // Load the department data
                DepartmentDataAccessHelper.getInstance(getContext()).getDepartmentOnce(doctor.getDepartmentId(), new DepartmentCallback() {
                    @Override
                    public void onDepartmentRetrieved(Department department) {
                        departmentNameTextView.setText(department.getName());
                    }

                    @Override
                    public void onDepartmentsRetrieved(List<Department> departments) {
                        // Not used in this context
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("DoctorProfileFragment", "An error occurred while retrieving department data", e);
                    }
                });
            }

            @Override
            public void onDoctorsRetrieved(List<Doctor> doctors) {
                // Not used in this context
            }

            @Override
            public void onError(Exception e) {
                Log.e("DoctorProfileFragment", "An error occurred while retrieving doctor data", e);
            }
        });
    }
}