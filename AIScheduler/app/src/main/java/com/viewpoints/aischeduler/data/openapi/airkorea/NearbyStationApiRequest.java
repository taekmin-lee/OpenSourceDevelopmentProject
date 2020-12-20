package com.viewpoints.aischeduler.data.openapi.airkorea;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.viewpoints.aischeduler.data.TMCoordinate;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class NearbyStationApiRequest extends Request<List<String>> {
    protected final Response.Listener<List<String>> listener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NearbyStationApiRequest(Location location, Response.Listener<List<String>> listener, Response.ErrorListener errorListener) {
        this(TMCoordinate.fromLocation(location), listener, errorListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected NearbyStationApiRequest(TMCoordinate coordinate, Response.Listener<List<String>> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                String.format("http://apis.data.go.kr/B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList?serviceKey=%s&tmX=%f&tmY=%f&returnType=json",
                        OpenApiContext.PUBLIC_DATA_PORTAL_SERVICE_KEY,
                        coordinate.getX(), coordinate.getY()
                ), errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(List<String> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<List<String>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject json = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
            JSONArray items = json.getJSONObject("response").getJSONObject("body").getJSONArray("items");

            List<String> result = new ArrayList<>();

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                result.add(item.getString("stationName"));
            }

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
