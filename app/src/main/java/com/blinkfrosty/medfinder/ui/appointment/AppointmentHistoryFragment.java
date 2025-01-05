package com.blinkfrosty.medfinder.ui.appointment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.AppointmentCallback;
import com.blinkfrosty.medfinder.dataaccess.AppointmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.DepartmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.DoctorDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Appointment;
import com.blinkfrosty.medfinder.helpers.ProgressDialogHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AppointmentHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private ProgressDialogHelper progressDialogHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_history, container, false);

        recyclerView = view.findViewById(R.id.appointment_history_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialogHelper = new ProgressDialogHelper();
        progressDialogHelper.showProgressDialog(requireContext(), "Loading...");

        loadPastAppointments();

        return view;
    }

    private void loadPastAppointments() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        AppointmentDataAccessHelper.getInstance(getContext()).getAppointmentsForUser(userId, new AppointmentCallback() {
            @Override
            public void onAppointmentRetrieved(Appointment appointment) {
                // Not used
            }

            @Override
            public void onAppointmentsRetrieved(List<Appointment> appointments) {
                List<Appointment> pastAppointments = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String currentDateTime = sdf.format(new Date());

                for (Appointment appointment : appointments) {
                    // TODO: Figure out how to add 30 mins to the start time of the appointment.
                    //       THen replace the start time below with the end time, and use <= for the new comparison
                    String appointmentDateTime = appointment.getDate() + " " + appointment.getAppointmentStartTime();
                    if (appointmentDateTime.compareTo(currentDateTime) < 0) {
                        pastAppointments.add(appointment);
                    }
                }

                appointmentAdapter = new AppointmentAdapter(pastAppointments, DoctorDataAccessHelper.getInstance(getContext()), DepartmentDataAccessHelper.getInstance(getContext()), appointment -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("appointmentId", appointment.getId());
                    Navigation.findNavController(requireView()).navigate(R.id.action_nav_appointment_history_to_nav_previous_appointment_details, bundle);
                });

                recyclerView.setAdapter(appointmentAdapter);
                progressDialogHelper.dismissProgressDialog();
                recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down));
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "An error occurred while retrieving appointments", Toast.LENGTH_SHORT).show();
                Log.e("AppointmentHistoryFragment", "Error loading past appointments", e);
                progressDialogHelper.dismissProgressDialog();
            }
        });
    }
}
