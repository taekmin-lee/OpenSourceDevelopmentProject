package com.viewpoints.aischeduler.data;

import androidx.room.TypeConverter;

import com.viewpoints.aischeduler.data.model.VehicleType;

public class VehicleTypeConverter {
    @TypeConverter
    public static Integer fromVehicleType(VehicleType value) {
        return value == null ? null : value.ordinal();
    }

    @TypeConverter
    public static VehicleType toVehicleType(Integer value) {
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

