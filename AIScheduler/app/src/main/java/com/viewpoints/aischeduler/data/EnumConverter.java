package com.viewpoints.aischeduler.data;

import androidx.room.TypeConverter;

import com.viewpoints.aischeduler.data.model.VehicleType;

public class EnumConverter {
    @TypeConverter
    public static Integer fromEnum(VehicleType value) {
        return value == null ? null : value.ordinal();
    }

    @TypeConverter
    public static VehicleType toEnum(Integer value) {
        if (value != null) {
            for (VehicleType t : VehicleType.values()) {
                if (t.ordinal() == value) {
                    return t;
                }
            }
        }

        return null;
    }
}

