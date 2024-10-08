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

}
