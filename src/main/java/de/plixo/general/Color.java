package de.plixo.general;


import org.plixo.gsonplus.ExposeField;

public class Color {

    public static float DARKER_FADE = 0.2f;
    public static float BRIGHTER_FADE = 0.2f;

    public final static Color BLACK = new Color(0xFF000000);
    public final static Color WHITE = new Color(0xFFFFFFFF);

    @ExposeField
    int rgba;

    public Color(int rgba) {
        this.rgba = rgba;
    }

    public Color(int r, int g, int b, int a) {
        r = Util.clamp(r, 255, 0);
        g = Util.clamp(g, 255, 0);
        b = Util.clamp(b, 255, 0);
        a = Util.clamp(a, 255, 0);

        this.rgba = a << 24 |
                r << 16 |
                g << 8 |
                b;
    }

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }


    public Color darker() {
        return darker(DARKER_FADE);
    }

    public Color darker(float fade) {
        return mix(BLACK, fade);
    }

    public Color brighter() {
        return brighter(BRIGHTER_FADE);
    }

    public Color brighter(float fade) {
        return mix(WHITE, fade);
    }

    public Color copy() {
        return new Color(rgba);
    }

    public int alpha() {
        return (rgba >> 24) & 0xff;
    }

    public int red() {
        return (rgba >> 16) & 0xff;
    }

    public int green() {
        return (rgba >> 8) & 0xff;
    }

    public int blue() {
        return rgba & 0xff;
    }

    public Color mix(Color color, float fade) {
        fade = Util.clamp01(fade);
        int a1 = (rgba >> 24) & 0xff;
        int r1 = (rgba >> 16) & 0xff;
        int r2 = (color.rgba >> 16) & 0xff;
        int g1 = (rgba >> 8) & 0xff;
        int g2 = (color.rgba >> 8) & 0xff;
        int b1 = rgba & 0xff;
        int b2 = color.rgba & 0xff;

        return new Color(a1 << 24 |
                (int) ((r2 - r1) * fade + r1) << 16 |
                (int) ((g2 - g1) * fade + g1) << 8 |
                (int) ((b2 - b1) * fade + b1));
    }

    public int getRgba() {
        return rgba;
    }


    public static int interpolateColor(int color1, int color2, float fraction) {

        fraction = Util.clamp01(fraction);
        int a1 = (color1 >> 24) & 0xff;
        int a2 = (color2 >> 24) & 0xff;
        int r1 = (color1 >> 16) & 0xff;
        int r2 = (color2 >> 16) & 0xff;
        int g1 = (color1 >> 8) & 0xff;
        int g2 = (color2 >> 8) & 0xff;
        int b1 = color1 & 0xff;
        int b2 = color2 & 0xff;

        return a1 << 24 |
                (int) ((r2 - r1) * fraction + r1) << 16 |
                (int) ((g2 - g1) * fraction + g1) << 8 |
                (int) ((b2 - b1) * fraction + b1);
    }

    public static int interpolateColorAlpha(int color1, int color2, float fraction) {

        fraction = Util.clamp01(fraction);
        int a1 = (color1 >> 24) & 0xff;
        int a2 = (color2 >> 24) & 0xff;
        int r1 = (color1 >> 16) & 0xff;
        int r2 = (color2 >> 16) & 0xff;
        int g1 = (color1 >> 8) & 0xff;
        int g2 = (color2 >> 8) & 0xff;
        int b1 = color1 & 0xff;
        int b2 = color2 & 0xff;

        return (int) ((a2 - a1) * fraction + a1) << 24 |
                (int) ((r2 - r1) * fraction + r1) << 16 |
                (int) ((g2 - g1) * fraction + g1) << 8 |
                (int) ((b2 - b1) * fraction + b1);
    }

    public static int getRed(int color) {
        return (color >> 16) & 0xff;
    }

    public static int getGreen(int color) {
        return (color >> 8) & 0xff;
    }

    public static int getBlue(int color) {
        return color & 0xff;
    }
}
