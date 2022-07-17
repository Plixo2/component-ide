package de.plixo.ui.lib.general;

import de.plixo.general.Color;
import de.plixo.general.Util;

public class ColorLib {

    public static Color mainColor = new Color(0xFF2B7EFF); //s: 83, B: 100, H: 217
    public static Color mainColorFade = new Color(0xFFF142E0);
    public static Color backgroundColor = new Color(0xFF1F3140);
    public static Color backgroundFadeColor = new Color(0xFF101215);


    public static int cyan() {
        return 0xFF1BBFAF;
    }

    public static int blue() {
        return 0xFF1E2D59;
    }

    public static int red() {
        return 0xFFF21D44;
    }

    public static int yellow() {
        return 0xFFF2A516;
    }

    public static int orange() {
        return 0xFFF25D27;
    }

    public static int green() {
        return 0xFF05A66B;
    }

    public static int getTextColor() {
        return -1;
    }

    public static int getBackground(float fraction) {
        return backgroundColor.mix(backgroundFadeColor, 1-fraction).getRgba();
    }


    public static int getMainColor(double fraction) {
        return mainColor.mix(mainColorFade, (float) fraction).getRgba();
    }


    public static int getDarker(int color) {
        return Color.interpolateColor(color, 0xFF000000, 0.3f);
    }

    public static int getBrighter(int color) {
        return Color.interpolateColor(color, 0xFFFFFFFF, 0.3f);
    }

    public static int getByBytes(int r, int g, int b, int a) {
        r = Util.clamp(r, 255, 0);
        g = Util.clamp(g, 255, 0);
        b = Util.clamp(b, 255, 0);
        a = Util.clamp(a, 255, 0);
        return a << 24 | r << 16 | g << 8 | b;
    }
}
