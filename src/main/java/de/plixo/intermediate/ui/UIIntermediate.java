package de.plixo.intermediate.ui;

import de.plixo.general.reference.ExposedReference;
import de.plixo.general.reference.ObjectReference;
import de.plixo.ui.lib.general.Icon;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.general.ColorLib;
import de.plixo.ui.lib.interfaces.IRenderer;
import de.plixo.ui.lib.resource.TextBox;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class UIIntermediate extends UICanvas {

    float lastKnownMouseX = 0;
    float lastKnownMouseY = 0;
    @NotNull Consumer<UII> drawCall;
    @NotNull UII uii;
    @NotNull Icon subMenuIcon;

    public UIIntermediate(@NotNull Consumer<UII> drawCall) {
        this.drawCall = drawCall;
        this.uii = new UII(GUI);
        subMenuIcon = new Icon("submenu");
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {


        lastKnownMouseX = mouseX;
        lastKnownMouseY = mouseY;
        uii.preClear();

        uii.pressedLeft = MOUSE.mouseDown(0);
        uii.pressedRight = MOUSE.mouseDown(1);

        uii.control = KEYBOARD.isCtrlKeyDown();
        uii.shift = KEYBOARD.isShiftKeyDown();
        uii.alt = KEYBOARD.isAltKeyDown();

        drawCall.accept(uii);

        uii.lastKey = -1;
        uii.leftClick = false;
        uii.rightClick = false;


        uii.hoveredSet.clear();

        if (uii.contextMenu != null) {
            drawPopUp(uii.contextMenu.posX + 1, uii.contextMenu.posY + 1, uii.contextMenu.topMenu, 0);
        } else {
            for (UII.Button button : uii.buttons) {
                if (button.rect.isInside(mouseX, mouseY)) {
                    uii.hoveredSet.add(button.name);
                }
            }
        }

        uii.postClear();


        super.drawScreen(mouseX, mouseY);
    }

    private void drawPopUp(float x, float y, ContextMenu.Menu menu, float depth) {
        float yProgress = y;
        float height = 15;
        float width = 120;

        GUI.drawRoundedRect(x - 3, y - 3, x + width + 3, y + menu.options.size() * height + 3, 3, ColorLib.getBackground(0.7f));
        for (var entry : menu.options) {
            float x2 = x + width;
            float y2 = yProgress + height;

            boolean inside = lastKnownMouseX >= x && lastKnownMouseY >= yProgress && lastKnownMouseX < x2 && lastKnownMouseY < y2;
            if (inside) {
                menu.selected = entry;
            }

            GUI.drawRoundedRect(x, yProgress, x2, y2, 3, menu.selected == entry ? ColorLib.getMainColor(depth) : 0);
            if (entry instanceof ContextMenu.Option option) {
                option.lastHovered = inside;
                if (option.icon != null) {
                    option.icon.draw(x, yProgress, height);
                    GUI.drawString(option.text, x + height + 10, yProgress + height / 2, ColorLib.getTextColor());
                } else {
                    GUI.drawString(option.text, x + 10, yProgress + height / 2, ColorLib.getTextColor());
                }
            } else if (entry instanceof ContextMenu.Menu subMenu) {
                subMenu.lastHovered = inside;
                GUI.drawString(subMenu.name, x + 10, yProgress + height / 2, ColorLib.getTextColor());
                subMenuIcon.draw(x + width - height, yProgress, height);
                if (menu.selected == entry) {
                    drawPopUp(x + width + 6, yProgress, subMenu, depth + 0.3f);
                }
            }
            yProgress += height;
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        lastKnownMouseX = mouseX;
        lastKnownMouseY = mouseY;


        if (mouseButton == 0) {
            uii.leftClick = true;
        } else if (mouseButton == 1) {
            uii.rightClick = true;
        }


        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouseButton) {
        lastKnownMouseX = mouseX;
        lastKnownMouseY = mouseY;
        uii.clickedSet.clear();

        if (uii.contextMenu != null) {
            ContextMenu.Menu latest = uii.contextMenu.topMenu;
            while (latest != null) {
                if (latest.selected instanceof ContextMenu.Option option) {
                    if (option.lastHovered) {
                        option.option.run();
                    }
                    break;
                } else if (latest.selected instanceof ContextMenu.Menu menu) {
                    if (menu.lastHovered) {
                        return;
                    }
                    latest = menu;
                } else {
                    latest = null;
                }
            }
            uii.contextMenu = null;
            return;
        }


        for (UII.Button button : uii.buttons) {
            if (button.rect.isInside(mouseX, mouseY)) {
                uii.clickedSet.add(button.name);
            }
        }

        uii.textBoxes.forEach((name, textbox) -> {
            boolean focused = textbox.rect.isInside(mouseX, mouseY);
            if (focused) {
                textbox.box.setCursor1(1000);
                textbox.box.setCursorDelay(500);
            }
            textbox.focused = focused;
        });


        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        var ref = new ExposedReference<>(false);
        uii.textBoxes.forEach((name, textbox) -> {
            var reference = textbox.box.getReference();
            assert reference != null;
            var pre = reference.getValue();
            if (textbox.focused) {
                textbox.box.onChar(typedChar);
                textbox.box.onKey(keyCode);

                if (keyCode == KEYBOARD.KEY_ENTER() || keyCode == KEYBOARD.KEY_ESCAPE()) {
                    textbox.focused = false;
                }
                ref.value = true;
            }
            var post = reference.getValue();
            if (!post.equals(pre)) {
                textbox.dirty = true;
            }
        });
        if (keyCode != 0) {
            uii.lastKey = keyCode;
        }
        // uii.lastKey = ref.value ? -1 : keyCode;

        super.keyTyped(typedChar, keyCode);
    }

    @RequiredArgsConstructor
    public class UII {

        public boolean leftClick = false;
        public boolean rightClick = false;


        public boolean pressedLeft = false;
        public boolean pressedRight = false;
        public @NotNull IRenderer gui;

        public boolean control = false;
        public boolean alt = false;
        public boolean shift = false;
        public int iconTint = -1;
        private int lastKey = -1;

        public boolean key(int key) {
            return lastKey == key;
        }

        Set<String> clickedSet = new HashSet<>();
        Set<String> hoveredSet = new HashSet<>();
        List<Button> buttons = new ArrayList<>();

        HashMap<String, TextInput> textBoxes = new HashMap<>();

        HashMap<String, Icon> iconMap = new HashMap<>();

        float translationX = 0;
        float translationY = 0;

        public void scissor(float x, float y, float width, float height, Runnable call) {
            x += translationX;
            y += translationY;
            final float[] mat = GUI.getModelViewMatrix();
            final Vector2f globalPosMIN = GUI.toScreenSpace(mat, x, y);
            final Vector2f globalPosMAX = GUI.toScreenSpace(mat, x + width, y + height);
            GUI.pushScissor(globalPosMIN.x, globalPosMIN.y, globalPosMAX.x, globalPosMAX.y);
            call.run();
            GUI.popScissor();
        }

        public void translate(float x, float y, Runnable call) {
            float tX = translationX;
            float tY = translationY;
            translationX += x;
            translationY += y;

            call.run();

            translationX = tX;
            translationY = tY;
        }

        public <T> float forEach(Collection<T> list, BiFunction<T, Float, Float> consumer) {
            float counter = 0;
            for (T element : list) {
                counter += consumer.apply(element, counter);
            }
            return counter;
        }

        public void icon(String src, float x, float y, float width, float height) {
            var icon = iconMap.computeIfAbsent(src, Icon::new);
            icon.texture.drawStatic(x, y, x + width, y + height, 0, 0, 1, 1, iconTint);
        }

        public void icon(String src, float x, float y, float size) {
            icon(src, x, y, size, size);
        }

        public boolean clickableIcon(String name, String icon, float x, float y, float width, float height) {
            icon(icon, x, y, width, height);
            buttons.add(new Button(new Rect(x, y, x + width, y + height), name));
            return clickedSet.contains(name);
        }

        private @Nullable ContextMenu contextMenu;
        private @Nullable ContextMenu.Menu latestMenu;

        public void subMenu(@NotNull String name, @NotNull Runnable call) {
            var menu = latestMenu;
            assert menu != null;
            ContextMenu.Menu current = new ContextMenu.Menu(name);
            menu.options.add(current);
            latestMenu = current;
            call.run();
            latestMenu = menu;
        }

        public void popUp(@NotNull Runnable call) {
            contextMenu = new ContextMenu(lastKnownMouseX, lastKnownMouseY);
            latestMenu = contextMenu.topMenu;
            call.run();

            if (contextMenu.topMenu.options.isEmpty()) {
                contextMenu = null;
            }
        }

        public void option(@NotNull String text, @NotNull Runnable call) {
            assert latestMenu != null;
            latestMenu.options.add(new ContextMenu.Option(call, text, null));
        }

        public void option(@NotNull String text, String icon, @NotNull Runnable call) {
            assert latestMenu != null;
            var icon_ = iconMap.computeIfAbsent(icon, Icon::new);
            latestMenu.options.add(new ContextMenu.Option(call, text, icon_));
        }

        @AllArgsConstructor
        static class TextInput {
            TextBox box;
            boolean dirty;
            boolean focused;
            Rect rect;
        }

        record Button(Rect rect, String name) {
        }

        public void preClear() {
            buttons.clear();
        }

        public void postClear() {
            clickedSet.clear();
        }

        public String textbox(float x, float y, float width, float height, @NotNull String name, @NotNull String text) {
            x += translationX;
            y += translationY;

            final float[] mat = GUI.getModelViewMatrix();
            final Vector2f globalPosMIN = GUI.toScreenSpace(mat, x, y);
            final Vector2f globalPosMAX = GUI.toScreenSpace(mat, x + width, y + height);
            GUI.pushScissor(globalPosMIN.x, globalPosMIN.y, globalPosMAX.x, globalPosMAX.y);

            var paddingX = 5;
            var paddingY = 4;


            var textbox = textBoxes.computeIfAbsent(name, (str) -> {
                ObjectReference<String> textReference = new ObjectReference<>(text);
                return new TextInput(new TextBox(textReference, GUI, KEYBOARD, 0, 0, 0, 0), false, false, new Rect(0, 0, 0, 0));
            });

            textbox.rect.x = x;
            textbox.rect.y = y;
            textbox.rect.x2 = x + width;
            textbox.rect.y2 = y + height;

            textbox.box.x = x + paddingX;
            textbox.box.y = y + paddingY;
            textbox.box.width = width - paddingX * 2;
            textbox.box.height = height - paddingY * 2;

            var bgColor = ColorLib.getBackground(0.5f);
            var radius = 5;

            GUI.drawRoundedRect(x, y, x + width, y + height, radius, bgColor);
            if (text.isEmpty()) {
                GUI.drawCenteredString(name, x + width / 2, y + height / 2, 0xFFA0A0A0);
            }

            textbox.box.drawScreen(textbox.focused);

            if (textbox.focused) {
                GUI.drawLinedRoundedRect(x, y, x + width, y + height, radius, -1, 1.5f);
            }
            GUI.popScissor();

            assert textbox.box.getReference() != null;
            if (textbox.dirty) {
                textbox.dirty = false;
                return textbox.box.getReference().getValue();
            }
            textbox.box.getReference().setValue(text);

            return text;
        }

        public boolean button(float x, float y, float height, @NotNull String name) {
            x += translationX;
            y += translationY;
            float width = GUI.getStringWidth(name) + 8;
            buttons.add(new Button(new Rect(x, y, x + width, y + height), name));

            boolean isHovered = hoveredSet.contains(name);
            var buttonColor = ColorLib.getMainColor(0);
            var radius = 5;

            GUI.drawRoundedRect(x, y, x + width, y + height, radius, isHovered ? ColorLib.getDarker(buttonColor) : buttonColor);

            if (isHovered && MOUSE.mouseDown(0)) {
                GUI.drawLinedRoundedRect(x, y, x + width, y + height, radius, -1, 1.5f);
            }

            GUI.drawCenteredString(name, x + width / 2, y + height / 2, ColorLib.getTextColor());


            return clickedSet.contains(name);
        }

    }

    @AllArgsConstructor
    public static class Rect {
        float x, y, x2, y2;

        public boolean isInside(float x, float y) {
            return x >= this.x && y >= this.y && x < this.x2 && y < this.y2;
        }
    }
}
