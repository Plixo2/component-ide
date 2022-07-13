package de.plixo.ui.elements.visuals;


import de.plixo.ui.elements.UIElement;

public class UICircle extends UIElement {

    float radius = 0;

    @Override
    public void setDimensions(float x, float y, float width, float height) {
        super.setDimensions(x, y, width, height);
        radius = height / 2;
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {

        GUI.drawCircle(x + width / 2, y + height / 2, radius * 1.1f, getComputedHoverColor());
        GUI.drawCircle(x + width / 2, y + height / 2, radius, getColor());

        defaults(mouseX, mouseY);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}

