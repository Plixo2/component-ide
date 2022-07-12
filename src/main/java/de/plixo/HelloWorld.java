package de.plixo;

import de.plixo.event.Dispatcher;
import de.plixo.event.impl.InitEvent;
import de.plixo.event.impl.RenderEvent;
import de.plixo.general.FileUtil;
import de.plixo.rendering.Mesh;
import de.plixo.rendering.Shader;
import de.plixo.rendering.Texture;
import de.plixo.state.IO;
import de.plixo.state.Window;
import de.plixo.ui.FontRenderer;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.io.IOException;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class HelloWorld {

    // The window handle

    FontRenderer renderer;

    public void run() {
        System.out.println("Running LWJGL " + Version.getVersion() + "!");

        registerStatic();
        Dispatcher.emit(new InitEvent());

        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(Window.INSTANCE.id());
        glfwDestroyWindow(Window.INSTANCE.id());

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }


    private void loop() {
        GL.createCapabilities();
        glClearColor(0.99f, 0.99f, 0.99f, 1.0f);

        renderer = new FontRenderer(new Font("Verdana", Font.BOLD, 50));

        final Shader shader = Shader.fromSource(FileUtil.loadAsString("content/shader/test.vert"),
                FileUtil.loadAsString("content/shader/test.frag"));
        final Shader.Uniform projection = shader.uniform("projection");
        final Shader.Uniform view = shader.uniform("view");
        final Shader.Uniform model = shader.uniform("model");

        float vertices[] = {0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
                0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
                -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
                -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f  // top left
        };
        int indices[] = {  // note that we start from 0!
                0, 1, 3,  // first Triangle
                1, 2, 3   // second Triangle
        };

        final Mesh mesh = Mesh.from_raw(vertices, indices,
                new Shader.Attribute[]{Shader.Attribute.Vec3, Shader.Attribute.Vec3, Shader.Attribute.Vec2});

        Texture texture;
        try {
            texture = Texture.fromFile("content/textures/exp.png", new Texture.ImgConfig(false, false));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        long lastMS = System.currentTimeMillis();
        while (!glfwWindowShouldClose(Window.INSTANCE.id())) {
            final long time = System.currentTimeMillis();
            Dispatcher.emit(new RenderEvent((time - lastMS) / 1000f));
            lastMS = time;
        }
    }

    public static void main(String[] args) {
        new HelloWorld().run();
    }

    public void registerStatic() {
        Dispatcher.registerStatic(IO.class);

        Window.INSTANCE = new Window();
        Dispatcher.register(Window.INSTANCE);
    }
}