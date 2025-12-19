package com.epicare.model;
public class Patient {
    private String name;
    private String dateTime;

    public Patient(String name, String dateTime) {
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getName() {
        return name;
    }

    public String getDateTime() {
        return dateTime;
    }
}
