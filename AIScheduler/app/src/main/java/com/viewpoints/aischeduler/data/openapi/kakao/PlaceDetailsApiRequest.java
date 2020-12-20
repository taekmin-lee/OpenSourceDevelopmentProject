package com.viewpoints.aischeduler.data.openapi.kakao;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlaceDetailsApiRequest extends Request<PlaceDetailsResult> {
    protected final Response.Listener<PlaceDetailsResult> listener;

    public PlaceDetailsApiRequest(int id, Response.Listener<PlaceDetailsResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET, "https://place.map.kakao.com/main/v/" + id, errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(PlaceDetailsResult response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<PlaceDetailsResult> parseNetworkResponse(NetworkResponse response) {
        try {
            String text = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("place details", text);

            JSONObject json = new JSONObject(text);

            PlaceDetailsResult result = new PlaceDetailsResult();
            JSONObject basicInfo = json.getJSONObject("basicInfo");

            if (basicInfo.has("mainphotourl")) {
                result.setPhotoUrl(basicInfo.getString("mainphotourl"));
            }

            result.setCategoryId(Integer.parseInt(basicInfo.getString("cateid")));
            result.setCategoryName(basicInfo.getString("catename"));

            if (basicInfo.has("openHour")) {
                JSONObject periodList = basicInfo.getJSONObject("openHour").getJSONArray("periodList").getJSONObject(0);

                if (periodList.has("timeList")) {
                    JSONArray timeList = periodList.getJSONArray("timeList");

                    for (int i = 0; i < timeList.length(); i++) {
                        JSONObject time = timeList.getJSONObject(i);
                        result.addOpenHours(time.getString("dayOfWeek"), time.getString("timeSE"));
                    }
                }
            } else if (json.has("hospitalInfo")) {
                JSONObject hospitalInfo = json.getJSONObject("hospitalInfo").getJSONArray("list").getJSONObject(0);

                if (hospitalInfo.has("openHourList")) {
                    JSONArray openHourList = hospitalInfo.getJSONArray("openHourList");

                    for (int i = 0; i < openHourList.length(); i++) {
                        JSONObject openHour = openHourList.getJSONObject(i);
                        result.addOpenHours(openHour.getString("day"), openHour.getString("time"));
                    }
                }
            }

            if (json.has("menuInfo")) {
                JSONArray menuList = json.getJSONObject("menuInfo").getJSONArray("menuList");

                for (int i = 0; i < menuList.length(); i++) {
                    JSONObject menu = menuList.getJSONObject(i);
                    result.addMenu(menu.getString("menu"), menu.getString("price"));
                }
            } else if (json.has("parkingLotInfo")) {
                JSONArray offlinePriceList = json.getJSONObject("parkingLotInfo").getJSONArray("offlinePriceList");

                for (int i = 0; i < offlinePriceList.length(); i++) {
                    JSONObject menu = offlinePriceList.getJSONObject(i);
                    result.addMenu(menu.getString("title"), menu.getString("price"));
                }
            } else if (json.has("oilPriceInfo")) {
                JSONArray priceList = json.getJSONObject("oilPriceInfo").getJSONArray("priceList");

                for (int i = 0; i < priceList.length(); i++) {
                    JSONObject menu = priceList.getJSONObject(i);
                    result.addMenu(menu.getString("type"), menu.getString("price"));
                }
            } else if (json.has("parkingInfo")) {
                JSONObject parkingInfo = json.getJSONObject("parkingInfo");

                if (parkingInfo.length() > 0) {
                    JSONArray offlinePriceList = parkingInfo.getJSONArray(parkingInfo.keys().next());

                    for (int i = 0; i < offlinePriceList.length(); i++) {
                        JSONObject menu = offlinePriceList.getJSONObject(i);
                        result.addMenu(menu.getString("title"), menu.getString("price"));
                    }
                }
            } else if (json.has("sleepInfo")) {
                JSONArray list = json.getJSONObject("sleepInfo").getJSONArray("list");

                if (list.length() > 0) {
                    JSONObject item = list.getJSONObject(0);

                    if (item.has("roomList")) {
                        JSONArray roomList = item.getJSONArray("roomList");

                        for (int i = 0; i < roomList.length(); i++) {
                            JSONObject menu = roomList.getJSONObject(i);
                            result.addMenu(menu.getString("name"), menu.getString("price1"));
                        }
                    }
                }
            }

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
