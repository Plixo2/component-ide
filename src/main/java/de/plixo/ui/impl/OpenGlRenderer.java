package de.plixo.ui.impl;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.ui.lib.general.UIManager;
import de.plixo.ui.lib.interfaces.IRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.Stack;

import static de.plixo.systems.RenderSystem.UI_SCALE;
import static org.lwjgl.opengl.GL11.*;


public class OpenGlRenderer implements IRenderer {

    static float yOffset = 13;
    static FontRenderer font;

    public static void setFontRenderer(FontRenderer fontRenderer) {
        OpenGlRenderer.font = fontRenderer;
    }

    @Override
    public void drawLinedRect(float left, float top, float right, float bottom, int color, float width) {
        if (color == 0) {
            return;
        }

        float temp;

        if (left < right) {
            temp = left;
            left = right;
            right = temp;
        }

        if (top < bottom) {
            temp = top;
            top = bottom;
            bottom = temp;
        }

        float wHalf = 0;

        drawLine(left + wHalf, top, right - wHalf, top, color, width);
        drawLine(left + wHalf, bottom, right - wHalf, bottom, color, width);

        drawLine(left, top + wHalf, left, bottom - wHalf, color, width);
        drawLine(right, top + wHalf, right, bottom - wHalf, color, width);


    }

    public void drawLinedRoundedRect(float left, float top, float right, float bottom, float radius,
                                     int color, float width) {
        if (color == 0) {
            return;
        }

        if (radius <= 1f) {
            drawLinedRect(left, top, right, bottom, color, width);
            return;
        }

        float var5;

        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }

        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        radius = Math.min((left - right) / 2, Math.min((top - bottom) / 2, radius));

        circleLinedSection(left - radius, top - radius, radius, color, 0, 45, width);
        circleLinedSection(left - radius, bottom + radius, radius, color, 45, 90, width);
        circleLinedSection(right + radius, top - radius, radius, color, 135, 180, width);
        circleLinedSection(right + radius, bottom + radius, radius, color, 90, 135, width);

