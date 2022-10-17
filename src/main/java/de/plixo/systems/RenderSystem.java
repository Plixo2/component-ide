package de.plixo.systems;

import de.plixo.event.AssetServer;
import de.plixo.event.Dispatcher;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.*;
import de.plixo.general.Color;
import de.plixo.general.GLFWSetup;
import de.plixo.rendering.RenderTarget;
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
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.awt.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL30.*;

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
    private float scaled_width;
    @Getter
    @Accessors(fluent = true)
    private float scaled_height;



    public void setup2D() {
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, unscaled_width, unscaled_height, 0, -1, 1);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glUseProgram(0);
    }


    @SubscribeEvent
    void init(@NotNull InitEvent event) {
        val init = GLFWSetup.initGlfw();
        AssetServer.update(new Assets.Window(init.first));
        unscaled_width = init.second;
        unscaled_height = init.third;

        GL.createCapabilities();
    }

    @SubscribeEvent
    void postInit(@NotNull PostInitEvent event) {
        OpenGlRenderer.setFontRenderer(new FontRenderer(new Font("Consolas", Font.PLAIN, 20)));
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

        scaled_width = unscaled_width / UI_SCALE;
        scaled_height = unscaled_height / UI_SCALE;

        UIManager.INSTANCE = new UIManager(scaled_width, scaled_height);
        val win = UIManager.displayWindow("Main");
        win.getCanvas().setColor(0);

        Dispatcher.emit(new UIInitEvent(win.getCanvas()));
        Dispatcher.emit(new UIChildEvent(win.getCanvas()));
    }

}
