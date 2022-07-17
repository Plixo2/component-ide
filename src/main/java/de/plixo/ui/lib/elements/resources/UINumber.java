package de.plixo.ui.lib.elements.resources;

import de.plixo.general.Util;
import de.plixo.general.reference.ObjectReference;
import de.plixo.general.reference.Reference;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.resource.TextBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class UINumber extends UIReference<Float> {

    TextBox box;

    Reference<String> internalRef = new ObjectReference<>();

    static NumberFormat format = new DecimalFormat("0.00");

    @Override
    public void init() {
        enableScissor();
        setSelectionOutlineColor(ColorLib.getMainColor(0.4f));
        setOutlineWidth(2f);
    }

    float last = 0;
    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX, mouseY);

        GUI.pushMatrix();
        startScissor();
        GUI.translate(x, y);
        box.drawScreen(isSelected());
        endScissor();
        GUI.popMatrix();

        if(isDragged()) {
            float delta = this.last - mouseX;
            assert reference != null;
            reference.setValue(reference.getValue() + delta * 0.1f);
        }
        this.last = mouseX;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (isSelected()) {
            box.onChar(typedChar);
            box.onKey(keyCode);
            if (keyCode == KEYBOARD.KEY_ENTER()) {
                setSelected(false);

                final String value = internalRef.getValue();
                if (Util.isNumeric(value)) {
                    reference.setValue(Float.parseFloat(value));
                }
            }
        } else {
            internalRef.setValue(format.format(reference.getValue()));
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.setSelected(false);
    }

    @Override
    public void onTick() {
        if (!isSelected()) {
            internalRef.setValue(format.format(reference.getValue()));
        }
        super.onTick();
    }


    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouseButton) {
        final boolean notSelected = !isSelected();
        if (notSelected) {
            internalRef.setValue(format.format(reference.getValue()));
        } else {
            final String value = internalRef.getValue();
            if (Util.isNumeric(value)) {
                reference.setValue(Float.parseFloat(value));
            } else {
                internalRef.setValue(format.format(reference.getValue()));
            }
        }
        super.mouseReleased(mouseX, mouseY, mouseButton);
        this.setSelected(this.isHovered(mouseX, mouseY));
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
        internalRef.setValue(format.format(reference.getValue()));
        box = new TextBox(internalRef, GUI, KEYBOARD, insert, height / 2 - textHeight / 2, width - insert * 2,
                textHeight);
        super.setDimensions(x, y, width, height);
    }
}
