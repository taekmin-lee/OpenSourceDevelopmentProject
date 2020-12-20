package com.viewpoints.aischeduler.data.openapi.airkorea;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

import org.json.JSONObject;

public class RealtimeAirPollutionApiRequest extends Request<RealtimeAirPollutionResult> {
    protected final Response.Listener<RealtimeAirPollutionResult> listener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public RealtimeAirPollutionApiRequest(String station, Response.Listener<RealtimeAirPollutionResult> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                String.format("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty?serviceKey=%s&stationName=%s&dataTerm=DAILY&ver=1.0&returnType=json",
                        OpenApiContext.PUBLIC_DATA_PORTAL_SERVICE_KEY, station
                ), errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(RealtimeAirPollutionResult response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<RealtimeAirPollutionResult> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject json = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
            JSONObject item = json.getJSONObject("response").getJSONObject("body").getJSONArray("items").getJSONObject(0);

            RealtimeAirPollutionResult result = new RealtimeAirPollutionResult();

            try {
                result.setSo2(Double.parseDouble(item.getString("so2Value")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                result.setCo(Double.parseDouble(item.getString("coValue")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                result.setO3(Double.parseDouble(item.getString("o3Value")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                result.setNo2(Double.parseDouble(item.getString("no2Value")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                result.setPm10(Integer.parseInt(item.getString("pm10Value")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                result.setPm25(Integer.parseInt(item.getString("pm25Value")));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            return Response.error(new ParseError(e));
        }
    }
}
