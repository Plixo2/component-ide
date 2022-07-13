package de.plixo.ui.elements.other;


import de.plixo.ui.elements.layout.UICanvas;
import de.plixo.ui.general.MainWindow;

public class UIPopup extends UICanvas {


    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if(!isHovered(mouseX,mouseY)) {
            MainWindow.closeWindow();
        }
    }
}
