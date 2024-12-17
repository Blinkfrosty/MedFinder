package com.blinkfrosty.medfinder.dataaccess;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Appointment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppointmentDataAccessHelper extends DatabaseHelperBase {

    private static AppointmentDataAccessHelper instance;
    private final DatabaseReference appointmentsReference;
    private static final String APPOINTMENTS = "appointments";

    private AppointmentDataAccessHelper(Context context) {
        super(context);
        appointmentsReference = getDatabaseReference().child(APPOINTMENTS);
    }

    public static synchronized AppointmentDataAccessHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AppointmentDataAccessHelper(context.getApplicationContext());
        }
        return instance;
    }

    public void addAppointment(String userId, String appointmentStartTime, String date, String reasonForVisit, String doctorId, String hospitalId) {
        String appointmentId = UUID.randomUUID().toString().replace("-", "");
        Appointment appointment = new Appointment(appointmentId, userId, appointmentStartTime, date, reasonForVisit, doctorId, hospitalId);
        appointmentsReference.child(appointmentId).setValue(appointment)
                .addOnSuccessListener(aVoid -> Log.d("AppointmentDataAccessHelper", "Appointment stored successfully"))
                .addOnFailureListener(e -> Log.e("AppointmentDataAccessHelper", "Failed to store appointment", e));
    }

    public void getAppointmentsForDoctorOnDate(String doctorId, String date, AppointmentCallback callback) {
        appointmentsReference.orderByChild("doctorId").equalTo(doctorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Appointment> appointments = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Appointment appointment = snapshot.getValue(Appointment.class);
                    if (appointment != null && appointment.getDate().equals(date)) {
                        appointments.add(appointment);
                    }
                }
                callback.onAppointmentsRetrieved(appointments);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.toException());
            }
        });
    }
}