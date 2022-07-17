package de.plixo.ui.lib.elements.layout;


import de.plixo.general.Util;
import de.plixo.ui.lib.elements.UIElement;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

public class UIGrid extends UICanvas {

    float planeX, planeY;
    @Getter
    @Setter
    float zoom = 1;
    @Getter
    @Setter
    float minZoom = 0.2f;
    @Getter
    @Setter
    float maxZoom = 7;

    private boolean dragging = false;
    float dragX, dragY;
    float startX, startY;
    @Getter
    @Setter
    boolean shouldDrawLines = false;
    @Getter
    @Setter
    int lineSpacing = 50;

    @Getter
    @Setter
    boolean drawCross = true;

    boolean canDrag = true;

    @Getter
    @Setter
    int lineColor = 0x6F000000;


    public UIGrid() {
        setColor(0);
        enableScissor();
    }



    @Override
    public void setDimensions(float x, float y, float width, float height) {
        planeX = width / 2;
        planeY = height / 2;
        super.setDimensions(x, y, width, height);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX,mouseY);

        if (canDrag) {
            if (dragging) {
                planeX += (mouseX - dragX);
                planeY += (mouseY - dragY);
                dragX = mouseX;
                dragY = mouseY;
            }

            if (isHovered(mouseX, mouseY)) {
                mouse(mouseX, mouseY);
            }
        }


        float[] mat = GUI.getModelViewMatrix();
        Vector2f globalPosMIN = GUI.toScreenSpace(mat, x, y);
        Vector2f globalPosMAX = GUI.toScreenSpace(mat, x + width, y + height);

        GUI.pushMatrix();
        GUI.translate(planeX, planeY);
        GUI.scale(getRelative(), getRelative());

        if (isScissor()) {
            GUI.pushScissor(globalPosMIN.x, globalPosMIN.y, globalPosMAX.x, globalPosMAX.y);
        }
        if (shouldDrawLines) {
            drawLines();
        }
        Vector2f mouseToWorld = screenToWorld(mouseX, mouseY);
        drawScreenTranslated(mouseToWorld.x, mouseToWorld.y);

        if (isScissor()) {
            GUI.popScissor();
        }

        GUI.popMatrix();

    }

    public void drawScreenTranslated(float mouseX, float mouseY) {
        GUI.pushMatrix();
        startScissor();
        GUI.translate(x, y);
        for (UIElement element : elements) {
            element.drawScreen(mouseX - x, mouseY - y);
        }
        endScissor();

        GUI.popMatrix();
    }

    public void mouseClickedTranslated(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseReleasedTranslated(float mouseX, float mouseY,
                                        int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (!isHovered(mouseX, mouseY)) {
            return;
        }
        if (mouseButton == 1) {
            startDragging(mouseX, mouseY);
        }
        Vector2f mouseToWorld = screenToWorld(mouseX, mouseY);
        mouseClickedTranslated(mouseToWorld.x, mouseToWorld.y, mouseButton);
    }

    public void startDragging(float mouseX, float mouseY) {
        dragging = true;
        dragX = mouseX;
        dragY = mouseY;
        startX = mouseX;
        startY = mouseY;
    }

    public boolean hasMoved(float mouseX, float mouseY) {
        return Math.abs(mouseX - startX) < 5 && Math.abs(mouseY - startY) < 5;
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (state == 1) {
            dragging = false;
            dragX = 0;
            dragY = 0;
        }
        Vector2f mouseToWorld = screenToWorld(mouseX, mouseY);
        mouseReleasedTranslated(mouseToWorld.x, mouseToWorld.y, state);
    }


    void mouse(float mouseX, float mouseY) {
        float dir = Math.signum(MOUSE.getDXWheel());
        if (dir != 0) {
            Vector2f pre = screenToWorld(mouseX, mouseY);
            float minDiff = (zoom * (0.1f * dir));
            if (Math.abs(minDiff) < 0.01f) {
                minDiff = dir * 0.01f;
            }
            zoom += minDiff;
            zoom = Util.clampFloat(zoom, 1, 0);
            Vector2f post = screenToWorld(mouseX, mouseY);
            Vector2f diff = new Vector2f(pre.x - post.x, pre.y - post.y);
            planeX -= diff.x * getRelative();
            planeY -= diff.y * getRelative();
        }
    }

    void drawLines() {
        int i = lineSpacing;
        Vector2f top = screenToWorld(x - i, y - i);
        Vector2f bottom = screenToWorld(x + width + i, y + height + i);
        float x = top.x;
        float mX = x % i;
        float y = top.y;
        float mY = y % i;

        for (float j = top.x; j <= bottom.x; j += i) {
            float off = j - mX;
            GUI.drawLine(off, top.y, off, bottom.y + i, lineColor, 1);
        }
        for (float j = top.y; j <= bottom.y; j += i) {
            float off = j - mY;
            GUI.drawLine(top.x, off, bottom.x, off, lineColor, 1);
        }

        i *= 0.75f;
        if (drawCross) {
            final float scale = GUI.getScale();
            GUI.drawLine(-i, 0, i, 0, -1, 1 * scale);
            GUI.drawLine(0, -i, 0, i, -1, 1 * scale);
        }
    }

    public float getRelative() {
        return minZoom + (maxZoom - minZoom) * zoom;
    }

    public Vector2f screenToWorld(float x, float y) {

        x -= this.planeX;
        y -= this.planeY;

        x /= getRelative();
        y /= getRelative();


        return new Vector2f(x, y);
    }

    public Vector2f worldToScreen(float x, float y) {

        x *= getRelative();
        y *= getRelative();

        x += this.planeX;
        y += this.planeY;

        return new Vector2f(x, y);
    }


}


