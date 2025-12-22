package com.mukkebi.foodfinder.core.support.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RectangleBounds {
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public String toRectParameter() {
        return String.format("%f,%f,%f,%f",
                minLongitude, minLatitude, maxLongitude, maxLatitude);
    }
}