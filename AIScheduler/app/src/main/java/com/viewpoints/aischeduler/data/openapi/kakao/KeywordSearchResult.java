package com.viewpoints.aischeduler.data.openapi.kakao;

public class KeywordSearchResult {
    protected final String name;
    protected final String category;
    protected final String address;
    protected final double longitude;
    protected final double latitude;

    public KeywordSearchResult(String name, String category, String address, double longitude, double latitude) {
        this.name = name;
        this.category = category;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getAddress() {
        return address;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
