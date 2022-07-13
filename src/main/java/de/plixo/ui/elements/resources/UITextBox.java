package de.plixo.ui.elements.resources;

import de.plixo.ui.elements.UIReference;
import de.plixo.ui.resource.TextBox;

public class UITextBox extends UIReference<String> {

    TextBox box;

    @Override
    public void init() {
        enableScissor();
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX, mouseY);

        GUI.pushMatrix();
        startScissor();
        GUI.translate(x, y);
        box.drawScreen(isSelected());
        endScissor();
        GUI.popMatrix();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (isSelected()) {
                box.onChar(typedChar);
                box.onKey(keyCode);
            if (keyCode == KEYBOARD.KEY_ENTER()) {
                setSelected(false);
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        final boolean notSelected = !isSelected();
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (isSelected() && notSelected) {
            box.setCursor1(1000);
            box.setCursorDelay(500);
        }
    }


    @Override
    public void setDimensions(float x, float y, float width, float height) {
        assert reference != null;
        float insert = 5;
        float textHeight = 16;
        box = new TextBox(reference, GUI, KEYBOARD, insert, height / 2 - textHeight / 2, width - insert * 2,
                textHeight);
        super.setDimensions(x, y, width, height);
    }
}
