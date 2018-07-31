
package com.icegps.mapview.utils;

public class ScaleHelper {
    public static int scale(double base, float multiplier) {
        return (int)((base * multiplier));
    }

    public static double unscale(double base, float multiplier) {
        return ((base / multiplier));
    }
}