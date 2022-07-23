package de.plixo.systems;

import de.plixo.event.AssetServer;
import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.*;
import de.plixo.general.Color;
import de.plixo.general.GLFWSetup;
import de.plixo.rendering.Camera;
import de.plixo.rendering.targets.RenderTarget;
import de.plixo.state.Assets;
import de.plixo.ui.impl.FontRenderer;
import de.plixo.ui.impl.GLFWKeyboard;
import de.plixo.ui.impl.GLFWMouse;
import de.plixo.ui.impl.OpenGlRenderer;
import de.plixo.ui.lib.general.LodestoneUI;
import de.plixo.ui.lib.general.UIManager;
import lombok.Getter;
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
    public static float UI_SCALE = 2f;


    @Getter
    @Accessors(fluent = true)
    private int unscaled_width;
    @Getter
    @Accessors(fluent = true)
    private int unscaled_height;

    @Getter
    @Accessors(fluent = true)
    private RenderTarget worldTarget;


    public void setup2D() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisable(GL_DEPTH_TEST);
        worldTarget.popBuffer();
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, unscaled_width, unscaled_height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glUseProgram(0);
    }

    public void setup3D() {
        worldTarget.pushBuffer(Color.WHITE);

        if (UISystem.reflectBool("Outlines", false)) {
            glDisable(GL_CULL_FACE);
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glCullFace(GL_BACK);
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
        final var camera = AssetServer.get(Camera.class);
        camera.perspective(UISystem.reflectBool("Perspective", true));

        final Matrix4f projectionMatrix = camera.projection();
        final Matrix4f viewMatrix = camera.view();

        glMatrixMode(GL_PROJECTION);
        try (final MemoryStack stack = stackPush()) {
            glLoadMatrixf(projectionMatrix.get(stack.mallocFloat(16)));
            glMatrixMode(GL_MODELVIEW);
            glLoadMatrixf(viewMatrix.get(stack.mallocFloat(16)));
        }
        val projView = new Matrix4f();
        projectionMatrix.mul(viewMatrix, projView);
        AssetServer.update(new Assets.ProjView(projView));
    }


    @SubscribeEvent
    void init(@NotNull InitEvent event) {
        val init = GLFWSetup.initGlfw();
        AssetServer.update(new Assets.Window(init.first));
        unscaled_width = init.second;
        unscaled_height = init.third;

        AssetServer.insertAndRegister(new Camera());

        GL.createCapabilities();
    }

    @SubscribeEvent
    void postInit(@NotNull PostInitEvent event) {
        OpenGlRenderer.setFontRenderer(new FontRenderer(new Font("Verdana", Font.BOLD, 18)));
        new LodestoneUI(new OpenGlRenderer(), new GLFWKeyboard(), new GLFWMouse());
        Dispatcher.emit(new ResizeEvent(new Vector2i(unscaled_width, unscaled_height)));
    }

    @SubscribeEvent
    void shutdown(@NotNull ShutDownEvent event) {
        final var window = AssetServer.get(Assets.Window.class).id();
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
        unscaled_width = event.size().x;
        unscaled_height = event.size().y;

        UIManager.INSTANCE = new UIManager(unscaled_width / UI_SCALE, unscaled_height / UI_SCALE);
        val win = UIManager.displayWindow("Main");
        win.getCanvas().setColor(0);

        if (worldTarget != null) {
            worldTarget.delete();
        }

        worldTarget = RenderTarget.generate(unscaled_width, unscaled_height);
        worldTarget.new_texture(GL_COLOR_ATTACHMENT0);
        worldTarget.new_buffer(GL_DEPTH_STENCIL_ATTACHMENT, GL_DEPTH24_STENCIL8);
        worldTarget.seal();

        Dispatcher.emit(new UIInitEvent(win.getCanvas()));
    }


}
