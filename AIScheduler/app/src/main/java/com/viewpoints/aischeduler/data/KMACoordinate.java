package com.viewpoints.aischeduler.data;

public class KMACoordinate {
    protected final int x, y;

    public KMACoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static KMACoordinate fromWGS84(WGS84Coordinate coordinate) {
        int grid = 5;
        double xo = 210 / grid;
        double yo = 675 / grid;

        double re = 6371.00877 / grid;
        double slat1 = Math.toRadians(30);
        double slat2 = Math.toRadians(60);
        double olon = Math.toRadians(126);
        double olat = Math.toRadians(38);

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + Math.toRadians(coordinate.getLatitude()) * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = Math.toRadians(coordinate.getLongitude()) - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int x = (int) ((float) (ra * Math.sin(theta)) + xo + 1.5);
        int y = (int) ((float) (ro - ra * Math.cos(theta)) + yo + 1.5);

        return new KMACoordinate(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
