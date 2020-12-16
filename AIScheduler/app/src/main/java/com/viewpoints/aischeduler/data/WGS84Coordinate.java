package com.viewpoints.aischeduler.data;

public class WGS84Coordinate {
    protected final double longitude, latitude;

    public WGS84Coordinate(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
