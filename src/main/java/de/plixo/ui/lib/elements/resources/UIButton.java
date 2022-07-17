package de.plixo.ui.lib.elements.resources;


import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.general.ColorLib;

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
