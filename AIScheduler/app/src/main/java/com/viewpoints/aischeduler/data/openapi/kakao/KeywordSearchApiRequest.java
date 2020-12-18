package com.viewpoints.aischeduler.data.openapi.kakao;

import android.location.Location;

import com.android.volley.Response;

import java.util.List;

public class KeywordSearchApiRequest extends PlaceSearchApiRequest {
    public KeywordSearchApiRequest(String query, Location location, Response.Listener<List<PlaceSearchResult>> listener, Response.ErrorListener errorListener) {
        super(String.format("https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&x=%f&y=%f", query, location.getLongitude(), location.getLatitude()), listener, errorListener);
    }
}
