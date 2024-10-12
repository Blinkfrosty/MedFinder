package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorDataAccessHelper extends DatabaseHelperBase {

    private static DoctorDataAccessHelper instance;
    private final DatabaseReference doctorsReference;
    private static final String DOCTORS = "doctors";
    private final Map<String, ValueEventListener> doctorListenersMap;
    private final Map<String, ValueEventListener> doctorsByDepartmentListenersMap;
    private final List<ValueEventListener> doctorListeners;

    private DoctorDataAccessHelper(Context context) {
        super(context);
        doctorsReference = getDatabaseReference().child(DOCTORS);
        doctorListenersMap = new HashMap<>();
        doctorsByDepartmentListenersMap = new HashMap<>();
        doctorListeners = new ArrayList<>();
    }

    public static synchronized DoctorDataAccessHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DoctorDataAccessHelper(context.getApplicationContext());
        }
        return instance;
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
        ValueEventListener listener = new ValueEventListener() {
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
        };

        doctorsReference.child(doctorId).addValueEventListener(listener);
        doctorListenersMap.put(doctorId, listener);
    }

    public void getAllDoctors(DoctorCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
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
        };

        doctorsReference.addValueEventListener(listener);
        doctorListeners.add(listener);
    }

    public void getDoctorsByDepartment(String departmentId, DoctorCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
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
        };

        doctorsReference.orderByChild("departmentId").equalTo(departmentId).addValueEventListener(listener);
        doctorsByDepartmentListenersMap.put(departmentId, listener);
    }

    public void removeDoctorDataChangeListeners() {
        for (Map.Entry<String, ValueEventListener> entry : doctorListenersMap.entrySet()) {
            doctorsReference.child(entry.getKey()).removeEventListener(entry.getValue());
        }
        doctorListenersMap.clear();

        for (Map.Entry<String, ValueEventListener> entry : doctorsByDepartmentListenersMap.entrySet()) {
            doctorsReference.orderByChild("departmentId").equalTo(entry.getKey()).removeEventListener(entry.getValue());
        }
        doctorsByDepartmentListenersMap.clear();

        for (ValueEventListener listener : doctorListeners) {
            doctorsReference.removeEventListener(listener);
        }
        doctorListeners.clear();
    }
}