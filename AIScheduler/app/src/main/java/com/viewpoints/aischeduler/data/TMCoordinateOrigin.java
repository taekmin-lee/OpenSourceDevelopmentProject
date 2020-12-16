package com.viewpoints.aischeduler.data;

public enum TMCoordinateOrigin {
    WESTERN(38, 125),
    CENTRAL(38, 127),
    EASTERN(38, 129),
    EAST_SEA(38, 131),
    JEJU(38, 127);

    private final int latitude, longitude;

    TMCoordinateOrigin(int latitude, int longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }
}
