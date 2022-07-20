package de.plixo.general;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseMoveEvent;
import de.plixo.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class IO {
    @Getter
    @Setter
    private static Vector2f mouse = new Vector2f();

    @Getter
    @Setter
    private static Vector2f canvasMouse = new Vector2f();
    @Getter
    @Setter
    private static Vector2f canvasPosition = new Vector2f();


    public static boolean mouseDown(int key) {
        return glfwGetMouseButton(RenderSystem.INSTANCE.window(), key) == GLFW_PRESS;
    }

    public static boolean keyDown(int key) {
        return glfwGetKey(RenderSystem.INSTANCE.window(), key) == GLFW_PRESS;
    }


    @SubscribeEvent
    static void mouse(@NotNull MouseMoveEvent event) {
        mouse = new Vector2f((float) event.mouse().x, (float) event.mouse().y);
    }
}
