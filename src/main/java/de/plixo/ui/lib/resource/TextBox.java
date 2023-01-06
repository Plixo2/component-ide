package de.plixo.ui.lib.resource;

import de.plixo.general.Color;
import de.plixo.general.Util;
import de.plixo.general.reference.Reference;
import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.interfaces.IKeyboard;
import de.plixo.ui.lib.interfaces.IRenderer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TextBox {
    @Getter
    @Nullable Reference<String> reference;
    @Nullable IRenderer renderer;
    @NotNull IKeyboard keyboard;
    public float x, y, width, height;

    public TextBox(@Nullable Reference<String> reference, @Nullable IRenderer renderer,
                   @NotNull IKeyboard keyboard, float x, float y, float width, float height) {
        this.reference = reference;
        this.renderer = renderer;
        this.keyboard = keyboard;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    private String internalString = "";

    @Setter
    private int cursor1 = 0;

    @Setter
    private boolean selection = false;
    @Setter
    private int selectionWidth = 0;

    private int textColor = ColorLib.getTextColor();
    private int selectedText = Color.BLACK.rgba();
    private int selectionColor = ColorLib.getMainColor(0.4f);

    @Setter
    private int cursorDelay = 0;

    public void drawScreen(boolean isFocused) {
        if (renderer == null) {
            return;
        }
        if (reference == null) {
            return;
        }
        internalString = reference.getValue();
        if (!isFocused) {
            renderer.drawString(internalString, x, y + height / 2, textColor);
            selection = false;
            return;
        }


        cursorDelay -= UIElement.GUI.deltaMS();
        cursor1 = Util.clamp(cursor1, internalString.length(), 0);

        final String subStr = internalString.substring(0, cursor1);
        final float cursorPosition = renderer.getStringWidth(subStr);
        if (selection) {
            final int cursor3 = Util.clamp(cursor1 + selectionWidth, internalString.length(), 0);

            final int min = Math.min(cursor1, cursor3);
            final int max = Math.max(cursor1, cursor3);
            final String prefix = internalString.substring(0, min);
            final String middle = internalString.substring(0, max);
            final String end = internalString.substring(max);
            final float cursorMin = renderer.getStringWidth(prefix);
            final float cursorMax = renderer.getStringWidth(middle);

            float p = Math.abs(selectionWidth) / (float) internalString.length();
            renderer.drawRoundedRect(x + cursorMin, y, x + cursorMax, y + height, 1.5f,
                    ColorLib.getMainColor(Util.clamp01(p)));

            renderer.drawString(prefix, x, y + height / 2, textColor);
            renderer.drawString(internalString.substring(min, max), x + cursorMin, y + height / 2,
                    selectedText);
            renderer.drawString(end, x + cursorMax, y + height / 2, textColor);


            if (selectionWidth == 0) {
                selection = false;
            }
        } else {
            if (cursorDelay > 0) {
                renderer.drawRect(x + cursorPosition - 0.5f, y, x + cursorPosition + 0.5f, y + height,
                        Color.WHITE.rgba());
            } else if (cursorDelay < -500) {
                cursorDelay = 500;
            }
            renderer.drawString(internalString, x, y + height / 2, textColor);
        }
    }

    public void onChar(char typedChar) {
        if (reference == null || typedChar == 0) {
            return;
        }
        if (typedChar > 126 || typedChar < 32) {
            return;
        }
        internalString = reference.getValue();

        if (selection) {
            selection = false;
            final int cursor3 = Util.clamp(cursor1 + selectionWidth, internalString.length(), 0);
            final int min = Math.min(cursor1, cursor3);
            final int max = Math.max(cursor1, cursor3);
            final String prefix = internalString.substring(0, min);
            final String suffix = internalString.substring(max);
            final String toAppend = String.valueOf(typedChar);
            internalString = prefix + toAppend + suffix;
            cursor1 = (prefix + toAppend).length();
        } else {
            cursor1 = Util.clamp(cursor1, internalString.length(), 0);
            final String prefix = internalString.substring(0, cursor1);
            final String suffix = internalString.substring(cursor1);

            final String toAppend = String.valueOf(typedChar);

            internalString = prefix + toAppend + suffix;
            cursor1 += 1;
        }
        reference.setValue(internalString);
    }

    public static String namePattern = "[a-zA-Z\\d]";

    public void onKey(int key) {
        if (reference == null || key == 0) {
            return;
        }
        internalString = reference.getValue();

        cursorDelay = 500;

        if (key == keyboard.KEY_LSHIFT() || key == keyboard.KEY_LCONTROL()) {
            return;
        }

        if (keyboard.isKeyDown(keyboard.KEY_LSHIFT())) {
            if (key == keyboard.KEY_LEFT()) {
                selection = true;
                if (keyboard.isKeyDown(keyboard.KEY_LCONTROL())) {
                    final int skip = skip(selectionWidth - 1, -1);
                    selectionWidth += skip;
                } else {
                    selectionWidth -= 1;
                }
            } else if (key == keyboard.KEY_RIGHT()) {
                selection = true;
                selectionWidth += keyboard.isKeyDown(keyboard.KEY_LCONTROL()) ? skip(selectionWidth, 1) : 1;
            } else if (key == keyboard.KEY_HOME()) {
                selection = true;
                selectionWidth = cursor1;
                cursor1 = 0;
            } else if (key == keyboard.KEY_END()) {
                selection = true;
                selectionWidth = 10000;
            }
        } else if (keyboard.isKeyDown(keyboard.KEY_LCONTROL())) {
            if (key == keyboard.KEY_A()) {
                cursor1 = 0;
                selection = true;
                selectionWidth = internalString.length();
            }
            if (key == keyboard.KEY_LEFT()) {
                cursor1 += skip(selectionWidth - 1, -1);
                selection = false;
                selectionWidth = 0;
            } else if (key == keyboard.KEY_RIGHT()) {
                cursor1 += skip(selectionWidth, 1);
                selection = false;
                selectionWidth = 0;
            }

        } else {
            if (key == keyboard.KEY_LEFT()) {
                if (selection) {
                    cursor1 = Math.min(cursor1, cursor1 + selectionWidth);
                } else {
                    cursor1 -= 1;
                }
                selection = false;
                selectionWidth = 0;
            } else if (key == keyboard.KEY_RIGHT()) {
                if (selection) {
                    cursor1 = Math.max(cursor1, cursor1 + selectionWidth);
                } else {
                    cursor1 += 1;
                }
                selection = false;
                selectionWidth = 0;
            } else if (key == keyboard.KEY_BACK()) {
                if (selection) {
                    final int cursor3 = Util.clamp(cursor1 + selectionWidth, internalString.length(), 0);
                    final int min = Math.min(cursor1, cursor3);
                    final int max = Math.max(cursor1, cursor3);
                    final String prefix = internalString.substring(0, min);
                    final String middle = internalString.substring(max);
                    internalString = prefix + middle;
                } else if (cursor1 >= 1) {
                    cursor1 = Util.clamp(cursor1, internalString.length(), 0);
                    if (cursor1 >= 1) {
                        final String prefix = internalString.substring(0, cursor1 - 1);
                        final String suffix = internalString.substring(cursor1);
                        internalString = prefix + suffix;
                        cursor1 -= 1;
                    }
                }
                selection = false;
                selectionWidth = 0;
            } else if (key == keyboard.KEY_DELETE()) {
                if (selection) {
                    final int cursor3 = Util.clamp(cursor1 + selectionWidth, internalString.length(), 0);
                    final int min = Math.min(cursor1, cursor3);
                    final int max = Math.max(cursor1, cursor3);
                    final String prefix = internalString.substring(0, min);
                    final String middle = internalString.substring(max);
                    internalString = prefix + middle;
                } else if (cursor1 >= 0 && internalString.length() > 0) {
                    cursor1 = Util.clamp(cursor1, internalString.length(), 0);
                    final String prefix = internalString.substring(0, cursor1);
                    final String suffix = internalString.substring(cursor1 + 1);
                    internalString = prefix + suffix;
                }
                selection = false;
                selectionWidth = 0;
            } else if (key == keyboard.KEY_END()) {
                cursor1 = internalString.length();
                selection = false;
                selectionWidth = 0;
            } else if (key == keyboard.KEY_HOME()) {
                cursor1 = 0;
                selection = false;
                selectionWidth = 0;
            }
        }

        final int inter = Util.clamp(cursor1 + selectionWidth, internalString.length(), 0);
        selectionWidth = selection ? inter - cursor1 : 0;

        cursor1 = Util.clamp(cursor1, internalString.length(), 0);
        reference.setValue(internalString);
    }

    public int skip(int start, int dir) {
        int type = 0;
        int width = start;
        while (true) {
            final int position = cursor1 + width;
            if (position >= internalString.length() || position < 0) {
                break;
            }
            width += dir;
            final char charAt = internalString.charAt(position);
            if (type == 0) {
                type = String.valueOf(charAt).matches(namePattern) ? 1 : -1;
            } else if (type == 1) {
                if (!String.valueOf(charAt).matches(namePattern)) {
                    width -= dir;
                    break;
                }
            } else if (String.valueOf(charAt).matches(namePattern)) {
                width -= dir;
                break;
            }
        }
        return width - start;
    }
}