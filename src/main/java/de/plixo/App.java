package de.plixo;

import de.plixo.animation.Animation;
import de.plixo.event.Dispatcher;
import de.plixo.event.impl.*;
import de.plixo.rendering.Debug;
import de.plixo.general.IO;
import de.plixo.systems.MeshSystem;
import de.plixo.systems.InteractionSystem;
import de.plixo.systems.RenderSystem;
import de.plixo.systems.WorldSystem;
import de.plixo.ui.PlacePanel;
import de.plixo.ui.UIInvTest;
import de.plixo.ui.impl.GLFWMouse;
import de.plixo.ui.impl.OpenGlRenderer;
import de.plixo.ui.lib.UI;
import de.plixo.ui.lib.general.UIManager;
import lombok.val;
import org.lwjgl.Version;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.*;

public class App {

    private long deltaTickMS;

    public void run() {
        System.out.println("Running LWJGL " + Version.getVersion() + "!");

        Dispatcher.registerStatic(IO.class);
        Dispatcher.registerStatic(GLFWMouse.class);
        Dispatcher.registerStatic(OpenGlRenderer.class);
        Dispatcher.registerStatic(MeshSystem.class);
        Dispatcher.registerStatic(UI.class);
        Dispatcher.registerStatic(Animation.class);
        Dispatcher.registerStatic(PlacePanel.class);
        Dispatcher.registerStatic(UIManager.class);
        Dispatcher.registerStatic(UIInvTest.class);

        InteractionSystem.INSTANCE = new InteractionSystem();
        Dispatcher.register(InteractionSystem.INSTANCE);

        RenderSystem.INSTANCE = new RenderSystem();
        Dispatcher.register(RenderSystem.INSTANCE);
        WorldSystem.INSTANCE = new WorldSystem();
        Dispatcher.register(WorldSystem.INSTANCE);

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
            var cFps = 0;
            var lastMS = System.currentTimeMillis();
            while (!RenderSystem.INSTANCE.shouldClose()) {
                val time = System.currentTimeMillis();
                val delta_time = time - lastMS;
                val delta_time_seconds = delta_time / 1000f;
                RenderSystem.INSTANCE.delta_time(delta_time_seconds);

                if(time - lastFPS > 1000) {
                    System.out.println(cFps + " FPS");
                    cFps = 0;
                    lastFPS += 1000;
                }
                cFps++;

                glViewport(0, 0, RenderSystem.INSTANCE.width(), RenderSystem.INSTANCE.height());
                glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glEnable(GL_DEPTH_TEST);
                glEnable(GL_CULL_FACE);
                RenderSystem.INSTANCE.setup3D();
                Dispatcher.emit(new Render3DEvent(delta_time_seconds));
                RenderSystem.INSTANCE.setup2D();
                Dispatcher.emit(new Render2DEvent(delta_time_seconds));

                Dispatcher.emit(new PostRenderEvent());
                glfwSwapBuffers(RenderSystem.INSTANCE.window());
                glfwPollEvents();

                val deltaMs = System.currentTimeMillis() - deltaTickMS;
                if (deltaMs > 50) {
                    deltaTickMS = System.currentTimeMillis() - Math.min(50, (deltaMs - 50));
                    Dispatcher.emit(new TickEvent());
                }
                lastMS = time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new App().run();
    }

}