package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class DaySchedule implements Serializable {
    private boolean available;
    private String startTime;
    private String endTime;

    public DaySchedule() { }

    public DaySchedule(boolean available, String startTime, String endTime) {
        this.available = available;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
