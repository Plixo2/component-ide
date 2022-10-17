package de.plixo;

import de.plixo.event.AssetServer;
import de.plixo.event.Dispatcher;
import de.plixo.event.impl.*;
import de.plixo.general.IO;
import de.plixo.intermediate.UI;
import de.plixo.intermediate.TokenRegistry;
import de.plixo.state.Assets;
import de.plixo.state.UIState;
import de.plixo.systems.AnimationSystem;
import de.plixo.systems.RenderSystem;
import de.plixo.systems.UISystem;
import de.plixo.ui.impl.GLFWMouse;
import de.plixo.ui.impl.OpenGlRenderer;
import de.plixo.ui.lib.general.UIManager;
import lombok.val;
import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class App {

    private long deltaTickMS;

    public void run() {
        System.out.println("Running LWJGL " + Version.getVersion() + "!");

        Dispatcher.registerStatic(IO.class);
        Dispatcher.registerStatic(GLFWMouse.class);
        Dispatcher.registerStatic(OpenGlRenderer.class);
        Dispatcher.registerStatic(UIManager.class);
        Dispatcher.registerStatic(UI.class);
        Dispatcher.registerStatic(TokenRegistry.class);

        AssetServer.insertAndRegister(new UISystem());
        AssetServer.insertAndRegister(new RenderSystem());
        AssetServer.insertAndRegister(new AnimationSystem());


        Dispatcher.emit(new InitEvent());
        System.out.println("OpenGL " + glGetString(GL_VERSION));
        Dispatcher.emit(new PostInitEvent());
        loop();
        Dispatcher.emit(new SaveEvent());
        Dispatcher.emit(new ShutDownEvent());

    }


    private void loop() {
        try {
            var lastFPS = System.currentTimeMillis();
            var fps_counter = 0;
            var last_ms = System.currentTimeMillis();
            val renderSystem = AssetServer.get(RenderSystem.class);
            final var window = AssetServer.get(Assets.Window.class).id();
            AssetServer.insert(new Assets.Fps(0));
            while (!glfwWindowShouldClose(window)) {
                val time = System.currentTimeMillis();
                val delta_time = time - last_ms;
                val delta_time_seconds = delta_time / 1000f;
                UIState.delta_time(delta_time_seconds);

                if (time - lastFPS > 1000) {
                    AssetServer.update(new Assets.Fps(fps_counter));
                    fps_counter = 0;
                    lastFPS += 1000;
                }
                fps_counter++;

                glViewport(0, 0, renderSystem.unscaled_width(), renderSystem.unscaled_height());
                glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_CULL_FACE);
                renderSystem.setup2D();
                Dispatcher.emit(new Render2DEvent(delta_time_seconds));

                Dispatcher.emit(new PostRenderEvent());
                glfwSwapBuffers(window);
                glfwPollEvents();

                val deltaMs = System.currentTimeMillis() - deltaTickMS;
                if (deltaMs > 50) {
                    deltaTickMS = System.currentTimeMillis() - Math.min(50, (deltaMs - 50));
                    Dispatcher.emit(new TickEvent());
                }
                last_ms = time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }

}