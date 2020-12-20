package com.viewpoints.aischeduler.data;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

public class UserLocationContext {
    private static UserLocationContext instance;

    private Context context;
    private LocationManager manager;
    private Location location;

    private UserLocationContext(Context context) {
        this.context = context;

        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    this.location = location;
                }

                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, l -> this.location = l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized UserLocationContext getInstance(Context context) {
        if (instance == null) {
            instance = new UserLocationContext(context);
        }
        return instance;
    }

    public Location getLocation() {
        if (location == null) {
            location = new Location("dummy");
            location.setLongitude(127.454444033872);
            location.setLatitude(36.625621535916);
        }

        return location;
    }
}
