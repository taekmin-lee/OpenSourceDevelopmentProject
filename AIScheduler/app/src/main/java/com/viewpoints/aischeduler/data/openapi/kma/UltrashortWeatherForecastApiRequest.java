package com.viewpoints.aischeduler.data.openapi.kma;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.viewpoints.aischeduler.data.KMACoordinate;
import com.viewpoints.aischeduler.data.WGS84Coordinate;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UltrashortWeatherForecastApiRequest extends Request<List<UltrashortWeatherForecast>> {
    protected final Map<String, String> headers = new HashMap<>();
    protected final Response.Listener<List<UltrashortWeatherForecast>> listener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static LocalDateTime getDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();

        if (dateTime.getMinute() < 45) {
            dateTime = dateTime.minusHours(1);
        }

        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), dateTime.getHour(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UltrashortWeatherForecastApiRequest(WGS84Coordinate coordinate, Response.Listener<List<UltrashortWeatherForecast>> listener, Response.ErrorListener errorListener) {
        this(KMACoordinate.fromWGS84(coordinate), listener, errorListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UltrashortWeatherForecastApiRequest(KMACoordinate coordinate, Response.Listener<List<UltrashortWeatherForecast>> listener, Response.ErrorListener errorListener) {
        this(coordinate, getDateTime(), listener, errorListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected UltrashortWeatherForecastApiRequest(KMACoordinate coordinate, LocalDateTime dateTime, Response.Listener<List<UltrashortWeatherForecast>> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                String.format("http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtFcst?serviceKey=%s&numOfRows=100&dataType=JSON&base_date=%04d%02d%02d&base_time=%02d%02d&nx=%d&ny=%d",
                        OpenApiContext.PUBLIC_DATA_PORTAL_SERVICE_KEY,
                        dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(),
                        coordinate.getX(), coordinate.getY()
                ), errorListener);
        this.listener = listener;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    protected void deliverResponse(List<UltrashortWeatherForecast> response) {
        listener.onResponse(response);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Response<List<UltrashortWeatherForecast>> parseNetworkResponse(NetworkResponse response) {
        try {
            String text = new String(response.data, HttpHeaderParser.parseCharset(response.headers));

            Log.d("Weather Forecast Ultra", text);

            JSONObject json = new JSONObject(text);
            JSONArray array = json.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            HashMap<LocalDateTime, UltrashortWeatherForecast> result = new HashMap<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);

                LocalDateTime dateTime = LocalDateTime.parse(item.getString("fcstDate") + item.getString("fcstTime"), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                UltrashortWeatherForecast forecast;

                if (result.containsKey(dateTime)) {
                    forecast = result.get(dateTime);
                } else {
                    forecast = new UltrashortWeatherForecast(dateTime);
                    result.put(dateTime, forecast);
                }

                String category = item.getString("category");
                String value = item.getString("fcstValue");

                try {
                    switch (category) {
                        case "T1H":
                            forecast.setTemperature(Float.parseFloat(value));
                            break;
                        case "RN1":
                            forecast.setPrecipitationAmount(Integer.parseInt(value));
                            break;
                        case "SKY":
                            forecast.setSkyType(Integer.parseInt(value));
                            break;
                        case "REH":
                            forecast.setHumidity(Integer.parseInt(value));
                            break;
                        case "PTY":
                            forecast.setPrecipitationType(Integer.parseInt(value));
                            break;
                        /*case "LGT":
                            forecast.setLightning(Integer.parseInt(value));
                            break;*/
                        case "VEC":
                            forecast.setWindDirection(Integer.parseInt(value));
                            break;
                        case "WSD":
                            forecast.setWindSpeed(Float.parseFloat(value));
                            break;
                    }
                } catch (Exception e) {
                    Log.d("forecast", "error " + e);
                }
            }

            List<UltrashortWeatherForecast> list = new ArrayList<>();
            list.addAll(result.values());

            return Response.success(list, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            Log.d("forecast", "encoding error " + e);
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            Log.d("forecast", "json error " + e);
            return Response.error(new ParseError(e));
        }
    }
}
