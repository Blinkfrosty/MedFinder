package com.blinkfrosty.medfinder.dataaccess;

import com.blinkfrosty.medfinder.dataaccess.datastructure.Appointment;

import java.util.List;

public interface AppointmentCallback {
    void onAppointmentRetrieved(Appointment appointment);
    void onAppointmentsRetrieved(List<Appointment> appointments);
    void onError(Exception e);
}