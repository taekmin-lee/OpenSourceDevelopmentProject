package com.viewpoints.aischeduler.data.openapi.kakao;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PlaceSearchApiRequest extends Request<List<PlaceSearchResult>> {
    protected final Map<String, String> headers = new HashMap<>();
    protected final Response.Listener<List<PlaceSearchResult>> listener;

    {
        headers.put("Authorization", "KakaoAK " + OpenApiContext.KAKAO_REST_API_KEY);
    }

    public PlaceSearchApiRequest(String url, Response.Listener<List<PlaceSearchResult>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    protected void deliverResponse(List<PlaceSearchResult> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<List<PlaceSearchResult>> parseNetworkResponse(NetworkResponse response) {
        try {
            String text = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("place search", text);

            JSONObject json = new JSONObject(text);
            JSONArray array = json.getJSONArray("documents");

            ArrayList<PlaceSearchResult> result = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);

                try {
                    String address = item.getString("road_address_name");

                    if (address.isEmpty()) {
                        address = item.getString("address_name");
                    }

                    PlaceSearchResult place = new PlaceSearchResult();
                    place.setId(item.getInt("id"));
                    place.setName(item.getString("place_name"));
                    place.setCategoryCode(item.getString("category_group_code"));
                    place.setCategoryText(item.getString("category_name"));
                    place.setAddress(address);
                    place.setPhone(item.getString("phone"));
                    place.setLongitude(Double.parseDouble(item.getString("x")));
                    place.setLatitude(Double.parseDouble(item.getString("y")));
                    place.setDistance(Integer.parseInt(item.getString("distance")));

                    result.add(place);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
