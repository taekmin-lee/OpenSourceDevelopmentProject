package com.viewpoints.aischeduler.data.openapi.kakao;

import android.location.Location;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CoordToAddressApiRequest extends Request<String> {
    protected final Map<String, String> headers = new HashMap<>();
    protected final Response.Listener<String> listener;

    {
        headers.put("Authorization", "KakaoAK " + OpenApiContext.KAKAO_REST_API_KEY);
    }

    public CoordToAddressApiRequest(Location location, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.GET, String.format("https://dapi.kakao.com/v2/local/geo/coord2address.json?x=%f&y=%f", location.getLongitude(), location.getLatitude()), errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String text = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(text);
            JSONArray array = json.getJSONArray("documents");

            if (array.length() > 0) {
                JSONObject item = array.getJSONObject(0).getJSONObject("address");
                return Response.success(item.getString("region_1depth_name") + " " + item.getString("region_2depth_name"), HttpHeaderParser.parseCacheHeaders(response));
            }

            return null;
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
