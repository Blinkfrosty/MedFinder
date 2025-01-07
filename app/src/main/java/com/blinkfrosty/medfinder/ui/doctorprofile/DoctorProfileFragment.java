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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.AppointmentCallback;
import com.blinkfrosty.medfinder.dataaccess.AppointmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.DepartmentCallback;
import com.blinkfrosty.medfinder.dataaccess.DepartmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.DoctorCallback;
import com.blinkfrosty.medfinder.dataaccess.DoctorDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.HospitalCallback;
import com.blinkfrosty.medfinder.dataaccess.HospitalDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Appointment;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;
import com.blinkfrosty.medfinder.dataaccess.datastructure.OfficeHours;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DoctorProfileFragment extends Fragment {

    private TextView doctorNameTextView;
    private TextView doctorDegreesTextView;
    private TextView departmentNameTextView;
    private TextView locationNameTextView;
    private TextView phoneNumberTextView;
    private TextView mondayHoursTextView;
    private TextView tuesdayHoursTextView;
    private TextView wednesdayHoursTextView;
    private TextView thursdayHoursTextView;
    private TextView fridayHoursTextView;
    private TextView saturdayHoursTextView;
    private TextView sundayHoursTextView;
    private ImageView doctorProfilePictureImageView;
    private Button callButton;
    private Button visitWebsiteButton;
    private Button directionsButton;
    private Button scheduleAppointmentButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        doctorNameTextView = view.findViewById(R.id.doctor_name);
        doctorDegreesTextView = view.findViewById(R.id.doctor_degrees);
        departmentNameTextView = view.findViewById(R.id.department_name);
        locationNameTextView = view.findViewById(R.id.location_name);
        phoneNumberTextView = view.findViewById(R.id.phone_number);
        mondayHoursTextView = view.findViewById(R.id.monday_hours);
        tuesdayHoursTextView = view.findViewById(R.id.tuesday_hours);
        wednesdayHoursTextView = view.findViewById(R.id.wednesday_hours);
        thursdayHoursTextView = view.findViewById(R.id.thursday_hours);
        fridayHoursTextView = view.findViewById(R.id.friday_hours);
        saturdayHoursTextView = view.findViewById(R.id.saturday_hours);
        sundayHoursTextView = view.findViewById(R.id.sunday_hours);
        doctorProfilePictureImageView = view.findViewById(R.id.doctor_profile_picture);
        callButton = view.findViewById(R.id.call_button);
        visitWebsiteButton = view.findViewById(R.id.visit_website_button);
        directionsButton = view.findViewById(R.id.directions_button);
        scheduleAppointmentButton = view.findViewById(R.id.schedule_appointment_button);

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

                OfficeHours officeHours = doctor.getOfficeHours();

                if (officeHours.getMonday().isAvailable()) {
                    String hoursText = formatOfficeHours(officeHours.getMonday().getStartTime(),
                            officeHours.getMonday().getEndTime());
                    mondayHoursTextView.setText(hoursText);
                } else {
                    mondayHoursTextView.setText(R.string.closed);
                }

                if (officeHours.getTuesday().isAvailable()) {
                    String hoursText = formatOfficeHours(officeHours.getTuesday().getStartTime(),
                            officeHours.getTuesday().getEndTime());
                    tuesdayHoursTextView.setText(hoursText);
                } else {
                    tuesdayHoursTextView.setText(R.string.closed);
                }

                if (officeHours.getWednesday().isAvailable()) {
                    String hoursText = formatOfficeHours(officeHours.getWednesday().getStartTime(),
                            officeHours.getWednesday().getEndTime());
                    wednesdayHoursTextView.setText(hoursText);
                } else {
                    wednesdayHoursTextView.setText(R.string.closed);
                }

                if (officeHours.getThursday().isAvailable()) {
                    String hoursText = formatOfficeHours(officeHours.getThursday().getStartTime(),
                            officeHours.getThursday().getEndTime());
                    thursdayHoursTextView.setText(hoursText);
                } else {
                    thursdayHoursTextView.setText(R.string.closed);
                }

                if (officeHours.getFriday().isAvailable()) {
                    String hoursText = formatOfficeHours(officeHours.getFriday().getStartTime(),
                            officeHours.getFriday().getEndTime());
                    fridayHoursTextView.setText(hoursText);
                } else {
                    fridayHoursTextView.setText(R.string.closed);
                }

                if (officeHours.getSaturday().isAvailable()) {
                    String hoursText = formatOfficeHours(officeHours.getSaturday().getStartTime(),
                            officeHours.getSaturday().getEndTime());
                    saturdayHoursTextView.setText(hoursText);
                } else {
                    saturdayHoursTextView.setText(R.string.closed);
                }

                if (officeHours.getSunday().isAvailable()) {
                    String hoursText = formatOfficeHours(officeHours.getSunday().getStartTime(),
                            officeHours.getSunday().getEndTime());
                    sundayHoursTextView.setText(hoursText);
                } else {
                    sundayHoursTextView.setText(R.string.closed);
                }

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

                        // Set up the visit website/appointment button
                        if (hospital.getAppointmentLink() != null && !hospital.getAppointmentLink().isEmpty()) {
                            visitWebsiteButton.setEnabled(true);
                            visitWebsiteButton.setOnClickListener(v -> {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(hospital.getAppointmentLink()));
                                startActivity(intent);
                            });
                        } else {
                            visitWebsiteButton.setEnabled(false);
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

                        // Set up the schedule appointment button
                        scheduleAppointmentButton.setOnClickListener(v -> checkAndScheduleAppointment(doctor));

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

    private String formatOfficeHours(String startTime, String endTime) {
        SimpleDateFormat _12HourSdf = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat _24HourSdf = new SimpleDateFormat("HH:mm");

        try {
            Date start = _24HourSdf.parse(startTime);
            Date end = _24HourSdf.parse(endTime);
            return _12HourSdf.format(start) + " - " + _12HourSdf.format(end);
        } catch (Exception e) {
            Log.e("DoctorProfileFragment", "An error occurred while parsing office hours", e);
            return startTime + " - " + endTime;
        }
    }

    private void checkAndScheduleAppointment(Doctor doctor) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            AppointmentDataAccessHelper.getInstance(getContext()).checkUserAppointmentsWithDoctor(userId, doctor.getId(), new AppointmentCallback() {
                @Override
                public void onAppointmentsRetrieved(List<Appointment> appointments) {
                    boolean hasUpcomingAppointment = false;
                    Date currentDateTime = new Date();

                    for (Appointment appointment : appointments) {
                        try {
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            Date appointmentDateTime = dateTimeFormat.parse(appointment.getDate() + " " + appointment.getAppointmentStartTime());

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(appointmentDateTime);
                            calendar.add(Calendar.MINUTE, 30);
                            Date appointmentEndTime = calendar.getTime();

                            if (appointmentEndTime.after(currentDateTime)) {
                                hasUpcomingAppointment = true;
                                break;
                            }
                        } catch (Exception e) {
                            Log.e("DoctorProfileFragment", "Error parsing appointment date/time", e);
                        }
                    }

                    if (hasUpcomingAppointment) {
                        new AlertDialog.Builder(requireContext())
                                .setMessage("You cannot schedule another appointment with this doctor." +
                                        "\nPlease cancel your upcoming appointment if you want to reschedule.")
                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                    } else {
                        scheduleNewAppointment(doctor);
                    }
                }

                @Override
                public void onAppointmentRetrieved(Appointment appointment) {
                    // Not used
                }

                @Override
                public void onError(Exception e) {
                    Log.e("DoctorProfileFragment", "Error checking user appointments", e);
                }
            });
        }
    }

    private void scheduleNewAppointment(Doctor doctor) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("doctor", doctor);
        Navigation.findNavController(requireView()).navigate(R.id.action_nav_doctor_profile_to_nav_schedule_appointment, bundle);
    }
}