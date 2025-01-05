package com.blinkfrosty.medfinder.dataaccess.datastructure;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class OfficeHours implements Serializable {
    private DaySchedule monday;
    private DaySchedule tuesday;
    private DaySchedule wednesday;
    private DaySchedule thursday;
    private DaySchedule friday;
    private DaySchedule saturday;
    private DaySchedule sunday;

    public OfficeHours() { }

    public OfficeHours(DaySchedule monday, DaySchedule tuesday, DaySchedule wednesday,
                       DaySchedule thursday, DaySchedule friday, DaySchedule saturday,
                       DaySchedule sunday) {
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.sunday = sunday;
    }

    public DaySchedule getMonday() {
        return monday;
    }

    public DaySchedule getTuesday() {
        return tuesday;
    }

    public DaySchedule getWednesday() {
        return wednesday;
    }

    public DaySchedule getThursday() {
        return thursday;
    }

    public DaySchedule getFriday() {
        return friday;
    }

    public DaySchedule getSaturday() {
        return saturday;
    }

    public DaySchedule getSunday() {
        return sunday;
    }
}
