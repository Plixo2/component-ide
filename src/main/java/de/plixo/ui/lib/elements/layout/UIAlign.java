package de.plixo.ui.lib.elements.layout;


import de.plixo.general.Util;
import de.plixo.ui.lib.elements.UIElement;
import lombok.Getter;
import lombok.Setter;

public class UIAlign extends UICanvas {

    private float space = 0;

    @Getter
    @Setter
    private float percent = 0;
    private float offset = 0;

    private int direction = VERTICAL;

    @Getter
    @Setter
    boolean canScroll = true;


    @Override
    public void init() {
        setColor(0);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        defaults(mouseX,mouseY);

        if (isHovered(mouseX, mouseY)) {
            float dir = Math.signum(MOUSE.getDXWheel());
            if (dir != 0) {
                final float v = getMax() - (direction == VERTICAL ? height : width);
                float size = (MOUSE.getDXWheel() * 6f) / v;

                percent = Util.clampFloat(percent - size, 1, 0);
            }
        }
        offset = 0;
        if (direction == VERTICAL) {
            if (getMax() > height && canScroll) {
                float maxDiff = getMax() - height;
                offset = maxDiff * percent;
            }
        } else if (getMax() > width && canScroll) {
            float maxDiff = getMax() - width;
            offset = maxDiff * percent;
        }

        if (direction == VERTICAL) {
            mouseY += offset;
        } else mouseX += offset;


        GUI.pushMatrix();

        startScissor();

        float add = 0;
        if (direction == VERTICAL) {
            GUI.translate(x, y - offset);
            for (UIElement element : elements) {
                element.y = add;
                element.drawScreen(mouseX - x, mouseY - y);
                add += element.height + space;
            }
        } else {
            GUI.translate(x - offset, y);
            for (UIElement element : elements) {
                element.x = add;
                element.drawScreen(mouseX - x, mouseY - y);
                add += element.width + space;
            }
        }

        endScissor();
        GUI.popMatrix();

    }


    public float getMax() {
        if (elements.size() > 0) {
            UIElement element = elements.get(elements.size() - 1);
            if (direction == VERTICAL) {
                return element.y + element.height + space;
            } else return element.x + element.width + space;
        }
        return 0;
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (direction == VERTICAL) {
            super.mouseClicked(mouseX, mouseY + offset, mouseButton);
        } else super.mouseClicked(mouseX + offset, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (direction == VERTICAL) {
            super.mouseReleased(mouseX, mouseY + offset, state);
        } else super.mouseReleased(mouseX + offset, mouseY, state);
    }

    @Override
    public void pack() {
        sort();
        float pre = this.height;
        float pre2 = this.width;
        this.height = 0;
        this.width = 0;
        for (UIElement element : elements) {
            this.width = Math.max(element.x + element.width, this.width);
            this.height = Math.max(element.y + element.height, this.height);
        }
        if (direction == VERTICAL) {
            this.height = Math.min(this.height, pre);
        } else this.width = Math.min(this.width, pre2);
    }

    public void sort() {
        float add = 0;
        if (direction == VERTICAL) {
            for (UIElement element : elements) {
                element.y = add;
                add += element.height + space;
            }
        } else {
            for (UIElement element : elements) {
                element.x = add;
                add += element.width + space;
            }
        }
    }

    public static int VERTICAL = 0;
    public static int HORIZONTAL = 1;


    public void setSpacing(float space) {
        this.space = space;
    }

    public void alignVertical() {
        this.direction = VERTICAL;
    }

    public void alignHorizontal() {
        this.direction = HORIZONTAL;
    }
}
