package com.blinkfrosty.medfinder.ui.appointment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingAppointmentDetailsFragment extends Fragment {

    private TextView departmentNameTextView;
    private TextView doctorNameTextView;
    private TextView appointmentDateTimeTextView;
    private TextView hospitalInfoTextView;
    private EditText reasonForVisitEditText;
    private Button callButton;
    private Button directionsButton;
    private Button cancelAppointmentButton;
    private ProgressDialogHelper progressDialogHelper;
    private boolean isDoctorLoaded = false;
    private boolean isDepartmentLoaded = false;
    private boolean isHospitalLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_appointment_details, container, false);

        departmentNameTextView = view.findViewById(R.id.department_name);
        doctorNameTextView = view.findViewById(R.id.doctor_name);
        appointmentDateTimeTextView = view.findViewById(R.id.appointment_date_time);
        hospitalInfoTextView = view.findViewById(R.id.hospital_info);
        reasonForVisitEditText = view.findViewById(R.id.reason_for_visit);
        callButton = view.findViewById(R.id.call_button);
        directionsButton = view.findViewById(R.id.directions_button);
        cancelAppointmentButton = view.findViewById(R.id.cancel_appointment_button);

        progressDialogHelper = new ProgressDialogHelper();
        progressDialogHelper.showProgressDialog(requireContext(), "Loading...");

        if (getArguments() != null) {
            String appointmentId = getArguments().getString("appointmentId");
            loadAppointmentDetails(appointmentId);
        }

        return view;
    }

    private void loadAppointmentDetails(String appointmentId) {
        AppointmentDataAccessHelper.getInstance(getContext()).getAppointmentById(appointmentId, new AppointmentCallback() {
            @Override
            public void onAppointmentRetrieved(Appointment appointment) {
                reasonForVisitEditText.setText(appointment.getReasonForVisit());

                // Load doctor details
                DoctorDataAccessHelper.getInstance(getContext()).getDoctorOnce(appointment.getDoctorId(), new DoctorCallback() {
                    @Override
                    public void onDoctorRetrieved(Doctor doctor) {
                        doctorNameTextView.setText("With " + doctor.getName());
                        isDoctorLoaded = true;
                        checkIfAllDataLoaded();

                        // Set up call button
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
                    }

                    @Override
                    public void onDoctorsRetrieved(List<Doctor> doctors) {
                        // Not used
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("UpcomingAppointmentDetailsFragment", "Error loading doctor details", e);
                        isDoctorLoaded = true;
                        checkIfAllDataLoaded();
                    }
                });

                // Load department details
                DepartmentDataAccessHelper.getInstance(getContext()).getDepartmentOnce(appointment.getDepartmentId(), new DepartmentCallback() {
                    @Override
                    public void onDepartmentRetrieved(Department department) {
                        departmentNameTextView.setText(department.getName() + " Visit");
                        isDepartmentLoaded = true;
                        checkIfAllDataLoaded();
                    }

                    @Override
                    public void onDepartmentsRetrieved(List<Department> departments) {
                        // Not used
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("UpcomingAppointmentDetailsFragment", "Error loading department details", e);
                        isDepartmentLoaded = true;
                        checkIfAllDataLoaded();
                    }
                });

                // Load hospital details
                HospitalDataAccessHelper.getInstance(getContext()).getHospitalOnce(appointment.getHospitalId(), new HospitalCallback() {
                    @Override
                    public void onHospitalRetrieved(Hospital hospital) {
                        String hospitalInfo = hospital.getName() + ", " + hospital.getNeighborhood() + ", " + hospital.getCity();
                        hospitalInfoTextView.setText(hospitalInfo);
                        isHospitalLoaded = true;
                        checkIfAllDataLoaded();

                        // Set up directions button
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
                        // Not used
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("UpcomingAppointmentDetailsFragment", "Error loading hospital details", e);
                        isHospitalLoaded = true;
                        checkIfAllDataLoaded();
                    }
                });

                // Convert and display appointment date and time
                try {
                    SimpleDateFormat sdf24 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                    SimpleDateFormat sdf12 = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
                    String appointmentDateTime24 = appointment.getDate() + " " + appointment.getAppointmentStartTime();
                    Date date = sdf24.parse(appointmentDateTime24);
                    String appointmentDateTime12 = sdf12.format(date);
                    appointmentDateTimeTextView.setText(appointmentDateTime12);
                } catch (Exception e) {
                    appointmentDateTimeTextView.setText(R.string.error_loading_date_time);
                }

                // Set up cancel appointment button
                cancelAppointmentButton.setOnClickListener(v -> showDeleteConfirmationDialog(appointmentId));
            }

            @Override
            public void onAppointmentsRetrieved(List<Appointment> appointments) {
                // Not used
            }

            @Override
            public void onError(Exception e) {
                Log.e("UpcomingAppointmentDetailsFragment", "Error loading appointment details", e);
                Toast.makeText(getContext(), "An error occurred while retrieving appointment details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfAllDataLoaded() {
        if (isDoctorLoaded && isDepartmentLoaded && isHospitalLoaded) {
            progressDialogHelper.dismissProgressDialog();
        }
    }

    private void showDeleteConfirmationDialog(String appointmentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_delete_appointment_confirmation, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        Button yesButton = dialogView.findViewById(R.id.delete_dialog_button_yes);
        Button noButton = dialogView.findViewById(R.id.delete_dialog_button_no);

        yesButton.setOnClickListener(v -> {
            AppointmentDataAccessHelper.getInstance(getContext()).deleteAppointmentById(appointmentId, new AppointmentCallback() {
                @Override
                public void onAppointmentRetrieved(Appointment appointment) {
                    dialog.dismiss();
                    Navigation.findNavController(requireView()).popBackStack();
                }

                @Override
                public void onAppointmentsRetrieved(List<Appointment> appointments) {
                    // Not used
                }

                @Override
                public void onError(Exception e) {
                    Log.e("UpcomingAppointmentDetailsFragment", "Error deleting appointment", e);
                    dialog.dismiss();
                }
            });
        });

        noButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}