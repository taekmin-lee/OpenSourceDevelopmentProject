package com.viewpoints.aischeduler.data.openapi.kma;

import java.time.LocalDateTime;

public class UltrashortWeatherForecast extends CurrentWeather {
    protected int skyType;
    //protected int lightning;

    public UltrashortWeatherForecast(LocalDateTime dateTime) {
        super(dateTime);
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
