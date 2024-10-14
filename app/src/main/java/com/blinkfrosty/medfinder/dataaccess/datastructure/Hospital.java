package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Hospital {

    private String id;
    private String name;
    private String streetAddress;
    private String neighborhood;
    private String city;
    private String postalCode;
    private String country;
    private String appointmentLink;

    public Hospital() { }

    public Hospital(String id, String name, String streetAddress, String neighborhood, String city,
                    String postalCode, String country, String appointmentLink) {
        this.id = id;
        this.name = name;
        this.streetAddress = streetAddress;
        this.neighborhood = neighborhood;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
        this.appointmentLink = appointmentLink;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getAppointmentLink() {
        return appointmentLink;
    }
}
