package de.plixo.ui.general;

import de.plixo.ui.elements.IGuiEvent;
import de.plixo.ui.elements.UIElement;
import de.plixo.ui.elements.layout.UICanvas;
import de.plixo.ui.elements.layout.UIContext;
import de.plixo.ui.elements.layout.UIDraggable;
import de.plixo.ui.impl.OpenGlRenderer;
import lombok.Getter;

import java.util.Stack;

import static de.plixo.state.Window.UI_SCALE;
import static org.lwjgl.opengl.GL11.*;

public class MainWindow implements IGuiEvent {
    public static MainWindow INSTANCE;
    public static float reduceModalSize = 0.2f;
    @Getter
    int width, height;
    public float mouseX, mouseY;

    public MainWindow(int width, int height) {
        INSTANCE = this;
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
//        UIElement.GUI.drawRect(0, 0, width, height, 0xFF00FF00);
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
}
