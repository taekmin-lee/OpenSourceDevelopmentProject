package com.viewpoints.aischeduler.data.openapi.kma;

import android.location.Location;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.viewpoints.aischeduler.data.KMACoordinate;
import com.viewpoints.aischeduler.data.openapi.OpenApiContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public class CurrentWeatherApiRequest extends Request<CurrentWeather> {
    protected final Response.Listener<CurrentWeather> listener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static LocalDateTime getDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();

        if (dateTime.getMinute() < 30) {
            dateTime = dateTime.minusHours(1);
        }

        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), dateTime.getHour(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CurrentWeatherApiRequest(Location location, Response.Listener<CurrentWeather> listener, Response.ErrorListener errorListener) {
        this(KMACoordinate.fromWGS84(location), listener, errorListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CurrentWeatherApiRequest(KMACoordinate coordinate, Response.Listener<CurrentWeather> listener, Response.ErrorListener errorListener) {
        this(coordinate, getDateTime(), listener, errorListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected CurrentWeatherApiRequest(KMACoordinate coordinate, LocalDateTime dateTime, Response.Listener<CurrentWeather> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                String.format("http://apis.data.go.kr/1360000/VilageFcstInfoService/getUltraSrtNcst?serviceKey=%s&dataType=JSON&base_date=%04d%02d%02d&base_time=%02d%02d&nx=%d&ny=%d",
                        OpenApiContext.PUBLIC_DATA_PORTAL_SERVICE_KEY,
                        dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(),
                        coordinate.getX(), coordinate.getY()
                ), errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(CurrentWeather response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<CurrentWeather> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject json = new JSONObject(new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
            JSONArray array = json.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            CurrentWeather result = new CurrentWeather();

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);

                String category = item.getString("category");
                String value = item.getString("obsrValue");

                try {
                    switch (category) {
                        case "T1H":
                            result.setTemperature(Float.parseFloat(value));
                            break;
                        case "RN1":
                            result.setPrecipitationAmount(Integer.parseInt(value));
                            break;
                        case "REH":
                            result.setHumidity(Integer.parseInt(value));
                            break;
                        case "PTY":
                            result.setPrecipitationType(Integer.parseInt(value));
                            break;
                        case "VEC":
                            result.setWindDirection(Integer.parseInt(value));
                            break;
                        case "WSD":
                            result.setWindSpeed(Float.parseFloat(value));
                            break;
                    }
                } catch (Exception e) {
                }
            }

            return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
