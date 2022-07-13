package de.plixo.ui.elements;

/**
 *  Event interface for standard event handling in Guis
 **/
public interface IGuiEvent {

    default void drawScreen(float mouseX, float mouseY) {
    }

    default void keyTyped(char typedChar, int keyCode) {
    }

    default void mouseClicked(float mouseX, float mouseY, int mouseButton) {
    }

    default void mouseReleased(float mouseX, float mouseY, int state) {
    }

    default void init() {
    }

    default void onTick() {
    }
}
