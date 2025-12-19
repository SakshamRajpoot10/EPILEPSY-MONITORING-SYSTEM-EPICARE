package com.epicare.model;

public class LocationDataModel {
    private double latitude;
    private double longitude;
    private long timestamp;

    public LocationDataModel(double latitude, double longitude, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public long getTimestamp() { return timestamp; }
}

