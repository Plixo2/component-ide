package de.plixo.ui.lib.elements.other;


import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.general.UIManager;

public class UIPopup extends UICanvas {

    public UIPopup() {
        setColor(0);
        setHoverColor(0);
    }
    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(!isHovered(mouseX,mouseY)) {
            UIManager.closeWindow();
        }
    }
}
