package com.viewpoints.aischeduler.data.openapi.kma;

public enum WindDirectionType {
    N("북"),
    NNE("북북동"),
    NE("북동"),
    ENE("동북동"),
    E("동"),
    ESE("동남동"),
    SE("남동"),
    SSE("남남동"),
    S("남"),
    SSW("남남서"),
    SW("남서"),
    WSW("서남서"),
    W("서"),
    WNW("서북서"),
    NW("북서"),
    NNW("북북서");

    private final String text;

    WindDirectionType(final String text) {
        this.text = text;
    }

    public static WindDirectionType get(int degree) {
        int value = (int) ((degree + 22.5 * 0.5) / 22.5);

        if (value >= 0 && value < WindDirectionType.values().length) {
            return WindDirectionType.values()[value];
        } else if (value == 16) {
            return WindDirectionType.N;
        }

        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
