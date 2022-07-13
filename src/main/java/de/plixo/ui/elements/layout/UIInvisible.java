package de.plixo.ui.elements.layout;

public class UIInvisible extends UICanvas {

    boolean enabled = true;

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        if (enabled) super.drawScreen(mouseX, mouseY);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (enabled) super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (enabled) super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (enabled) super.keyTyped(typedChar, keyCode);
    }


    public void enable() {
        this.enabled = true;
    }

    public void disable() {
        this.enabled = false;
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }
}
