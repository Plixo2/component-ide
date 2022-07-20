package de.plixo.ui.lib.elements.layout;

import de.plixo.ui.lib.elements.UIReference;

public class UIInvisibleRef extends UIReference<Boolean> {


    @Override
    public void drawScreen(float mouseX, float mouseY) {
        if (reference.getValue())
            super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (reference.getValue())
            super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (reference.getValue())
            super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (reference.getValue())
            super.keyTyped(typedChar, keyCode);
    }


    public void enable() {
        this.reference.setValue(true);
    }

    public void disable() {
        this.reference.setValue(false);
    }

    public void setEnabled(boolean state) {
        this.reference.setValue(state);
    }

    public void toggle() {
        this.reference.setValue(!this.reference.getValue());
    }
}
