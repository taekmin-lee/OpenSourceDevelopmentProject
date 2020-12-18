package com.viewpoints.aischeduler.data.openapi.kakao;

import android.location.Location;

import com.android.volley.Response;
import com.viewpoints.aischeduler.data.model.PlaceType;

import java.util.List;

public class CategorySearchApiRequest extends PlaceSearchApiRequest {
    public CategorySearchApiRequest(PlaceType placeType, Location location, int radius, Response.Listener<List<PlaceSearchResult>> listener, Response.ErrorListener errorListener) {
        super(String.format("https://dapi.kakao.com/v2/local/search/category.json?category_group_code=%s&x=%f&y=%f&radius=%d", placeType.getCode(), location.getLongitude(), location.getLatitude(), radius), listener, errorListener);
    }
}
