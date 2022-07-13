package de.plixo.ui.elements.visuals;


import de.plixo.ui.elements.UIElement;

public class UILabel extends UIElement {


    public UILabel() {
        super();
        this.setColor(0);
    }
    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX,mouseY);
    }

}
