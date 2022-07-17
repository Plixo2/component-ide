package de.plixo.ui.lib.elements.layout;


import de.plixo.ui.lib.elements.UIElement;

public class UISpacer extends UIElement {

    public UISpacer() {
        super();
        setColor(-1);
        setHoverColor(0);
        setOutlineColor(0);
    }
    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX,mouseY);
    }
}
