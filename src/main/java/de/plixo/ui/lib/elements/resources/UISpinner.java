package de.plixo.ui.lib.elements.resources;

import de.plixo.general.Util;
import de.plixo.general.reference.ObjectReference;
import de.plixo.general.reference.Reference;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.resource.TextBox;

public class UISpinner extends UIReference<Integer> {

    public UISpinner() {
        setColor(ColorLib.getBackground(0.6f));
    }

    @Override
    public void setDimensions(float x, float y, float width, float height) {
        UISpinnerProxy proxy = new UISpinnerProxy();
        proxy.setReference(this.reference);
        proxy.setColor(0);
        proxy.setDimensions(0, 0, width - height, height);
        add(proxy);

        UIButton up = new UIButton();
        up.setDimensions(width - height, 0, height, height / 2);
        up.setDisplayName("+");
        up.setColor(ColorLib.getMainColor(0.6f));
        up.setRoundness(getRoundness()/2);
        add(up);

        UIButton down = new UIButton();
        down.setDimensions(width - height, height / 2, height, height / 2);
        down.setDisplayName("-");
        down.setColor(ColorLib.getMainColor(0.6f));
        down.setRoundness(getRoundness()/2);
        add(down);

        up.setAction(() -> this.reference.setValue(this.reference.getValue() + 1));

        down.setAction(() -> this.reference.setValue(this.reference.getValue() - 1));

        super.setDimensions(x, y, width, height);
    }

    public static class UISpinnerProxy extends UIReference<Integer> {
        TextBox box;

        Reference<String> internalRef = new ObjectReference<>();

        @Override
        public void init() {
            enableScissor();
            setSelectionOutlineColor(ColorLib.getMainColor(0.4f));
            setOutlineWidth(2f);
        }

        float last = 0;

        @Override
        public void drawScreen(float mouseX, float mouseY) {
//        defaults(mouseX, mouseY);
            super.drawScreen(mouseX, mouseY);

            GUI.pushMatrix();
            startScissor();
            GUI.translate(x, y);
            box.drawScreen(isSelected());
            endScissor();
            GUI.popMatrix();

            float delta = this.last - mouseX;

            if (isDragged()) {
                if (Math.abs(delta) > 10) {
                    assert reference != null;
                    reference.setValue((int) (reference.getValue() - Math.signum(delta)));
                    this.last = mouseX;
                }
            } else {
                this.last = mouseX;
            }

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
                        reference.setValue(Integer.parseInt(value));
                    }
                }
            } else {
                internalRef.setValue(String.valueOf(reference.getValue()));
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
                internalRef.setValue(String.valueOf(reference.getValue()));
            }
            super.onTick();
        }


        @Override
        public void mouseReleased(float mouseX, float mouseY, int mouseButton) {
            final boolean notSelected = !isSelected();
            if (notSelected) {
                internalRef.setValue(String.valueOf(reference.getValue()));
            } else {
                final String value = internalRef.getValue();
                if (Util.isNumeric(value)) {
                    reference.setValue(Integer.parseInt(value));
                } else {
                    internalRef.setValue(String.valueOf(reference.getValue()));
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
            float textHeight = Math.min(16, height - 4);
            internalRef.setValue(String.valueOf(reference.getValue()));
            box = new TextBox(internalRef, GUI, KEYBOARD, insert, height / 2 - textHeight / 2,
                    (width) - insert * 2,
                    textHeight);


            super.setDimensions(x, y, width, height);
        }
    }
}
