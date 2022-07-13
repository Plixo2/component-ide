package de.plixo.ui.elements.layout;

import lombok.Getter;
import lombok.Setter;

public class UIScalar extends UICanvas {

    @Getter
    @Setter
    float scale = 1;

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        GUI.pushMatrix();
        GUI.scale(scale, scale);
        super.drawScreen(mouseX / scale, mouseY / scale);
        GUI.popMatrix();
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        super.mouseReleased(mouseX / scale, mouseY / scale, state);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX / scale, mouseY / scale, mouseButton);
    }

    public void scaleSize() {
        width = getScaledWidth();
        height = getScaledHeight();
    }

    public float getScaledWidth() {
        return width / scale;
    }

    public float getScaledHeight() {
        return height / scale;
    }

}
