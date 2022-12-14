package de.plixo.ui.lib.elements;


import de.plixo.general.Color;
import de.plixo.general.Util;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.interfaces.IKeyboard;
import de.plixo.ui.lib.interfaces.IMouse;
import de.plixo.ui.lib.interfaces.IRenderer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class UIElement implements IGuiEvent {

    public static IRenderer GUI;
    public static IKeyboard KEYBOARD;
    public static IMouse MOUSE;

    public static float HOVER_SPEED = 0.7f;

    public static float DEFAULT_ROUNDNESS = 3;

    public static int DEFAULT_COLOR = ColorLib.getBackground(0.8f);
    public static int DEFAULT_OUTLINE_COLOR = 0x55000000;
    public static int DEFAULT_HOVER_COLOR = 0x23000000;
    public static int DEFAULT_SELECTION_COLOR = 0;


    public float height, width;
    public float x, y;

    @Getter
    float hoverProgress = 0;
    @Getter
    @Setter
    @Nullable Runnable onTick = null;

    @Getter
    @Setter
    @Nullable Runnable action = null;
    @Getter
    @Setter
    @Nullable Runnable option = null;
    @Getter
    boolean dragged = false;

    @Getter
    boolean selected = false;

    @Getter
    @Setter
    int selectionOutlineColor = 0;

    int alignment = 0;

    @Getter
    @Setter
    float roundness = 0;

    @Getter
    @Setter
    int color = 0;

    @Getter
    @Setter
    float outlineWidth = 0;

    @Getter
    @Setter
    int outlineColor = 0;

    @Getter
    @Setter
    int hoverColor = 0;
    @Getter
    @Setter
    @Nullable String displayName = null;
    @Getter
    @Setter
    @Nullable String hoverName = null;
    long lastMs = 0;

    @Getter
    @Setter
    boolean textShadow = true;

    @Getter
    @Setter
    int textColor = ColorLib.getTextColor();

    public UIElement() {
        super();
        setRoundness(DEFAULT_ROUNDNESS);
        setColor(DEFAULT_COLOR);
        setOutlineColor(DEFAULT_OUTLINE_COLOR);
        setHoverColor(DEFAULT_HOVER_COLOR);
        setSelectionOutlineColor(DEFAULT_SELECTION_COLOR);
        init();
    }

    public abstract void drawScreen(float mouseX, float mouseY);

    public void onSelect() {

    }

    public void onSelectionLost() {

    }


    protected void defaults(float mouseX, float mouseY) {
        defaultBackground();
        defaultOutline();
        defaultHover();
        defaultUpdate(mouseX, mouseY);
    }

    protected void defaultUpdate(float mouseX, float mouseY) {
        updateHoverProgress(mouseX, mouseY);
        drawName(mouseX, mouseY);
        if (isSelected()) drawOutline(selectionOutlineColor);
    }

    protected void defaultHover() {
        final int computedHoverColor = getComputedHoverColor();
        drawDefault(computedHoverColor);
    }

    protected void defaultBackground() {
        drawDefault(getColor());
    }

    protected void defaultOutline() {
        drawOutline(getOutlineColor());
    }

    private void drawName(float mouseX, float mouseY) {
        if (hoverName != null) {
            if (isHovered(mouseX, mouseY)) {
                drawName(hoverName);
                return;
            }
        }
        if (displayName != null) {
            drawName(displayName);
        }
    }

    private void drawName(@NotNull String name) {
        drawName(name, alignment);
    }

    public void onTick() {
        if (onTick != null) {
            onTick.run();
        }
    }

    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        setSelected(this.isHovered(mouseX, mouseY));
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 0) {
                dragged = true;
                if (action != null) {
                    action.run();
                }
            } else if (mouseButton == 1) {
                if (option != null) {
                    option.run();
                }
            }
        }
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        dragged = false;
    }

    public void updateHoverProgress(float mouseX, float mouseY) {
        long delta = System.currentTimeMillis() - lastMs;
        if (!isHovered(mouseX, mouseY)) {
            delta = -delta;
        }
        delta *= HOVER_SPEED;

        hoverProgress = Util.clampFloat(hoverProgress + delta, 100, 0);
        lastMs = System.currentTimeMillis();
    }

    public void setDimensions(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    public boolean isHovered(float mouseX, float mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    protected int getComputedHoverColor() {
        return Color.interpolateColorAlpha(0, hoverColor, hoverProgress / 100f);
    }

    public void alignTextLeft() {
        alignment = -1;
    }

    public void alignTextMiddle() {
        alignment = 0;
    }

    public void alignTextRight() {
        alignment = 1;
    }


    private void drawName(@NotNull String name, int alignment) {
        if (textShadow) {
            if (alignment == -1) {
                GUI.drawStringWithShadow(name, x + 3, y + height / 2, textColor);
            } else if (alignment == 1) {
                GUI.drawStringWithShadow(name, x + width - (GUI.getStringWidth(name) + 3), y + height / 2, textColor);
            } else {
                GUI.drawCenteredStringWithShadow(name, x + width / 2, y + height / 2, textColor);
            }
        } else {
            if (alignment == -1) {
                GUI.drawString(name, x + 3, y + height / 2, textColor);
            } else if (alignment == 1) {
                GUI.drawString(name, x + width - (GUI.getStringWidth(name) + 3), y + height / 2, textColor);
            } else {
                GUI.drawCenteredString(name, x + width / 2, y + height / 2, textColor);
            }
        }

    }


    public void drawDefault(int color) {
        GUI.drawRoundedRect(x, y, x + width, y + height, roundness, color);
    }


    public void drawOutline(int color) {
        if (outlineWidth > 0.01) {
            GUI.drawLinedRoundedRect(x, y, x + width, y + height, roundness, color, outlineWidth);
        }
    }

    public void scaleDimensions(UIElement element) {
        scaleDimensions(element.width, element.height);
    }

    public void scaleDimensions(float width, float height) {
        setDimensions(this.x, this.y, width, height);
    }

    public void translateDimensions(float x, float y) {
        setDimensions(x, y, this.width, this.height);
    }

    public void copyDimensions(@NotNull UIElement element) {
        setDimensions(element.x, element.y, element.width, element.height);
    }

    public float aspect() {
        return width / height;
    }


    public void enableTextShadow() {
        textShadow = true;
    }

    public void disableTextShadow() {
        textShadow = false;
    }

    protected void setSelected(boolean b) {
        if (b && !selected) {
            onSelect();
        } else if (!b && selected) {
            onSelectionLost();
        }
        this.selected = b;
    }

    public void addTo(@NotNull UICanvas canvas) {
        canvas.add(this);
    }
}
