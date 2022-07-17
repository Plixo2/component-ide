package de.plixo.general;

import de.plixo.event.Dispatcher;
import de.plixo.event.impl.*;
import de.plixo.general.Tuple;
import de.plixo.general.reference.ExposedReference;
import lombok.val;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWSetup {
    public static Tuple.Triple<Long, Integer, Integer> initGlfw() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SAMPLES, 4);

        val window = glfwCreateWindow(1280, 720, "", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        if (glfwRawMouseMotionSupported())
            glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);

        long cursor = glfwCreateStandardCursor(org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR);
        glfwSetCursor(window, cursor);

        glfwSetKeyCallback(window, (window_, key, scancode, action, mods) -> {
            Dispatcher.emit(new KeyEvent(key, scancode, action, mods));
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window_, true);
        });
        ExposedReference<Vector2d> last = new ExposedReference<>(new Vector2d());
        glfwSetCursorPosCallback(window, (window_, x, y) -> {
            final Vector2d pos = new Vector2d(x, y);
            final Vector2d delta = new Vector2d(x, y).sub(last.value);
            Dispatcher.emit(new MouseMoveEvent(pos, delta));
            last.value = pos;
        });

        glfwSetFramebufferSizeCallback(window, ((window_, width, height) -> {
            Dispatcher.emit(new ResizeEvent(new Vector2i(width, height)));
        }));

        glfwSetCharCallback(window, ((window_, codepoint) -> {
            Dispatcher.emit(new CharEvent((char) codepoint));
        }));

        glfwSetMouseButtonCallback(window, ((window_, button, action, mods) -> {
            try (final var stack = stackPush()) {
                val x = stack.mallocDouble(1);
                val y = stack.mallocDouble(1);
                glfwGetCursorPos(window, x, y);
                final float x_ = (float) x.get();
                final float y_ = (float) y.get();
                if (action == GLFW_PRESS) {
                    Dispatcher.emit(new MouseClickEvent(button, x_, y_));
                } else {
                    Dispatcher.emit(new MouseReleaseEvent(button, x_, y_));
                }
            }
        }));
        glfwSetScrollCallback(window, ((window_, x, y) -> {
            Dispatcher.emit(new ScrollEvent(new Vector2d(x, y)));
        }));

        int width, height;
        try (MemoryStack stack = stackPush()) {
            val pWidth = stack.mallocInt(1);
            val pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            val vid_mode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            assert vid_mode != null;
            width = pWidth.get(0);
            height = pHeight.get(0);
            glfwSetWindowPos(window, (vid_mode.width() - width) / 2,
                    (vid_mode.height() - height) / 2);
        }
        glfwMakeContextCurrent(window);
        glfwShowWindow(window);
        glfwSwapInterval(1);

        return new Tuple.Triple<>(window, width, height);
    }
}
