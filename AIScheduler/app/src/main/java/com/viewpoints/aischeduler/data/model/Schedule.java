package com.viewpoints.aischeduler.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.viewpoints.aischeduler.data.DateTimeConverter;
import com.viewpoints.aischeduler.data.VehicleTypeConverter;
import com.viewpoints.aischeduler.data.PlaceTypeConverter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
public class Schedule implements Serializable {
    @PrimaryKey(autoGenerate = true)
    protected int id;

    protected String name;

    @TypeConverters({DateTimeConverter.class})
    protected LocalDateTime start;

    @TypeConverters({DateTimeConverter.class})
    protected LocalDateTime end;

    @ColumnInfo(name = "place_name")
    protected String placeName;

    @ColumnInfo(name = "place_id")
    protected Integer placeId;

    @ColumnInfo(name = "place_type")
    @TypeConverters({PlaceTypeConverter.class})
    protected PlaceType placeType;

    @ColumnInfo(name = "place_category_id")
    protected Integer placeCategoryId;

    @ColumnInfo(name = "place_category_name")
    protected String placeCategoryName;

    @ColumnInfo(name = "place_longitude")
    protected Double placeLongitude;

    @ColumnInfo(name = "place_latitude")
    protected Double placeLatitude;

    @ColumnInfo(name = "all_day")
    protected boolean allDay;

    @ColumnInfo(name = "vehicle_type")
    @TypeConverters({VehicleTypeConverter.class})
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

    public void setPlaceName(String name) {
        placeName = name;
    }

    public Integer getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Integer id) {
        placeId = id;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public Integer getPlaceCategoryId() {
        return placeCategoryId;
    }

    public void setPlaceCategoryId(Integer placeCategoryId) {
        this.placeCategoryId = placeCategoryId;
    }

    public String getPlaceCategoryName() {
        return placeCategoryName;
    }

    public void setPlaceCategoryName(String placeCategoryName) {
        this.placeCategoryName = placeCategoryName;
    }

    public Double getPlaceLongitude() {
        return placeLongitude;
    }

    public void setPlaceLongitude(Double longitude) {
        placeLongitude = longitude;
    }

    public Double getPlaceLatitude() {
        return placeLatitude;
    }

    public void setPlaceLatitude(Double latitude) {
        placeLatitude = latitude;
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
