package de.plixo.ui.lib.elements.visuals;


import de.plixo.ui.lib.elements.UIElement;

public class UIEmpty extends UIElement {


    public UIEmpty() {
        super();
        this.setColor(0);
    }
    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX,mouseY);
    }

}
