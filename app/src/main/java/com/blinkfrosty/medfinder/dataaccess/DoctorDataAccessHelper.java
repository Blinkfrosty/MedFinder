package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorDataAccessHelper extends DatabaseHelperBase {

    private final DatabaseReference doctorsReference;
    private static final String DOCTORS = "doctors";

    public DoctorDataAccessHelper(Context context) {
        super(context);
        doctorsReference = getDatabaseReference().child(DOCTORS);
    }

    public void getDoctorOnce(String doctorId, DoctorCallback callback) {
        doctorsReference.child(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                if (doctor != null) {
                    callback.onDoctorRetrieved(doctor);
                } else {
                    callback.onError(new Exception("Doctor not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void addDoctorDataChangeListener(String doctorId, DoctorCallback callback) {
        doctorsReference.child(doctorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Doctor doctor = dataSnapshot.getValue(Doctor.class);
                if (doctor != null) {
                    callback.onDoctorRetrieved(doctor);
                } else {
                    callback.onError(new Exception("Doctor not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getAllDoctors(DoctorCallback callback) {
        doctorsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Doctor> doctors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        doctors.add(doctor);
                    }
                }
                callback.onDoctorsRetrieved(doctors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getDoctorsByDepartment(String departmentId, DoctorCallback callback) {
        doctorsReference.orderByChild("departmentId").equalTo(departmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Doctor> doctors = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Doctor doctor = snapshot.getValue(Doctor.class);
                    if (doctor != null) {
                        doctors.add(doctor);
                    }
                }
                callback.onDoctorsRetrieved(doctors);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
}