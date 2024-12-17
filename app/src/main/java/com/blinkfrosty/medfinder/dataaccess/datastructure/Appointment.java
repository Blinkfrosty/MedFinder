package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Appointment {

    private String id;
    private String userId;
    private String appointmentStartTime;
    private String date;
    private String reasonForVisit;
    private String doctorId;
    private String hospitalId;
    private String appointmentNotes;

    public Appointment() { }

    public Appointment(String id, String userId, String appointmentStartTime, String date, String reasonForVisit, String doctorId, String hospitalId) {
        this.id = id;
        this.userId = userId;
        this.appointmentStartTime = appointmentStartTime;
        this.date = date;
        this.reasonForVisit = reasonForVisit;
        this.doctorId = doctorId;
        this.hospitalId = hospitalId;
        this.appointmentNotes = "";
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getAppointmentStartTime() {
        return appointmentStartTime;
    }

    public String getDate() {
        return date;
    }

    public String getReasonForVisit() {
        return reasonForVisit;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public String getAppointmentNotes() {
        return appointmentNotes;
    }

    public void setAppointmentNotes(String appointmentNotes) {
        this.appointmentNotes = appointmentNotes;
    }
}