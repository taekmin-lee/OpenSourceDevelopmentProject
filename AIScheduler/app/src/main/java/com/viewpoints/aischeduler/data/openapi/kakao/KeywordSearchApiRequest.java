package com.viewpoints.aischeduler.data.openapi.kakao;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.viewpoints.aischeduler.data.WGS84Coordinate;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeywordSearchApiRequest extends Request<List<KeywordSearchResult>> {
    protected final Map<String, String> headers = new HashMap<>();
    protected final Response.Listener<List<KeywordSearchResult>> listener;

    {
        headers.put("Authorization", "KakaoAK " + OpenApiContext.KAKAO_REST_API_KEY);
    }

    public KeywordSearchApiRequest(String query, Response.Listener<List<KeywordSearchResult>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, String.format("https://dapi.kakao.com/v2/local/search/keyword.json?query=%s", query), errorListener);
        this.listener = listener;
    }

    public KeywordSearchApiRequest(String query, WGS84Coordinate coordinate, Response.Listener<List<KeywordSearchResult>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, String.format("https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&x=%f&y=%f", query, coordinate.getLongitude(), coordinate.getLatitude()), errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    protected void deliverResponse(List<KeywordSearchResult> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<List<KeywordSearchResult>> parseNetworkResponse(NetworkResponse response) {
        try {
            String text = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            Log.d("JSON result", text);

            JSONObject json = new JSONObject(text);
            JSONArray array = json.getJSONArray("documents");

            ArrayList<KeywordSearchResult> result = new ArrayList<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);

                try {
                    String address = item.getString("road_address_name");

                    if (address.isEmpty())
                    {
                        address = item.getString("address_name");
                    }

                    result.add(new KeywordSearchResult(
                        item.getString("place_name"),
                        item.getString("category_name"),
                        address,
                        Double.parseDouble(item.getString("x")),
                        Double.parseDouble(item.getString("y"))
                    ));
                } catch (Exception e) {
                }
            }

            Log.d("array list", result.toString());

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
