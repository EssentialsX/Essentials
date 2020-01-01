package com.earth2me.essentials.utils;

/**
 * parseFloat and parseDouble proxies that are protected against non-finite values.
 */
public class FloatUtil {
    private FloatUtil() {
    }

    public static float parseFloat(String s) throws NumberFormatException {
        float f = Float.parseFloat(s);
        if (Float.isNaN(f)) {
            throw new NumberFormatException("NaN is not valid");
        }
        if (Float.isInfinite(f)) {
            throw new NumberFormatException("Infinity is not valid");
        }
        return f;
    }

    public static double parseDouble(String s) throws NumberFormatException {
        double d = Double.parseDouble(s);
        if (Double.isNaN(d)) {
            throw new NumberFormatException("NaN is not valid");
        }
        if (Double.isInfinite(d)) {
            throw new NumberFormatException("Infinity is not valid");
        }
        return d;
    }
}