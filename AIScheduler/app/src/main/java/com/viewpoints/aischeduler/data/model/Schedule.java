package com.viewpoints.aischeduler.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.viewpoints.aischeduler.data.DateTimeConverter;
import com.viewpoints.aischeduler.data.EnumConverter;

import java.time.LocalDateTime;

@Entity
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    protected int id;

    protected String name;

    @TypeConverters({DateTimeConverter.class})
    protected LocalDateTime start;

    @TypeConverters({DateTimeConverter.class})
    protected LocalDateTime end;

    @ColumnInfo(name="place_name")
    protected String placeName;

    protected Double longitude, latitude;

    @ColumnInfo(name = "all_day")
    protected boolean allDay;

    @ColumnInfo(name = "vehicle_type")
    @TypeConverters({EnumConverter.class})
    protected VehicleType vehicleType;

    protected String memo;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
