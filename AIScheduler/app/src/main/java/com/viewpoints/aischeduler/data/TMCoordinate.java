package com.viewpoints.aischeduler.data;

import android.location.Location;

public class TMCoordinate {
    protected final double x, y;
    protected final TMCoordinateOrigin origin;
    protected final TMCoordinateType type;

    public TMCoordinate(double x, double y, TMCoordinateOrigin origin, TMCoordinateType type) {
        this.x = x;
        this.y = y;
        this.origin = origin;
        this.type = type;
    }

    public static TMCoordinate fromLocation(Location location) {
        return fromLocation(location, TMCoordinateOrigin.CENTRAL, TMCoordinateType.GRS80);
    }

    public static TMCoordinate fromLocation(Location location, TMCoordinateOrigin origin, TMCoordinateType type) {
        double lon = Math.toRadians(location.getLongitude());
        double lat = Math.toRadians(location.getLatitude());

        double lon0 = Math.toRadians(origin.getLongitude());
        double lat0 = Math.toRadians(origin.getLatitude());

        double e2 = (Math.pow(type.getSemiMajorAxis(), 2) - Math.pow(type.getSemiMinorAxis(), 2)) / Math.pow(type.getSemiMinorAxis(), 2);
        double e2p = (Math.pow(type.getSemiMajorAxis(), 2) - Math.pow(type.getSemiMinorAxis(), 2)) / Math.pow(type.getSemiMinorAxis(), 2);

        double t = Math.pow(Math.tan(lat), 2);
        double c = e2 / (1 - e2) * Math.pow(Math.cos(lat), 2);
        double a = (lon - lon0) * Math.cos(lat);
        double n = type.getSemiMajorAxis() / Math.sqrt(1 - e2 * Math.pow(Math.sin(lat), 2));
        double m = type.getSemiMajorAxis() * ((1 - e2 / 4 - 3 * Math.pow(e2, 2) / 64 - 5 * Math.pow(e2, 3) / 256) * lat - (3 * e2 / 8 + 3 * Math.pow(e2, 2) / 32 + 45 * Math.pow(e2, 3) / 1024) * Math.sin(2 * lat) + (15 * Math.pow(e2, 2) / 256 + 45 * Math.pow(e2, 3) / 1024) * Math.sin(4 * lat) - (35 * Math.pow(e2, 3) / 3072) * Math.sin(6 * lat));
        double m0 = type.getSemiMajorAxis() * ((1 - e2 / 4 - 3 * Math.pow(e2, 2) / 64 - 5 * Math.pow(e2, 3) / 256) * lat0 - (3 * e2 / 8 + 3 * Math.pow(e2, 2) / 32 + 45 * Math.pow(e2, 3) / 1024) * Math.sin(2 * lat0) + (15 * Math.pow(e2, 2) / 256 + 45 * Math.pow(e2, 3) / 1024) * Math.sin(4 * lat0) - (35 * Math.pow(e2, 3) / 3072) * Math.sin(6 * lat0));

        double x = type.getOffsetX(origin) + type.getScaleCoefficient() * (m - m0 + n * Math.tan(lat) * (Math.pow(a, 2) / 2 + Math.pow(a, 4) / 24 * (5 - t + 9 * c + 4 * Math.pow(c, 2)) + Math.pow(a, 6) / 720 * (61 - 58 * t + Math.pow(t, 2) + 600 * c - 330 * e2p)));
        double y = type.getOffsetY() + type.getScaleCoefficient() * n * (a + Math.pow(a, 3) / 6 * (1 - t + c) + Math.pow(a, 5) / 120 * (5 - 18 * t + Math.pow(t, 2) + 72 * c - 58 * e2p));

        return new TMCoordinate(x, y, origin, type);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public TMCoordinateOrigin getOrigin() {
        return origin;
    }

    public TMCoordinateType getType() {
        return type;
    }
}
