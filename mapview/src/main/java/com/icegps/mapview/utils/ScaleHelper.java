
package com.icegps.mapview.utils;

public class ScaleHelper {
    public static int scale(double base, float multiplier) {
        return (int) ((base * multiplier) + 0.5);
    }

    public static int unscale(int base, float multiplier) {
        return (int) ((base / multiplier) + 0.5);
    }
}