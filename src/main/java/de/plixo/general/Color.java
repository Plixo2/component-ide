package de.plixo.general;


import lombok.Getter;
import lombok.experimental.Accessors;
import org.plixo.gsonplus.ExposeField;

import static org.lwjgl.opengl.GL11.glColor4f;

public class Color {
    public static final Color YELLOW = new Color(0xFFFFFF00);
    public static final Color ORANGE = new Color(0xFFFF8800);
    public static final Color PURPLE = new Color(0xFFA832A6);
    public final static Color BLACK = new Color(0xFF000000);
    public final static Color WHITE = new Color(0xFFFFFFFF);
    public final static Color RED = new Color(0xFFFF0000);
    public final static Color GREEN = new Color(0xFF00FF00);
    public final static Color BLUE = new Color(0xFF0000FF);
    public final static Color TRANSPARENT = new Color(0);

    public static float DARKER_FADE = 0.2f;
    public static float BRIGHTER_FADE = 0.2f;
    @ExposeField
    @Getter
    @Accessors(fluent = true)
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


    public void bindGl() {
        float alpha = (float) (rgba >> 24 & 255) / 255.0f;
        float red = (float) (rgba >> 16 & 255) / 255.0f;
        float green = (float) (rgba >> 8 & 255) / 255.0f;
        float blue = (float) (rgba & 255) / 255.0f;
        glColor4f(red, green, blue, alpha);
    }


    public float[] toArray() {
        return new float[]{red() / 255f, green() / 255f, blue() / 255f, alpha() / 255f};
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

    public void overwrite(int rgba) {
        this.rgba = rgba;
    }

    @Override
    public String toString() {
        return String.valueOf(rgba);
    }
}
