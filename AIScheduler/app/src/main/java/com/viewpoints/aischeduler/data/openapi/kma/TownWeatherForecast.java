package com.viewpoints.aischeduler.data.openapi.kma;

import java.time.LocalDateTime;

public class TownWeatherForecast extends UltrashortWeatherForecast {
    protected int precipitationProbability;
    //protected int skyType;

    public TownWeatherForecast(LocalDateTime dateTime) {
        super(dateTime);
    }

    public int getPrecipitationProbability() {
        return precipitationProbability;
    }

    public void setPrecipitationProbability(int value) {
        precipitationProbability = value;
    }

    /*public SkyType getSkyType() {
        return SkyType.get(skyType);
    }

    public void setSkyType(int value) {
        skyType = value;
    }*/
}
