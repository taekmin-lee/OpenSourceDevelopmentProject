package com.viewpoints.aischeduler.data.openapi.kma;

import android.location.Location;
import android.os.Build;
import android.util.Log;

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
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;

public class TownWeatherForecastApiRequest extends Request<Collection<TownWeatherForecast>> {
    protected final Response.Listener<Collection<TownWeatherForecast>> listener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static LocalDateTime getDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();

        if (dateTime.getHour() % 3 != 2 || dateTime.getMinute() < 10) {
            if (dateTime.getHour() <= 2) {
                dateTime = dateTime.minusDays(1).withHour(23);
            } else {
                dateTime = dateTime.minusHours((dateTime.getHour() - 2) % 3);
            }
        }

        Log.d("town weather", "getting data of " + dateTime);
        return LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), dateTime.getHour(), 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TownWeatherForecastApiRequest(Location location, Response.Listener<Collection<TownWeatherForecast>> listener, Response.ErrorListener errorListener) {
        this(KMACoordinate.fromWGS84(location), listener, errorListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public TownWeatherForecastApiRequest(KMACoordinate coordinate, Response.Listener<Collection<TownWeatherForecast>> listener, Response.ErrorListener errorListener) {
        this(coordinate, getDateTime(), listener, errorListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected TownWeatherForecastApiRequest(KMACoordinate coordinate, LocalDateTime dateTime, Response.Listener<Collection<TownWeatherForecast>> listener, Response.ErrorListener errorListener) {
        super(Method.GET,
                String.format("http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=%s&numOfRows=100&dataType=JSON&base_date=%04d%02d%02d&base_time=%02d%02d&nx=%d&ny=%d",
                        OpenApiContext.PUBLIC_DATA_PORTAL_SERVICE_KEY,
                        dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute(),
                        coordinate.getX(), coordinate.getY()
                ), errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(Collection<TownWeatherForecast> response) {
        listener.onResponse(response);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected Response<Collection<TownWeatherForecast>> parseNetworkResponse(NetworkResponse response) {
        try {
            String text = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            JSONObject json = new JSONObject(text);
            JSONArray array = json.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            HashMap<LocalDateTime, TownWeatherForecast> result = new HashMap<>();

            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);

                LocalDateTime dateTime = LocalDateTime.parse(item.getString("fcstDate") + item.getString("fcstTime"), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
                TownWeatherForecast forecast;

                if (result.containsKey(dateTime)) {
                    forecast = result.get(dateTime);
                } else {
                    forecast = new TownWeatherForecast(dateTime);
                    result.put(dateTime, forecast);
                }

                String category = item.getString("category");
                String value = item.getString("fcstValue");

                try {
                    switch (category) {
                        case "POP":
                            forecast.setPrecipitationProbability(Integer.parseInt(value));
                            break;
                        case "T3H":
                            forecast.setTemperature(Float.parseFloat(value));
                            break;
                        case "R06":
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
                        case "VEC":
                            forecast.setWindDirection(Integer.parseInt(value));
                            break;
                        case "WSD":
                            forecast.setWindSpeed(Float.parseFloat(value));
                            break;
                    }
                } catch (Exception e) {
                }
            }

            return Response.success(result.values(), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
