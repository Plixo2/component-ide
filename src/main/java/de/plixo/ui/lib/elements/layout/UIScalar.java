package de.plixo.ui.lib.elements.layout;

import de.plixo.ui.lib.elements.UIElement;
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


    @Override
    public void pack() {
        this.width = 0;
        this.height = 0;
        for (UIElement element : elements) {
            this.width = Math.max(element.x + element.width, this.width) * scale;
            this.height = Math.max(element.y + element.height, this.height) * scale;
        }
    }
}
