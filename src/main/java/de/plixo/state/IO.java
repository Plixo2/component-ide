package de.plixo.state;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseMoveEvent;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class IO {
    @Getter
    @Setter
    private static Vector2f mouse = new Vector2f();


    public static boolean mouseDown(int key) {
        return glfwGetMouseButton(Window.INSTANCE.id(), key) == GLFW_PRESS;
    }

    public static boolean keyDown(int key) {
        return glfwGetKey(Window.INSTANCE.id(), key) == GLFW_PRESS;
    }


    @SubscribeEvent
    static void mouse(@NotNull MouseMoveEvent event) {
        mouse = new Vector2f((float) event.mouse().x, (float) event.mouse().y);
    }
}