        drawLine(left - radius, top, right + radius, top, color, width);
        drawLine(left - radius, bottom, right + radius, bottom, color, width);
        drawLine(left, top - radius, left, bottom + radius, color, width);
        drawLine(right, top - radius, right, bottom + radius, color, width);

    }

    @Override
    public void drawLine(float left, float top, float right, float bottom, int color, float width) {
        if (color == 0) {
            return;
        }

        set(color);

        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);

        glBegin(GL_LINES);
        glVertex2d(left, top);
        glVertex2d(right, bottom);
        glEnd();

        reset();
    }

    @Override
    public void drawRect(float left, float top, float right, float bottom, int color) {
        if (color == 0) {
            return;
        }

        float temp;

        if (left < right) {
            temp = left;
            left = right;
            right = temp;
        }

        if (top < bottom) {
            temp = top;
            top = bottom;
            bottom = temp;
        }

        set(color);
        glBegin(GL_QUADS);

        glVertex2d(left, bottom);
        glVertex2d(right, bottom);
        glVertex2d(right, top);
        glVertex2d(left, top);

        glEnd();
        reset();


    }

    @Override
    public void drawCircle(float x, float y, float radius, int color) {
        if (color == 0) {
            return;
        }


        set(color);
        glEnable(GL_POLYGON_SMOOTH);
        glBegin(9);
        float s = (float) (Math.PI / 90.0);
        int i = 0;
        glVertex2d(x, y);
        float theta;
        while (i <= 360) {
            theta = i * s;

            glVertex2d((x + Math.sin(theta) * radius), (y + Math.cos(theta) * radius));
            i += 9;
        }
        glEnd();
        reset();

        glDisable(GL_POLYGON_SMOOTH);
    }

    @Override
    public void drawOval(float x, float y, float width, float height, int color) {
        if (color == 0) {
            return;
        }

        set(color);
        glBegin(9);
        int i = 0;
        double theta = 0;
        glVertex2d(x, y);
        while (i <= 360) {
            theta = i * Math.PI / 90.0;
            GL11.glVertex2d(x + (width / 2 * Math.cos(theta)), (height / 2 * Math.sin(theta)));
            i += 3;
        }
        glEnd();
        reset();
    }

    @Override
    public void drawRoundedRect(float left, float top, float right, float bottom, float radius, int color) {
        if (color == 0) {
            return;
        }

        if (radius <= 1f) {
            drawRect(left, top, right, bottom, color);
            return;
        }

        float temp;

        if (left < right) {
            temp = left;
            left = right;
            right = temp;
        }

        if (top < bottom) {
            temp = top;
            top = bottom;
            bottom = temp;
        }

        radius = Math.min((left - right) / 2, Math.min((top - bottom) / 2, radius));

        set(color);

        drawCircleFast(left - radius, top - radius, radius, 0, 45);
        drawCircleFast(left - radius, bottom + radius, radius, 45, 90);
        drawCircleFast(right + radius, top - radius, radius, 135, 180);
        drawCircleFast(right + radius, bottom + radius, radius, 90, 135);

        drawRectFast(left - radius, top, right + radius, bottom);
        drawRectFast(left, top - radius, left - radius, bottom + radius);
        drawRectFast(right + radius, top - radius, right, bottom + radius);

        reset();

    }

    public void drawRectFast(float left, float top, float right, float bottom) {
        glDisable(GL_POLYGON_SMOOTH);
        glBegin(GL_QUADS);
        glVertex2d(left, bottom);
        glVertex2d(right, bottom);
        glVertex2d(right, top);
        glVertex2d(left, top);
        glEnd();
    }

    public void drawCircleFast(float x, float y, float radius, int from, int to) {

        float s = (float) (Math.PI / 90.0f);
        float yaw;
        glBegin(GL_TRIANGLE_FAN);
        glVertex2d(x, y);
        float offset = to - from;
        float rad = 0;
        while (rad <= offset) {
            yaw = (from + rad) * s;
            glVertex2d((x + Math.sin(yaw) * radius), (y + Math.cos(yaw) * radius));
            rad += 9;
        }
        glEnd();

    }

    @Override
    public void drawCircle(float x, float y, float radius, int color, int from, int to) {
        if (color == 0) {
            return;
        }

        set(color);
        glBegin(9);
        int i = from;
        glVertex2d(x, y);
        while (i <= to) {
            glVertex2d((x + Math.sin((float) i * Math.PI / 90.0) * radius),
                    (y + Math.cos(i * Math.PI / 90.0) * radius));
            i += 9;
        }
        glEnd();
        reset();
    }

    @Override
    public void circleLinedSection(float x, float y, float radius, int color, int from, int to, float width) {
        if (color == 0) {
            return;
        }

        set(color);
        glEnable(GL_LINE_SMOOTH);
        glLineWidth(width);

        glBegin(GL_LINES);
        int i = from;
        float toRadiant = (float) (Math.PI / 90);
        while (i < to) {
            glVertex2d(x + Math.sin(i * toRadiant) * radius, y + Math.cos(i * toRadiant) * radius);
            i += 9;
            glVertex2d(x + Math.sin(i * toRadiant) * radius, y + Math.cos(i * toRadiant) * radius);
        }
        glEnd();
        reset();
    }

    public static void set(int color) {
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        setColor(color);
    }

    public static void reset() {
        glEnable(GL_TEXTURE_2D);
        glColor4f(1, 1, 1, 1);
    }

    public static void setColor(int color) {
        float alpha = (float) (color >> 24 & 255) / 255.0f;
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;
        glColor4f(red, green, blue, alpha);
    }

    @Override
    public void pushMatrix() {
        glPushMatrix();
    }

    @Override
    public void popMatrix() {
        glPopMatrix();
    }

    @Override
    public void translate(float x, float y) {
        glTranslated(x, y, 0);
    }

    @Override
    public void scale(float x, float y) {
        glScaled(x, y, 1);
    }

    public Stack<float[]> scissorStack = new Stack<>();

    @Override
    public void pushScissor(float x, float y, float x2, float y2) {
        if (scissorStack.size() != 0) {
            final float[] pre = scissorStack.get(scissorStack.size() - 1);
            x = Math.max(pre[0], x);
            y = Math.max(pre[1], y);
            x2 = Math.min(pre[2], x2);
            y2 = Math.min(pre[3], y2);
        }
        scissorStack.push(new float[]{x, y, x2, y2});
        createScissorBox(x, y, x2, y2);
    }

    @Override
    public void popScissor() {
        scissorStack.pop();
        final float[] peek = scissorStack.peek();
        createScissorBox(peek[0], peek[1], peek[2], peek[3]);
    }


    private void createScissorBox(float x, float y, float x2, float y2) {
        float wDiff = x2 - x;
        float hDiff = y2 - y;
        if (wDiff < 0 || hDiff < 0) {
            return;
        }


        float bottomY = (UIManager.INSTANCE.getHeight() * UI_SCALE) - y2;
        glScissor(Math.round(x),
                Math.round(bottomY),
                Math.round(wDiff),
                Math.round(hDiff));


    }


    @Override
    public float[] getModelViewMatrix() {
        float[] mat = new float[16];
        glGetFloatv(GL_MODELVIEW_MATRIX, mat);
        return mat;
    }

    @Override
    public Vector2f toScreenSpace(float[] mat, float x, float y) {
        float nX = mat[0] * x + mat[4] * y + mat[8] * 0 + mat[12];
        float nY = mat[1] * x + mat[5] * y + mat[9] * 0 + mat[13];
        return new Vector2f(nX, nY);
    }

    static float scale = 0.5f;

    @Override
    public void drawCenteredString(String text, float x, float y, int color) {

        pushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);
        font.drawString(-getStringWidth(text), -yOffset, text, color, false);
        popMatrix();
    }

    @Override
    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        pushMatrix();
        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);
        font.drawString(-getStringWidth(text), -yOffset, text, color, true);
        popMatrix();
    }


    @Override
    public void drawString(String text, float x, float y, int color) {
        pushMatrix();

        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);
        font.drawString(0, -yOffset, text, color, false);

        popMatrix();
    }

    @Override
    public void drawStringWithShadow(String text, float x, float y, int color) {
        pushMatrix();

        GL11.glTranslated(x, y, 0);
        GL11.glScaled(scale, scale, 1);
        font.drawString(0, -yOffset, text, color, true);

        popMatrix();
    }

    @Override
    public float getStringWidth(String text) {
        try {
            return font.getWidth(text) / 2f;
        } catch (Exception e) {
            return 0;
        }
    }


    @Override
    public float getScale() {
        float[] modelViewMatrix = getModelViewMatrix();
        return (float) Math.sqrt(
                modelViewMatrix[0] * modelViewMatrix[0] + modelViewMatrix[1] * modelViewMatrix[1] +
                        modelViewMatrix[2] * modelViewMatrix[2]);
    }

    static long delta = 0;

    @Override
    public long deltaMS() {
        return delta;
    }

    @SubscribeEvent
    static void renderEvent(@NotNull Render3DEvent event) {
        delta = (long) (event.delta() * 1000);
    }


    public static void push() {
        glPushMatrix();
    }

    public static void pop() {
        glPopMatrix();
    }

}
