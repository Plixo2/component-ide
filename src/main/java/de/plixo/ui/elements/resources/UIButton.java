package de.plixo.ui.elements.resources;


import de.plixo.ui.elements.UIElement;
import de.plixo.ui.general.ColorLib;

public class UIButton extends UIElement {
    @Override
    public void init() {
        this.setColor(ColorLib.getMainColor(0));
        this.setOutlineWidth(1);
        this.setOutlineColor(0x77000000);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX,mouseY);
    }

}
