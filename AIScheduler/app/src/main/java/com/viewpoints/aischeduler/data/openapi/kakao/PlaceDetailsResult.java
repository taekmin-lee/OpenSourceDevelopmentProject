package com.viewpoints.aischeduler.data.openapi.kakao;

import java.util.HashMap;
import java.util.Map;

public class PlaceDetailsResult {
    protected String photoUrl;
    protected String categoryName;
    protected Map<String, String> menus = new HashMap<>();
    protected Map<String, String> openHours = new HashMap<>();

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Map<String, String> getMenus() {
        return menus;
    }

    public void addMenu(String name, String price) {
        menus.put(name, price);
    }

    public Map<String, String> getOpenHours() {
        return openHours;
    }

    public void addOpenHours(String name, String time) {
        openHours.put(name, time);
    }
}
