package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Hospital;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HospitalDataAccessHelper extends DatabaseHelperBase {

    private static HospitalDataAccessHelper instance;
    private final DatabaseReference hospitalsReference;
    private static final String HOSPITALS = "hospitals";
    private final Map<String, ValueEventListener> hospitalListenersMap;
    private final List<ValueEventListener> hospitalListeners;

    private HospitalDataAccessHelper(Context context) {
        super(context);
        hospitalsReference = getDatabaseReference().child(HOSPITALS);
        hospitalListenersMap = new HashMap<>();
        hospitalListeners = new ArrayList<>();
    }

    public static synchronized HospitalDataAccessHelper getInstance(Context context) {
        if (instance == null) {
            instance = new HospitalDataAccessHelper(context.getApplicationContext());
        }
        return instance;
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
        ValueEventListener listener = new ValueEventListener() {
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
        };

        hospitalsReference.child(hospitalId).addValueEventListener(listener);
        hospitalListenersMap.put(hospitalId, listener);
    }

    public void getAllHospitals(HospitalCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
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
        };

        hospitalsReference.addValueEventListener(listener);
        hospitalListeners.add(listener);
    }

    public void removeHospitalDataChangeListeners() {
        for (Map.Entry<String, ValueEventListener> entry : hospitalListenersMap.entrySet()) {
            hospitalsReference.child(entry.getKey()).removeEventListener(entry.getValue());
        }
        hospitalListenersMap.clear();

        for (ValueEventListener listener : hospitalListeners) {
            hospitalsReference.removeEventListener(listener);
        }
        hospitalListeners.clear();
    }
}