package com.blinkfrosty.medfinder.ui.appointment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.DepartmentCallback;
import com.blinkfrosty.medfinder.dataaccess.DepartmentDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.DoctorCallback;
import com.blinkfrosty.medfinder.dataaccess.DoctorDataAccessHelper;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Appointment;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Department;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final List<Appointment> appointments;
    private final DoctorDataAccessHelper doctorDataAccessHelper;
    private final DepartmentDataAccessHelper departmentDataAccessHelper;
    private final OnAppointmentClickListener onAppointmentClickListener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> appointments, DoctorDataAccessHelper doctorDataAccessHelper,
                              DepartmentDataAccessHelper departmentDataAccessHelper, OnAppointmentClickListener onAppointmentClickListener) {
        this.appointments = appointments;
        this.doctorDataAccessHelper = doctorDataAccessHelper;
        this.departmentDataAccessHelper = departmentDataAccessHelper;
        this.onAppointmentClickListener = onAppointmentClickListener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.itemView.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.item_click_animation));
            onAppointmentClickListener.onAppointmentClick(appointment);
        });

        doctorDataAccessHelper.getDoctorOnce(appointment.getDoctorId(), new DoctorCallback() {
            @Override
            public void onDoctorRetrieved(Doctor doctor) {
                holder.doctorName.setText(doctor.getName());
            }

            @Override
            public void onDoctorsRetrieved(List<Doctor> doctors) {
                // Not used
            }

            @Override
            public void onError(Exception e) {
                holder.doctorName.setText(R.string.error_loading_doctor);
            }
        });

        departmentDataAccessHelper.getDepartmentOnce(appointment.getDepartmentId(), new DepartmentCallback() {
            @Override
            public void onDepartmentRetrieved(Department department) {
                holder.departmentName.setText(department.getName());
            }

            @Override
            public void onDepartmentsRetrieved(List<Department> departments) {
                // Not used
            }

            @Override
            public void onError(Exception e) {
                holder.departmentName.setText(R.string.error_loading_department);
            }
        });

        // Convert 24-hour format to 12-hour format
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            SimpleDateFormat sdf12 = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
            String appointmentDateTime24 = appointment.getDate() + " " + appointment.getAppointmentStartTime();
            Date date = sdf24.parse(appointmentDateTime24);
            String appointmentDateTime12 = sdf12.format(date);
            holder.appointmentDateTime.setText(appointmentDateTime12);
        } catch (Exception e) {
            holder.appointmentDateTime.setText(R.string.error_loading_date_time);
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        TextView departmentName;
        TextView appointmentDateTime;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            departmentName = itemView.findViewById(R.id.department_name);
            appointmentDateTime = itemView.findViewById(R.id.appointment_date_time);
        }
    }
}