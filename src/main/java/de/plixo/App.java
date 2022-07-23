package de.plixo;

import de.plixo.systems.AnimationSystem;
import de.plixo.event.AssetServer;
import de.plixo.event.Dispatcher;
import de.plixo.event.impl.*;
import de.plixo.rendering.Debug;
import de.plixo.general.IO;
import de.plixo.state.Assets;
import de.plixo.state.UIState;
import de.plixo.systems.*;
import de.plixo.ui.PlacePanel;
import de.plixo.ui.UIInvTest;
import de.plixo.ui.impl.GLFWMouse;
import de.plixo.ui.impl.OpenGlRenderer;
import de.plixo.systems.UISystem;
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
        Dispatcher.registerStatic(MeshSystem.class);
        Dispatcher.registerStatic(UISystem.class);
        Dispatcher.registerStatic(AnimationSystem.class);
        Dispatcher.registerStatic(PlacePanel.class);
        Dispatcher.registerStatic(UIManager.class);
        Dispatcher.registerStatic(UIInvTest.class);

        AssetServer.insertAndRegister(new ItemSystem());
        AssetServer.insertAndRegister(new UISystem());
        AssetServer.insertAndRegister(new InteractionSystem());
        AssetServer.insertAndRegister(new RenderSystem());
        AssetServer.insertAndRegister(new WorldSystem());
        AssetServer.insertAndRegister(new AnimationSystem());


        Dispatcher.registerStatic(Debug.class);

        Dispatcher.emit(new InitEvent());
        Dispatcher.emit(new PostInitEvent());
        System.out.println("OpenGL " + glGetString(GL_VERSION));
        loop();
        Dispatcher.emit(new ShutDownEvent());
    }


    private void loop() {
        try {
            var lastFPS = System.currentTimeMillis();
            var fps_counter = 0;
            var last_ms = System.currentTimeMillis();
            RenderSystem renderSystem = AssetServer.get(RenderSystem.class);
            final var window = AssetServer.get(Assets.Window.class).id();
            while (!glfwWindowShouldClose(window)) {
                val time = System.currentTimeMillis();
                val delta_time = time - last_ms;
                val delta_time_seconds = delta_time / 1000f;
                UIState.delta_time(delta_time_seconds);

                if(time - lastFPS > 1000) {
                    System.out.println(fps_counter + " FPS");
                    fps_counter = 0;
                    lastFPS += 1000;
                }
                fps_counter++;

                glViewport(0, 0, renderSystem.unscaled_width(), renderSystem.unscaled_height());
                glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_CULL_FACE);
                renderSystem.setup3D();
                Dispatcher.emit(new Render3DEvent(delta_time_seconds));
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