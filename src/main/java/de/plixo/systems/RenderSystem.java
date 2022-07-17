package de.plixo.systems;

import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.*;
import de.plixo.general.Color;
import de.plixo.rendering.Camera;
import de.plixo.general.GLFWSetup;
import de.plixo.rendering.targets.RenderTarget;
import de.plixo.ui.impl.FontRenderer;
import de.plixo.ui.impl.GLFWKeyboard;
import de.plixo.ui.impl.GLFWMouse;
import de.plixo.ui.impl.OpenGlRenderer;
import de.plixo.ui.lib.UI;
import de.plixo.ui.lib.general.LodestoneUI;
import de.plixo.ui.lib.general.UIManager;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.awt.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class RenderSystem {
    public static RenderSystem INSTANCE;
    public static float UI_SCALE = 2f;

    @Getter()
    @Setter
    @Accessors(fluent = true)
    private long window;

    @Getter
    @Accessors(fluent = true)
    private int width;
    @Getter
    @Accessors(fluent = true)
    private int height;

    @Getter
    @Accessors(fluent = true)
    private Camera camera;

    @Getter
    @Setter
    @Accessors(fluent = true)
    float delta_time;

    @Getter
    @Accessors(fluent = true)
    private final Matrix4f projView = new Matrix4f();

    private RenderTarget worldTarget;


    public void setup2D() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisable(GL_DEPTH_TEST);
        worldTarget.popBuffer();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glUseProgram(0);
        worldTarget.render_texture(0, 0, width, height, GL_COLOR_ATTACHMENT0);
    }

    public void setup3D() {
        worldTarget.pushBuffer(Color.WHITE);

        if (UI.reflectBool("Outlines", false)) {
            glDisable(GL_CULL_FACE);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glCullFace(GL_BACK);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

        camera().perspective(UI.reflectBool("Perspective", true));

        final Matrix4f projectionMatrix = camera.projection();
        final Matrix4f viewMatrix = camera.view();

        glMatrixMode(GL_PROJECTION);
        try (final MemoryStack stack = stackPush()) {
            glLoadMatrixf(projectionMatrix.get(stack.mallocFloat(16)));
            glMatrixMode(GL_MODELVIEW);
            glLoadMatrixf(viewMatrix.get(stack.mallocFloat(16)));
        }
        projectionMatrix.mul(viewMatrix, projView);

    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    @SubscribeEvent
    void init(@NotNull InitEvent event) {
        val init = GLFWSetup.initGlfw();
        window = init.first;
        width = init.second;
        height = init.third;

        camera = new Camera();
        Dispatcher.register(camera);

        GL.createCapabilities();
    }

    @SubscribeEvent
    void postInit(@NotNull PostInitEvent event) {
        OpenGlRenderer.setFontRenderer(new FontRenderer(new Font("Verdana", Font.BOLD, 18)));
        new LodestoneUI(new OpenGlRenderer(), new GLFWKeyboard(), new GLFWMouse());
        Dispatcher.emit(new ResizeEvent(new Vector2i(width, height)));
    }

    @SubscribeEvent
    void shutdown(@NotNull ShutDownEvent event) {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        final GLFWErrorCallback obj = glfwSetErrorCallback(null);
        if (obj != null) {
            obj.free();
        }
    }

    @SubscribeEvent
    void resize(@NotNull ResizeEvent event) {
        width = event.size().x;
        height = event.size().y;

        UIManager.INSTANCE = new UIManager(width / UI_SCALE, height / UI_SCALE);
        val win = UIManager.displayWindow("Main");
        win.getCanvas().setColor(0);
        Dispatcher.emit(new UIInitEvent(win.getCanvas()));

        if (worldTarget != null) {
            worldTarget.delete();
        }

        worldTarget = RenderTarget.generate(width, height);
        worldTarget.new_texture(GL_COLOR_ATTACHMENT0);
        worldTarget.new_buffer(GL_DEPTH_STENCIL_ATTACHMENT, GL_DEPTH24_STENCIL8);
        worldTarget.seal();
    }
}
