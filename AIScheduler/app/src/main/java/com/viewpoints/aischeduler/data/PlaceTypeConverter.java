package com.viewpoints.aischeduler.data;

import androidx.room.TypeConverter;

import com.viewpoints.aischeduler.data.model.PlaceType;

public class PlaceTypeConverter {
    @TypeConverter
    public static Integer fromPlaceType(PlaceType value) {
        return value == null ? null : value.ordinal();
    }

    @TypeConverter
    public static PlaceType toPlaceType(Integer value) {
        if (value != null) {
            for (PlaceType t : PlaceType.values()) {
                if (t.ordinal() == value) {
                    return t;
                }
            }
        }

        return null;
    }
}
