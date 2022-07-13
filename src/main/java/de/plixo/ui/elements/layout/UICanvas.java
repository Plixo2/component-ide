package de.plixo.ui.elements.layout;


import de.plixo.ui.elements.UIElement;
import de.plixo.ui.general.ColorLib;
import lombok.Getter;
import org.joml.Vector2f;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * for other {@code UIElement} inside a Element...
 * great for backgrounds and easy drawing, input and layout handling
 **/
public class UICanvas extends UIElement {

    @Getter
    //for removing while looping
    public CopyOnWriteArrayList<UIElement> elements = new CopyOnWriteArrayList<>();

    public boolean scissor = false;



    public UICanvas() {
        init();
    }

    @Override
    public void init() {
        this.setColor(0);
    }

    //add a elements
    public void add(UIElement element) {
        elements.add(element);
    }

    //remove a element
    public void remove(UIElement element) {
        elements.remove(element);
    }

    //clears the mod
    public void clear() {
        elements.clear();
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {

        defaults(mouseX, mouseY);

        GUI.pushMatrix();
        startScissor();
        GUI.translate(x, y);
        for (UIElement element : elements) {
            element.drawScreen(mouseX - x, mouseY - y);
        }
        endScissor();

        GUI.popMatrix();
    }


    //checks if canvas has UIElements
    public boolean hasValues() {
        return elements.size() > 0;
    }

    //inherited input
    @Override
    public void keyTyped(char typedChar, int keyCode) {
        for (UIElement element : elements) {
            element.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    protected void startScissor() {
        if (scissor) {
            final float[] mat = GUI.getModelViewMatrix();
            final Vector2f globalPosMIN = GUI.toScreenSpace(mat, x, y);
            final Vector2f globalPosMAX = GUI.toScreenSpace(mat, x + width, y + height);
            GUI.pushScissor(globalPosMIN.x, globalPosMIN.y, globalPosMAX.x, globalPosMAX.y);
        }
    }

    protected void endScissor() {
        if (scissor) {
            GUI.popScissor();
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {

        for (UIElement element : elements) {
            element.mouseClicked(mouseX - x, mouseY - y, mouseButton);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        for (UIElement element : elements) {
            element.mouseReleased(mouseX - x, mouseY - y, state);
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    //inherited tick
    @Override
    public void onTick() {
        for (UIElement element : elements) {
            element.onTick();
        }
        super.onTick();
    }

    public void enableScissor() {
        this.scissor = true;
    }

    public void disableScissor() {
        this.scissor = false;
    }

    public boolean isScissor() {
        return scissor;
    }

    public void pack() {
        this.width = 0;
        this.height = 0;
        for (UIElement element : elements) {
            this.width = Math.max(element.x + element.width, this.width);
            this.height = Math.max(element.y + element.height, this.height);
        }
    }


}
