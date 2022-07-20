package de.plixo.ui.lib.elements.resources;


import de.plixo.general.Color;
import de.plixo.general.Util;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.impl.OpenGlRenderer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.*;

public class UIFillBar extends UIReference<Float> {
    private static final DecimalFormat format = new DecimalFormat("0.00");

    static {
        format.setRoundingMode(RoundingMode.HALF_UP);
    }

    @Setter

    boolean isDraggable = false;
    @Setter
    boolean drawNumber = false;


    @Setter
    @Accessors(fluent = true)
    private @NotNull Color primaryColor = ColorLib.mainColor;

    @Setter
    @Accessors(fluent = true)
    private @NotNull Color fadeColor = ColorLib.mainColorFade;


    @Setter
    @Accessors(fluent = true)
    private boolean fade = true;

    public UIFillBar() {
        this.setColor(ColorLib.getBackground(0.6f));
    }


    @Getter
    @Setter
    float min = 0, max = 1;

    @Override
    public void drawScreen(float mouseX, float mouseY) {

        defaults(mouseX, mouseY);
        float size = 2;
        float verticalSpace = 2;
        float upperBound = x + size;
        float lowerBound = x + width - size;

        float rel = Util.clampFloat((mouseX - upperBound) / (lowerBound - upperBound), 1, 0);
        final float value = reference.getValue();
        if (isDragged() && isDraggable) {
            val val = min + rel * (max - min);
            reference.setValue(val);
        }
        float percent = Util.clampFloat((value - min) / (max - min), 1, 0);
        float diff = lowerBound - upperBound;
        int color = Color.interpolateColorAlpha(getColor(),
                ColorLib.getDarker(getColor()), getHoverProgress() / 100f);

        GUI.drawRoundedRect(upperBound - 2, y + verticalSpace - 2, lowerBound + 2,
                y + height - verticalSpace + 2, 50, color);
        if (fade) {
            int color2 = primaryColor.mix(fadeColor, Util.clamp01(percent + getHoverProgress() / 200f)).rgba();
            final int leftCol = color2;
            final int rightCol = primaryColor.rgba();
            OpenGlRenderer.set(-1);
            glBegin(GL_QUADS);

            var left = upperBound;
            var top = y + verticalSpace;
            var right = upperBound + diff * percent;
            var bottom = y + height - verticalSpace;

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
            OpenGlRenderer.setColor(leftCol);
            glVertex2d(left, bottom);
            OpenGlRenderer.setColor(rightCol);
            glVertex2d(right, bottom);
            glVertex2d(right, top);
            OpenGlRenderer.setColor(leftCol);
            glVertex2d(left, top);

            glEnd();
            OpenGlRenderer.reset();
        } else {
            var left = upperBound;
            var top = y + verticalSpace;
            var right = upperBound + diff * percent;
            var bottom = y + height - verticalSpace;
            GUI.drawRoundedRect(left, top, right, bottom, 50, primaryColor.mix(fadeColor,percent).rgba());
        }




        if (drawNumber) {
            String text = format.format(value);
            if (max == 100 && min == 0) {
                text = Math.round(value) + "%";
            }
            float min = upperBound + 3;
            GUI.drawString(text, min, y + height / 2, ColorLib.getTextColor());
        }
    }


}
