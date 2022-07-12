package de.plixo.general;


import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;
import java.util.function.Function;


public class Util {


    public static int clamp(int value, int max, int min) {
        return Math.max(min, Math.min(value, max));
    }

    public static double clampDouble(double value, double max, double min) {
        return Math.max(min, Math.min(value, max));
    }

    public static float clampFloat(float value, float max, float min) {
        return Math.max(min, Math.min(value, max));
    }

    public static float clamp01(float value) {
        return clampFloat(value, 1, 0);
    }

    public static boolean isNumeric(String str) {
        return NumberUtils.isCreatable(str);
    }

    public static void print(Object obj) {
        System.out.println(obj);
    }

    public static void print(Object... obj) {
        print(Arrays.toString(obj));
    }


    public static float getDeltaAngle(float current, float target) {
        current = clampAngle(current);
        target = clampAngle(target);
        float rot = (target + 180.0F - current) % 360.0F;
        return rot + (rot > 0.0F ? -180.0F : 180.0F);
    }

    static float clampAngle(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }
        if (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }



}
