package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HospitalDataAccessHelper extends DatabaseHelperBase {

    private final DatabaseReference hospitalsReference;
    private static final String HOSPITALS = "hospitals";

    public HospitalDataAccessHelper(Context context) {
        super(context);
        hospitalsReference = getDatabaseReference().child(HOSPITALS);
    }

    public void getHospitalOnce(String hospitalId, HospitalCallback callback) {
        hospitalsReference.child(hospitalId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Hospital hospital = dataSnapshot.getValue(Hospital.class);
                if (hospital != null) {
                    callback.onHospitalRetrieved(hospital);
                } else {
                    callback.onError(new Exception("Hospital not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void addHospitalDataChangeListener(String hospitalId, HospitalCallback callback) {
        hospitalsReference.child(hospitalId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Hospital hospital = dataSnapshot.getValue(Hospital.class);
                if (hospital != null) {
                    callback.onHospitalRetrieved(hospital);
                } else {
                    callback.onError(new Exception("Hospital not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }

    public void getAllHospitals(HospitalCallback callback) {
        hospitalsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Hospital> hospitals = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hospital hospital = snapshot.getValue(Hospital.class);
                    if (hospital != null) {
                        hospitals.add(hospital);
                    }
                }
                callback.onHospitalsRetrieved(hospitals);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
}