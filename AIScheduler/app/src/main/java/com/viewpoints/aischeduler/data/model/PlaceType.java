package com.viewpoints.aischeduler.data.model;

public enum PlaceType {
    MART("MT1", "대형마트"),
    CONVENIENCE_STORE("CS2", "편의점"),
    KINDERGARTEN("PS3", "어린이집, 유치원"),
    SCHOOL("SC4", "학교"),
    ACADEMY("AC5", "학원"),
    PARKING("PK6", "주차장"),
    GAS_STATION("OL7", "주유소, 충전소"),
    SUBWAY_STATION("SW8", "지하철역"),
    BANK("BK9", "은행"),
    CULTURE_FACILITIES("CT1", "문화시설"),
    AGENCY("AG2", "중개업소"),
    PUBLIC_OFFICE("PO3", "공공기관"),
    ATTRACTIONS("AT4", "관광지"),
    ACCOMMODATION("AD5", "숙박"),
    RESTAURANT("FD6", "음식점"),
    CAFE("CE7", "카페"),
    HOSPITAL("HP8", "병원"),
    PHARMACY("PM9", "약국");

    private final String code, text;

    PlaceType(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public static PlaceType get(String code) {
        for (PlaceType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return text;
    }
}
