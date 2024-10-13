package com.blinkfrosty.medfinder.ui.doctor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.blinkfrosty.medfinder.R;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;
import com.bumptech.glide.Glide;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {

    private final List<Doctor> doctors;
    private final List<Hospital> allHospitals;
    private final NavController navController;

    public DoctorAdapter(List<Doctor> doctors, List<Hospital> allHospitals, NavController navController) {
        this.doctors = doctors;
        this.allHospitals = allHospitals;
        this.navController = navController;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_doctor, parent, false);
        return new DoctorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = doctors.get(position);
        holder.doctorName.setText(doctor.getName());
        holder.doctorDegrees.setText(doctor.getDegrees());

        // Load the profile picture using Glide
        String profilePictureUri = doctor.getProfilePictureUri();
        if (profilePictureUri == null || profilePictureUri.isEmpty()) {
            holder.doctorProfilePicture.setImageResource(R.mipmap.ic_generic_profile_img);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(profilePictureUri)
                    .placeholder(R.mipmap.ic_generic_profile_img) // Placeholder image
                    .into(holder.doctorProfilePicture);
        }

        // Set the hospital info
        String hospitalName = getHospitalNameById(doctor.getHospitalId());
        String neighborhood = getHospitalNeighborhoodById(doctor.getHospitalId());
        holder.hospitalInfo.setText(hospitalName + ", " + neighborhood);

        // Set click listener to navigate to DoctorProfileFragment
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("doctorId", doctor.getId());
            navController.navigate(R.id.action_search_by_doctor_to_doctor_profile, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        TextView doctorDegrees;
        ImageView doctorProfilePicture;
        TextView hospitalInfo; // New TextView

        public DoctorViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_name);
            doctorDegrees = itemView.findViewById(R.id.doctor_degrees);
            doctorProfilePicture = itemView.findViewById(R.id.doctor_profile_picture);
            hospitalInfo = itemView.findViewById(R.id.hospital_info); // Initialize new TextView
        }
    }

    private String getHospitalNameById(String hospitalId) {
        for (Hospital hospital : allHospitals) {
            if (hospital.getId().equals(hospitalId)) {
                return hospital.getName();
            }
        }
        return "";
    }

    private String getHospitalNeighborhoodById(String hospitalId) {
        for (Hospital hospital : allHospitals) {
            if (hospital.getId().equals(hospitalId)) {
                return hospital.getNeighborhood();
            }
        }
        return "";
    }
}