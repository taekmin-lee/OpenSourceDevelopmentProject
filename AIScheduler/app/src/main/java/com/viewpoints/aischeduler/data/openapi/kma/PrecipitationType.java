package com.viewpoints.aischeduler.data.openapi.kma;

public enum PrecipitationType {
    NONE("없음"),
    RAIN("비"),
    SLEET("비/눈"),
    SNOW("눈"),
    SHOWERS("소나기"),
    RAINDROPS("빗방울"),
    RAINDROPS_LIGHTSNOW("빗방울/눈날림"),
    LIGHTSNOW("눈날림");

    private final String text;

    PrecipitationType(String text) {
        this.text = text;
    }

    public static PrecipitationType get(int code) {
        if (code >= 0 && code < PrecipitationType.values().length) {
            return PrecipitationType.values()[code];
        }

        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
