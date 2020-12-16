package com.viewpoints.aischeduler.data.openapi.kma;

public enum SkyType {
    CLEAR("맑음"),
    MOSTLY_CLOUDY("구름 많음"),
    CLOUDY("흐림");

    private final String text;

    SkyType(String text) {
        this.text = text;
    }

    public static SkyType get(int code) {
        switch (code) {
            case 1:
                return SkyType.CLEAR;
            case 3:
                return SkyType.MOSTLY_CLOUDY;
            case 4:
                return SkyType.CLOUDY;
        }

        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
