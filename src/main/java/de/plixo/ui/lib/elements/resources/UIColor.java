package de.plixo.ui.lib.elements.resources;

import de.plixo.general.Color;
import de.plixo.general.reference.ObjectReference;
import de.plixo.general.reference.Reference;
import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.general.ColorLib;
import lombok.val;

public class UIColor extends UIReference<Color> {

    @Override
    public void setDimensions(float x, float y, float width, float height) {

        assert reference.getValue() != null;
        setHoverColor(0);

        Reference<Float> red = new ObjectReference<>(reference.getValue().red() / 255f);
        Reference<Float> green = new ObjectReference<>(reference.getValue().green() / 255f);
        Reference<Float> blue = new ObjectReference<>(reference.getValue().blue() / 255f);
        Reference<Float> alpha = new ObjectReference<>(reference.getValue().alpha() / 255f);

        setOnTick(() -> {
            val color = reference.getValue();
            red.setValue(color.red() / 255f);
            green.setValue(color.green() / 255f);
            blue.setValue(color.blue() / 255f);
            alpha.setValue(color.alpha() / 255f);
        });


        float h = (height / 5);
        float padding = 3;
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(red);
            fillBar.setDraggable(true);
            fillBar.setDimensions(0, 0, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(0xFFFF2B39));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UINumber();
            number.setReference(red);
            number.setDimensions(width - h * 3, 0, h * 3, h);
            this.add(number);

            red.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color((int) (a * 255), value.green(), value.blue(), value.alpha()).rgba());
            });
        }
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(green);
            fillBar.setDraggable(true);
            fillBar.setDimensions(0, h, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(0xFF2BFF4F));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UINumber();
            number.setReference(green);
            number.setDimensions(width - h * 3, h, h * 3, h);
            this.add(number);

            green.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color(value.red(), (int) (a * 255), value.blue(), value.alpha()).rgba());
            });
        }
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(blue);
            fillBar.setDraggable(true);
            fillBar.setDimensions(0, h * 2, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(0xFF2B7EFF));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UINumber();
            number.setReference(blue);
            number.setDimensions(width - h * 3, h * 2, h * 3, h);
            this.add(number);

            blue.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color(value.red(), value.green(), (int) (a * 255), value.alpha()).rgba());
            });
        }
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(alpha);
            fillBar.setDraggable(true);
            fillBar.setDimensions(0, h * 3, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(ColorLib.getBackground(1f)));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UINumber();
            number.setReference(alpha);
            number.setDimensions(width - h * 3, h * 3, h * 3, h);
            this.add(number);

            alpha.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color(value.red(), value.green(), value.blue(), (int) (a * 255)).rgba());
            });
        }

        val stringWidth = GUI.getStringWidth("#FFFFFFFF");
        UIElement drawer = new UIElement() {
            @Override
            public void drawScreen(float mouseX, float mouseY) {
                val value = reference.getValue();
                final String hex =
                        String.format("#%02X%02X%02X%02X", value.red(), value.green(), value.blue(), value.alpha());
                val left = x + width - stringWidth - 4;
                GUI.drawString(hex, left, y + height / 2, ColorLib.getTextColor());
                GUI.drawRoundedRect(x + 1, y + 1, left - 1, y + height - 1, 300, -1);
                GUI.drawRoundedRect(x + 2, y + 2, left - 2, y + height - 2, 300, value.rgba());
            }
        };
        drawer.setDimensions(0, h * 4, width, h);
        this.add(drawer);


        super.setDimensions(x, y, width, height);
    }
}
