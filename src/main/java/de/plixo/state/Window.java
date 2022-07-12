package de.plixo.state;

import de.plixo.Camera;
import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.*;
import de.plixo.general.reference.ExposedReference;
import de.plixo.rendering.Debug;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    public static Window INSTANCE;

    @Getter()
    @Setter
    @Accessors(fluent = true)
    private long id;

    @Getter
    @Accessors(fluent = true)
    private int width;
    @Getter
    @Accessors(fluent = true)
    private int height;

    @Getter
    @Accessors(fluent = true)
    private Camera camera;

    @SubscribeEvent
    void init(@NotNull InitEvent event) {
        id = initGlfw();
        camera = new Camera(0, 0, 6, false, new Vector3f(), 70);
        Dispatcher.register(camera);
        Dispatcher.emit(new ResizeEvent(new Vector2i(width, height)));
    }


    @SubscribeEvent
    void renderEvent(@NotNull RenderEvent event) {
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        final Matrix4f projectionMatrix = camera.projection();
        final Matrix4f viewMatrix = camera.view();

        glMatrixMode(GL_PROJECTION);
        try (final MemoryStack stack = stackPush()) {
            glLoadMatrixf(projectionMatrix.get(stack.mallocFloat(16)));
        }
        glMatrixMode(GL_MODELVIEW);
        try (final MemoryStack stack = stackPush()) {
            glLoadMatrixf(viewMatrix.get(stack.mallocFloat(16)));
        }

        //TODO 3D
        Debug.renderGizmo();

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        //TODO 2D

        glfwSwapBuffers(id);
        glfwPollEvents();
    }


    @SubscribeEvent
    void resize(@NotNull ResizeEvent event) {
        width = event.size().x;
        height = event.size().y;
    }

    private long initGlfw() {
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

        if (glfwRawMouseMotionSupported()) glfwSetInputMode(window, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);

        long cursor = glfwCreateStandardCursor(org.lwjgl.glfw.GLFW.GLFW_CROSSHAIR_CURSOR);
        glfwSetCursor(window, cursor);

        glfwSetKeyCallback(window, (window_, key, scancode, action, mods) -> {
            Dispatcher.emit(new KeyEvent(key, scancode, action, mods));
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window_, true);
        });
        ExposedReference<Vector2d> last = new ExposedReference<>(new Vector2d());
        glfwSetCursorPosCallback(window, (window_, x, y) -> {
            final Vector2d pos = new Vector2d(x, y);
            final Vector2d delta = new Vector2d(x, y).sub(last.value);
            Dispatcher.emit(new MouseEvent(pos, delta));
            last.value = pos;
        });
        glfwSetFramebufferSizeCallback(window, ((window_, width, height) -> {
            Dispatcher.emit(new ResizeEvent(new Vector2i(width, height)));
        }));

        glfwSetScrollCallback(window, ((window_, x, y) -> {
            Dispatcher.emit(new ScrollEvent(new Vector2d(x, y)));
        }));


        try (MemoryStack stack = stackPush()) {
            final IntBuffer pWidth = stack.mallocInt(1);
            final IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            final GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            assert vidmode != null;
            width = pWidth.get(0);
            height = pHeight.get(0);
            glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        return window;
    }


}
