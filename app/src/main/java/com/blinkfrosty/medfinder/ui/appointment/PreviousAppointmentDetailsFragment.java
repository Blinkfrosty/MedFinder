package com.blinkfrosty.medfinder.ui.appointment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class PreviousAppointmentDetailsFragment extends Fragment {

    private TextView departmentNameTextView;
    private TextView doctorNameTextView;
    private TextView appointmentDateTimeTextView;
    private TextView hospitalInfoTextView;
    private EditText reasonForVisitEditText;
    private EditText appointmentNotesEditText;
    private ProgressDialogHelper progressDialogHelper;
    private boolean isDoctorLoaded = false;
    private boolean isDepartmentLoaded = false;
    private boolean isHospitalLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_previous_appointment_details, container, false);

        departmentNameTextView = view.findViewById(R.id.department_name);
        doctorNameTextView = view.findViewById(R.id.doctor_name);
        appointmentDateTimeTextView = view.findViewById(R.id.appointment_date_time);
        hospitalInfoTextView = view.findViewById(R.id.hospital_info);
        reasonForVisitEditText = view.findViewById(R.id.reason_for_visit);
        appointmentNotesEditText = view.findViewById(R.id.appointment_notes);

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
                appointmentNotesEditText.setText(appointment.getAppointmentNotes());

                // Load doctor details
                DoctorDataAccessHelper.getInstance(getContext()).getDoctorOnce(appointment.getDoctorId(), new DoctorCallback() {
                    @Override
                    public void onDoctorRetrieved(Doctor doctor) {
                        doctorNameTextView.setText("With " + doctor.getName());
                        isDoctorLoaded = true;
                        checkIfAllDataLoaded();
                    }

                    @Override
                    public void onDoctorsRetrieved(List<Doctor> doctors) {
                        // Not used
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PreviousAppointmentDetailsFragment", "Error loading doctor details", e);
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
                        Log.e("PreviousAppointmentDetailsFragment", "Error loading department details", e);
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
                    }

                    @Override
                    public void onHospitalsRetrieved(List<Hospital> hospitals) {
                        // Not used
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("PreviousAppointmentDetailsFragment", "Error loading hospital details", e);
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
            }

            @Override
            public void onAppointmentsRetrieved(List<Appointment> appointments) {
                // Not used
            }

            @Override
            public void onError(Exception e) {
                Log.e("PreviousAppointmentDetailsFragment", "Error loading appointment details", e);
                Toast.makeText(getContext(), "An error occurred while retrieving appointment details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfAllDataLoaded() {
        if (isDoctorLoaded && isDepartmentLoaded && isHospitalLoaded) {
            progressDialogHelper.dismissProgressDialog();
        }
    }
}
