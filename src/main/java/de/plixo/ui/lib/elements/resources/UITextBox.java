package de.plixo.ui.lib.elements.resources;

import de.plixo.general.reference.ObjectReference;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.resource.TextBox;

public class UITextBox extends UIReference<String> {

    TextBox box;
    private boolean onLoss = false;

    @Override
    public void init() {
        enableScissor();
        setSelectionOutlineColor(ColorLib.getMainColor(0.4f));
        setOutlineWidth(2f);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX, mouseY);

        GUI.pushMatrix();
        startScissor();
        GUI.translate(x, y);

        if (!isSelected() && onLoss) {
            assert box.getReference() != null;
            box.getReference().setValue(reference.getValue());
        }
        box.drawScreen(isSelected());
        endScissor();
        GUI.popMatrix();
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (isSelected()) {
            box.onChar(typedChar);
            box.onKey(keyCode);
            if (keyCode == KEYBOARD.KEY_ENTER() || keyCode == KEYBOARD.KEY_ESCAPE()) {
                setSelected(false);
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onSelect() {
        box.setCursor1(1000);
        box.setCursorDelay(500);
    }


    @Override
    public void onSelectionLost() {
        assert box.getReference() != null;
        this.reference.setValue(box.getReference().getValue());
    }

    @Override
    public void setDimensions(float x, float y, float width, float height) {
        assert reference != null;
        float insert = 5;
        float textHeight = Math.min(16, height - 4);
        if (onLoss) {
            box = new TextBox(new ObjectReference<>(reference.getValue()), GUI, KEYBOARD, insert,
                    height / 2 - textHeight / 2, width - insert * 2, textHeight);
        } else {
            box = new TextBox(reference, GUI, KEYBOARD, insert, height / 2 - textHeight / 2,
                    width - insert * 2, textHeight);
        }
        super.setDimensions(x, y, width, height);
    }


    public void writeBackOnFocusLost() {
        onLoss = true;
    }
}
