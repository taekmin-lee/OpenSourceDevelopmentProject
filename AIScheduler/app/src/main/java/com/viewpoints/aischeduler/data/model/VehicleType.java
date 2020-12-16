package com.viewpoints.aischeduler.data.model;

public enum VehicleType {
    CAR("자동차"),
    TRANSIT("대중교통");

    private final String text;

    VehicleType(String text) {
        this.text = text;
    }

    public static VehicleType get(int code) {
        switch (code) {
            case 0:
                return VehicleType.CAR;
            case 1:
                return VehicleType.TRANSIT;
        }

        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
