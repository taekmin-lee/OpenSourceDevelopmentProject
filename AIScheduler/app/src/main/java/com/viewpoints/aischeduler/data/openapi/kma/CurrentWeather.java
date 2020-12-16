package com.viewpoints.aischeduler.data.openapi.kma;

import java.time.LocalDateTime;

public class CurrentWeather {
    protected LocalDateTime dateTime;

    protected float temperature;
    protected float precipitationAmount;
    protected int humidity;
    protected int precipitationType;
    protected int windDirection;
    protected float windSpeed;

    public CurrentWeather() {

    }

    public CurrentWeather(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getPrecipitationAmount() {
        return precipitationAmount;
    }

    public void setPrecipitationAmount(float precipitationAmount) {
        this.precipitationAmount = precipitationAmount;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public PrecipitationType getPrecipitationType() {
        return PrecipitationType.get(precipitationType);
    }

    public void setPrecipitationType(int precipitationType) {
        this.precipitationType = precipitationType;
    }

    public WindDirectionType getWindDirectionType() {
        return WindDirectionType.get(windDirection);
    }

    public int getWindDirectionDegree() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }
}
