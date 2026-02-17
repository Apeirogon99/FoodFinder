package com.mukkebi.foodfinder.core.support.utils;

import java.util.ArrayList;
import java.util.List;

public class CoordinateUtils {
    private static final double METERS_PER_DEGREE_LAT = 111320.0;
    private static final double EARTH_RADIUS_METERS = 6371000.0;

    /**
     * 바운딩 박스 내 모든 그리드 셀의 중심 좌표를 열거합니다.
     */
    public static List<double[]> enumerateGridCells(RectangleBounds bounds, int gridSizeMeters) {
        double latStep = gridSizeMeters / METERS_PER_DEGREE_LAT;
        List<double[]> cells = new ArrayList<>();

        double startLat = Math.floor(bounds.getMinLatitude() / latStep) * latStep;
        double endLat = Math.ceil(bounds.getMaxLatitude() / latStep) * latStep;

        for (double cellLat = startLat; cellLat <= endLat; cellLat += latStep) {
            double snappedLat = Math.round(cellLat / latStep) * latStep;
            double lngStep = gridSizeMeters / (METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(snappedLat)));

            double startLng = Math.floor(bounds.getMinLongitude() / lngStep) * lngStep;
            double endLng = Math.ceil(bounds.getMaxLongitude() / lngStep) * lngStep;

            for (double cellLng = startLng; cellLng <= endLng; cellLng += lngStep) {
                double snappedLng = Math.round(cellLng / lngStep) * lngStep;
                cells.add(new double[]{snappedLat, snappedLng});
            }
        }

        return cells;
    }

    /**
     * 셀 중심 좌표에서 RectangleBounds를 생성합니다.
     */
    public static RectangleBounds cellBounds(double cellLat, double cellLng, int gridSizeMeters) {
        double halfLatStep = (gridSizeMeters / METERS_PER_DEGREE_LAT) / 2.0;
        double halfLngStep = (gridSizeMeters / (METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(cellLat)))) / 2.0;

        return new RectangleBounds(
                cellLat - halfLatStep,
                cellLat + halfLatStep,
                cellLng - halfLngStep,
                cellLng + halfLngStep
        );
    }

    /**
     * Haversine 공식으로 두 좌표 사이의 거리를 미터 단위로 계산합니다.
     */
    public static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }

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