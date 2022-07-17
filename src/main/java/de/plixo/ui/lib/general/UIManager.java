package de.plixo.ui.lib.general;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.*;
import de.plixo.general.IO;
import de.plixo.systems.RenderSystem;
import de.plixo.ui.impl.OpenGlRenderer;
import de.plixo.ui.lib.elements.IGuiEvent;
import de.plixo.ui.lib.elements.UIElement;
import de.plixo.ui.lib.elements.layout.UICanvas;
import de.plixo.ui.lib.elements.layout.UIContext;
import de.plixo.ui.lib.elements.layout.UIDraggable;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

import static de.plixo.systems.RenderSystem.UI_SCALE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.opengl.GL11.*;

public class UIManager implements IGuiEvent {
    public static UIManager INSTANCE;
    public static float reduceModalSize = 0.2f;
    @Getter
    float width, height;
    public float mouseX, mouseY;

    public UIManager(float width, float height) {
        this.width = width;
        this.height = height;
    }

    Stack<Window> stack = new Stack<>();

    public static Window displayWindow(String name) {
        UICanvas canvas = new UICanvas();
        canvas.setDimensions(0, 0, INSTANCE.width, INSTANCE.height);
        canvas.setHoverColor(0);

        Window window = new Window(name, canvas, canvas) {
            @Override
            public void dispose() {
                super.dispose();
                closeWindow(this);
            }
        };
        INSTANCE.stack.push(window);
        return window;
    }

    public static Window displayModalWindow(String name) {
        UICanvas canvas = new UICanvas();
        canvas.setHoverColor(0);

        canvas.setDimensions(INSTANCE.width * reduceModalSize, INSTANCE.height * reduceModalSize,
                INSTANCE.width * (1 - reduceModalSize * 2), INSTANCE.height * (1 - reduceModalSize * 2));

        Window window = new Window(name, canvas, canvas) {
            @Override
            public void dispose() {
                super.dispose();
                closeWindow(this);
            }
        };
        INSTANCE.stack.push(window);
        return window;
    }

    public static Window displayDraggableModalWindow(String name, float headSize) {
        UICanvas canvas = new UIDraggable();
        canvas.setDimensions(INSTANCE.width * reduceModalSize, INSTANCE.height * reduceModalSize,
                INSTANCE.width * (1 - reduceModalSize * 2), headSize);
        canvas.setColor(0);
        canvas.setHoverColor(0);
        UIContext full = new UIContext();
        full.setDimensions(0, 0, canvas.width, INSTANCE.height * (1 - reduceModalSize * 2));
        canvas.add(full);

        full.setDrawContext((x, y) -> {
            UIContext.GUI.drawRoundedRect(0, 0, full.width, headSize, full.getRoundness(),
                    ColorLib.getDarker(full.getColor()));
        });

        Window window = new Window(name, full, canvas) {
            @Override
            public void dispose() {
                super.dispose();
                closeWindow(this);
            }
        };
        INSTANCE.stack.push(window);
        return window;
    }

    public static void closeWindow() {
        if (INSTANCE.stack.size() > 1) {
            INSTANCE.stack.pop().dispose();
        }

    }

    public static void closeWindow(Window window) {
        if (window.onDispose != null) {
            window.onDispose.run();
        }
        INSTANCE.stack.remove(window);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        if (stack.isEmpty()) {
            return;
        }
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        glEnable(GL_SCISSOR_TEST);
        ((OpenGlRenderer) UIElement.GUI).scissorStack.clear();
        UIElement.GUI.pushScissor(0, 0, width * UI_SCALE, height * UI_SCALE);
        stack.forEach(ref -> ref.internal.drawScreen(mouseX, mouseY));
        glDisable(GL_SCISSOR_TEST);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == UIElement.KEYBOARD.KEY_ESCAPE()) {
            closeWindow();
            return;
        }
        if (stack.isEmpty()) {
            return;
        }
        getCurrentWindow().internal.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (stack.isEmpty()) {
            return;
        }
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        getCurrentWindow().internal.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (stack.isEmpty()) {
            return;
        }
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        getCurrentWindow().internal.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void onTick() {
        getCurrentWindow().internal.onTick();
    }

    public Window getCurrentWindow() {
        if (stack.isEmpty()) {
            return null;
        }
        return stack.peek();
    }

    public Stack<Window> getStack() {
        return stack;
    }

    public static class Window {
        String name;
        UICanvas canvas;
        UICanvas internal;
        Runnable onDispose;

        public Window(String name, UICanvas canvas, UICanvas internal) {
            this.name = name;
            this.canvas = canvas;
            this.internal = internal;
        }

        public UICanvas getCanvas() {
            return canvas;
        }

        public UICanvas getInternal() {
            return internal;
        }

        public void dispose() {
            if (onDispose != null) {
                onDispose.run();
            }
        }

        public String getName() {
            return name;
        }

        public void setOnDispose(Runnable onDispose) {
            this.onDispose = onDispose;
        }
    }


    static int fps = 0;
    @SubscribeEvent
    static void tick(@NotNull TickEvent event) {
        assert INSTANCE != null;
        INSTANCE.onTick();
        fps = Math.round(1f / RenderSystem.INSTANCE.delta_time());
    }

    @SubscribeEvent
    static void onClick(@NotNull MouseClickEvent event) {
        assert INSTANCE != null;
        INSTANCE.mouseClicked(event.mouseX() / UI_SCALE, event.mouseY() / UI_SCALE, event.button());
    }

    @SubscribeEvent
    static void onRelease(@NotNull MouseReleaseEvent event) {
        assert INSTANCE != null;
        INSTANCE.mouseReleased(event.mouseX() / UI_SCALE, event.mouseY() / UI_SCALE, event.button());
    }

    @SubscribeEvent
    static void key(@NotNull KeyEvent event) {
        assert INSTANCE != null;
        if (event.action() == GLFW_PRESS)
            INSTANCE.keyTyped((char) 0, event.key());
    }

    @SubscribeEvent
    static void onChar(@NotNull CharEvent event) {
        assert INSTANCE != null;
        INSTANCE.keyTyped(event.character(), 0);
    }

    @SubscribeEvent
    static void draw(@NotNull Render2DEvent event) {
//        assert INSTANCE != null;
//        UIElement.GUI.pushMatrix();
//        UIElement.GUI.scale(UI_SCALE, UI_SCALE);
//        INSTANCE.drawScreen(IO.getMouse().x / UI_SCALE, IO.getMouse().y / UI_SCALE);
//        UIElement.GUI.popMatrix();

    }
}
