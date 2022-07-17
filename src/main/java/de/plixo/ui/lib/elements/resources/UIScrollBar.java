package de.plixo.ui.lib.elements.resources;


import de.plixo.general.Util;
import de.plixo.ui.lib.elements.UIReference;
import de.plixo.ui.lib.general.ColorLib;
import lombok.Setter;
import lombok.experimental.Accessors;

public class UIScrollBar extends UIReference<Float> {


    float draggableHeight = 10;
    @Setter
    @Accessors(fluent = true)
    float min = 0;

    @Setter
    @Accessors(fluent = true)
    float max = 1;

    @Override
    public void init() {
        setColor(ColorLib.getBackground(0.8f));
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX, mouseY);

        if (reference == null) {
            return;
        }

        float upperBound = y + draggableHeight;
        float lowerBound = y + height - draggableHeight;

        float rel = Util.clamp01((mouseY - upperBound) / (lowerBound - upperBound));
        if (isDragged()) {
            if (getReference() != null) {
                getReference().setValue(min + (rel * (max - min)));
            }
        } else {
            if (isHovered(mouseX, mouseY)) {
                final float v = (max - min) * MOUSE.getDXWheel() / 100f;
                reference.setValue(Util.clampFloat(reference.getValue() + v, max, min));
            }
        }


        GUI.drawRoundedRect(x, y, x + this.width, y + height, 100, getColor());


        float percent = Util.clampFloat((reference.getValue() - min) / (max - min), 1, 0);

        float cur = upperBound + (lowerBound - upperBound) * percent;
        GUI.drawRoundedRect(x, cur - draggableHeight, x + this.width, cur + draggableHeight, 100,
                ColorLib.getBackground(0.1f));
        GUI.drawRoundedRect(x +1, cur - draggableHeight + 2, x + this.width - 1, cur + draggableHeight - 2, 100,
                ColorLib.getMainColor(percent));



    }

    public void setDragerHeight(float draggableHeight) {
        this.draggableHeight = draggableHeight;
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }
}
