package com.mukkebi.foodfinder.core.support.utils;

public class CoordinateUtils {
    private static final double METERS_PER_DEGREE_LAT = 111320.0;

    /**
     * 중심점과 반경을 기준으로 사각형 경계를 계산합니다
     */
    public static RectangleBounds calculateRectangleBounds(double centerLat, double centerLon, int radiusMeters) {
        double latDelta = radiusMeters / METERS_PER_DEGREE_LAT;
        double lonDelta = radiusMeters / (METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(centerLat)));

        return new RectangleBounds(
                centerLat - latDelta,
                centerLat + latDelta,
                centerLon - lonDelta,
                centerLon + lonDelta
        );
    }

    /**
     * 사각형을 4개의 작은 사각형으로 분할합니다
     */
    public static RectangleBounds[] subdivideRectangle(RectangleBounds bounds) {
        double midLat = (bounds.getMinLatitude() + bounds.getMaxLatitude()) / 2;
        double midLon = (bounds.getMinLongitude() + bounds.getMaxLongitude()) / 2;

        return new RectangleBounds[] {
                new RectangleBounds(bounds.getMinLatitude(), midLat, bounds.getMinLongitude(), midLon),
                new RectangleBounds(bounds.getMinLatitude(), midLat, midLon, bounds.getMaxLongitude()),
                new RectangleBounds(midLat, bounds.getMaxLatitude(), bounds.getMinLongitude(), midLon),
                new RectangleBounds(midLat, bounds.getMaxLatitude(), midLon, bounds.getMaxLongitude())
        };
    }
}