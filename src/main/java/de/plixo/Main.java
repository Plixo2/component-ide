package de.plixo;

import de.plixo.event.Dispatcher;
import de.plixo.event.impl.*;
import de.plixo.game.MeshRegistry;
import de.plixo.state.IO;
import de.plixo.state.Window;
import de.plixo.state.World;
import de.plixo.ui.impl.GLFWMouse;
import de.plixo.ui.impl.OpenGlRenderer;
import de.plixo.ui.impl.UI;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;

public class Main {

    private long deltaTickMS;

    public void run() {
        System.out.println("Running LWJGL " + Version.getVersion() + "!");

        registerStatic();
        Dispatcher.emit(new InitEvent());

        loop();

        glfwFreeCallbacks(Window.INSTANCE.id());
        glfwDestroyWindow(Window.INSTANCE.id());

        glfwTerminate();
        final GLFWErrorCallback obj = glfwSetErrorCallback(null);
        if (obj != null) {
            obj.free();
        }
    }


    private void loop() {
        GL.createCapabilities();
        glClearColor(0.90f, 0.90f, 0.90f, 1.0f);
        Dispatcher.emit(new PostInitEvent());

        long lastMS = System.currentTimeMillis();
        while (!glfwWindowShouldClose(Window.INSTANCE.id())) {
            final long time = System.currentTimeMillis();
            Dispatcher.emit(new RenderEvent((time - lastMS) / 1000f));
            Dispatcher.emit(new PostRenderEvent());
            lastMS = time;

            long deltaMs = System.currentTimeMillis() - deltaTickMS;
            if (deltaMs > 50) {
                deltaTickMS = System.currentTimeMillis() - Math.min(50, (deltaMs - 50));
                Dispatcher.emit(new TickEvent());
            }

        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public void registerStatic() {
        Dispatcher.registerStatic(IO.class);

        Dispatcher.registerStatic(GLFWMouse.class);
        Dispatcher.registerStatic(OpenGlRenderer.class);
        Dispatcher.registerStatic(MeshRegistry.class);
        Dispatcher.registerStatic(UI.class);


        Window.INSTANCE = new Window();
        Dispatcher.register(Window.INSTANCE);

        Dispatcher.register(World.INSTANCE);
    }
}