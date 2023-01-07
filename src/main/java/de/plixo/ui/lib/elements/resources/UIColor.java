package de.plixo.ui.lib.elements.resources;

import de.plixo.general.Color;
import de.plixo.general.reference.InterfaceReference;
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
            red.setValue((float)color.red());
            green.setValue((float)color.green());
            blue.setValue((float)color.blue());
            alpha.setValue((float)color.alpha());
        });


        float h = (height / 5);
        float padding = 3;
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(red);
            fillBar.setDraggable(true);
            fillBar.setMin(0);
            fillBar.setMax(255);
            fillBar.setDimensions(0, 0, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(0xFFFF2B39));
            fillBar.fadeColor(new Color(0xFFFF2B39));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UISpinner();
            number.setScrollSpeed(6);
            number.setReference(new InterfaceReference<>(ref -> red.setValue((float)(int)ref), () -> (int)(float)red.getValue()));
            number.setDimensions(width - h * 3, 0, h * 3, h);
            this.add(number);

            red.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color((int) (float)(a), value.green(), value.blue(), value.alpha()).rgba());
            });
        }
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(green);
            fillBar.setDraggable(true);
            fillBar.setMin(0);
            fillBar.setMax(255);
            fillBar.setDimensions(0, h, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(0xFF2BFF4F));
            fillBar.fadeColor(new Color(0xFF2BFF4F));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UISpinner();
            number.setScrollSpeed(6);
            number.setReference(new InterfaceReference<>(ref -> green.setValue((float)(int)ref), () -> (int)(float)green.getValue()));
            number.setDimensions(width - h * 3, h, h * 3, h);
            this.add(number);

            green.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color(value.red(), (int) (float)(a), value.blue(), value.alpha()).rgba());
            });
        }
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(blue);
            fillBar.setDraggable(true);
            fillBar.setMin(0);
            fillBar.setMax(255);
            fillBar.setDimensions(0, h * 2, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(0xFF2B7EFF));
            fillBar.fadeColor(new Color(0xFF2B7EFF));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UISpinner();
            number.setScrollSpeed(6);
            number.setReference(new InterfaceReference<>(ref -> blue.setValue((float)(int)ref), () -> (int)(float)blue.getValue()));
            number.setDimensions(width - h * 3, h * 2, h * 3, h);
            this.add(number);

            blue.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color(value.red(), value.green(), (int) (float)(a), value.alpha()).rgba());
            });
        }
        {
            val fillBar = new UIFillBar();
            fillBar.setReference(alpha);
            fillBar.setDraggable(true);
            fillBar.setMin(0);
            fillBar.setMax(255);
            fillBar.setDimensions(0, h * 3, width - h * 3 - padding, h);
            fillBar.primaryColor(new Color(0xFFBBCCDD));
            fillBar.fadeColor(new Color(0xFFBBCCDD));
            fillBar.fade(false);
            this.add(fillBar);

            val number = new UISpinner();
            number.setScrollSpeed(6);
            number.setReference(new InterfaceReference<>(ref -> alpha.setValue((float)(int)ref), () -> (int)(float)alpha.getValue()));
            number.setDimensions(width - h * 3, h * 3, h * 3, h);
            this.add(number);

            alpha.addObserver(a -> {
                val value = reference.getValue();
                value.overwrite(new Color(value.red(), value.green(), value.blue(), (int) (float)(a)).rgba());
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
