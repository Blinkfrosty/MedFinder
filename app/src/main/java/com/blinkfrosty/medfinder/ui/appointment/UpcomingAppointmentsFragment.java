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

public class UpcomingAppointmentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private AppointmentAdapter appointmentAdapter;
    private ProgressDialogHelper progressDialogHelper;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_appointments, container, false);

        recyclerView = view.findViewById(R.id.upcoming_appointments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialogHelper = new ProgressDialogHelper();
        progressDialogHelper.showProgressDialog(requireContext(), "Loading...");

        fetchUpcomingAppointments();

        return view;
    }

    private void fetchUpcomingAppointments() {
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        AppointmentDataAccessHelper.getInstance(requireContext()).getAppointmentsForUser(userId, new AppointmentCallback() {
            @Override
            public void onAppointmentsRetrieved(List<Appointment> appointments) {
                List<Appointment> upcomingAppointments = new ArrayList<>();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String currentDateTime = sdf.format(new Date());

                for (Appointment appointment : appointments) {
                    String appointmentDateTime = appointment.getDate() + " " + appointment.getAppointmentStartTime();
                    if (appointmentDateTime.compareTo(currentDateTime) >= 0) {
                        upcomingAppointments.add(appointment);
                    }
                }

                // Sort the appointments by date and time
                upcomingAppointments.sort((a1, a2) -> {
                    String dateTime1 = a1.getDate() + " " + a1.getAppointmentStartTime();
                    String dateTime2 = a2.getDate() + " " + a2.getAppointmentStartTime();
                    return dateTime1.compareTo(dateTime2);
                });

                appointmentAdapter = new AppointmentAdapter(upcomingAppointments,
                        DoctorDataAccessHelper.getInstance(requireContext()),
                        DepartmentDataAccessHelper.getInstance(requireContext()),
                        appointment -> {
                            Bundle args = new Bundle();
                            args.putString("appointmentId", appointment.getId());
                            Navigation.findNavController(requireView()).navigate(R.id.action_nav_upcoming_appointments_to_nav_upcoming_appointment_details, args);
                        });

                recyclerView.setAdapter(appointmentAdapter);
                progressDialogHelper.dismissProgressDialog();
                recyclerView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation_fall_down));
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onAppointmentRetrieved(Appointment appointment) {
                // Not used
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "An error occurred while retrieving appointments", Toast.LENGTH_SHORT).show();
                Log.e("UpcomingAppointmentsFragment", "An error occurred while retrieving appointments", e);
                progressDialogHelper.dismissProgressDialog();
            }
        });
    }
}