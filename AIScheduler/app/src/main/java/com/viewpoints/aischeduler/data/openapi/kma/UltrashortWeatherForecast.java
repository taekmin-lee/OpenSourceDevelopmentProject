package com.viewpoints.aischeduler.data.openapi.kma;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;

public class UltrashortWeatherForecast extends CurrentWeather implements Comparable<UltrashortWeatherForecast> {
    protected int skyType;
    //protected int lightning;

    public UltrashortWeatherForecast(LocalDateTime dateTime) {
        super(dateTime);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compareTo(UltrashortWeatherForecast forecast) {
        return dateTime.compareTo(forecast.getDateTime());
    }

    public SkyType getSkyType() {
        return SkyType.get(skyType);
    }

    public void setSkyType(int value) {
        skyType = value;
    }

    /*public boolean getLightning() {
        return lightning > 0;
    }

    public void setLightning(int value) {
        lightning = value;
    }*/
}
