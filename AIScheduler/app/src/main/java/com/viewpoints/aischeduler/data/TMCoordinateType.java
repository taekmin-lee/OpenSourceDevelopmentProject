package com.viewpoints.aischeduler.data;

public enum TMCoordinateType {
    BESSEL1841,
    GRS80,
    OLD_BESSEL1841,
    OLD_GRS80;

    public double getSemiMajorAxis() {
        if (this == BESSEL1841 || this == OLD_BESSEL1841) {
            return 6377397.155;
        } else {
            return 6378137;
        }
    }

    public double getSemiMinorAxis() {
        return getSemiMajorAxis() * (1 - getFlattening());
    }

    public double getFlattening() {
        if (this == BESSEL1841 || this == OLD_BESSEL1841) {
            return 1 / 299.1528128;
        } else {
            return 1 / 298.257222101;
        }
    }

    public double getScaleCoefficient() {
        return 1.0;
    }

    public int getOffsetX(TMCoordinateOrigin origin) {
        if (this == BESSEL1841 || this == GRS80) {
            return 600000;
        } else {
            if (origin == TMCoordinateOrigin.JEJU) {
                return 550000;
            } else {
                return 500000;
            }
        }
    }

    public int getOffsetY() {
        return 200000;
    }
}
