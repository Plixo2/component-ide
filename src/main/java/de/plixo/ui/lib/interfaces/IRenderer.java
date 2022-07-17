package de.plixo.ui.lib.interfaces;


import org.joml.Vector2f;

public interface IRenderer {


    void drawLinedRect(float left, float top, float right, float bottom, int color, float width);

    void drawLinedRoundedRect(float left, float top, float right, float bottom, float radius, int color, float width);

    void drawLine(float left, float top, float right, float bottom, int color, float width);

    void drawRect(float left, float top, float right, float bottom, int color);
    void drawCircle(float x, float y, float radius, int color);

    void drawOval(float x, float y, float width, float height, int color);
    void drawRoundedRect(float left, float top, float right, float bottom, float radius, int color) ;

    void drawCircle(float x, float y, float radius, int color, int from, int to);

    void circleLinedSection(float x, float y, float radius, int color, int from, int to, float width);

    void pushMatrix();
    void popMatrix();
    void translate(float x , float y);
    void scale(float x , float y);

    void pushScissor(float x, float y, float x2, float y2);
    void popScissor();

    float[] getModelViewMatrix();
    Vector2f toScreenSpace(float[] mat, float x, float y);

    void drawCenteredString(String text, float x, float y, int color);
    void drawCenteredStringWithShadow(String text, float x, float y, int color);
    void drawString(String text, float x, float y, int color);
    void drawStringWithShadow(String text, float x, float y, int color);

    float getStringWidth(String text);

    float getScale();

    long deltaMS();

}
