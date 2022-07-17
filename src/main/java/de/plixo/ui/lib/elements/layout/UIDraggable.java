package de.plixo.ui.lib.elements.layout;


import lombok.Setter;

public class UIDraggable extends UICanvas {

    private float offsetX = 0;
    private float offsetY = 0;

    @Setter
    int gridLock = 1;

    /**
     * for calculation the offset
     */
    @Override
    public void drawScreen(float mouseX, float mouseY) {
        super.drawScreen(mouseX, mouseY);
        if (isDragged()) {
            x = (mouseX + offsetX);
            y = (mouseY + offsetY);

            x = (Math.round((x) / gridLock)) * gridLock;
            y = (Math.round((y) / gridLock)) * gridLock;
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if(isHovered(mouseX , mouseY) && mouseButton == 0) {
            offsetX = x-mouseX;
            offsetY = y-mouseY;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        super.mouseReleased(mouseX,mouseY,state);
    }
}
