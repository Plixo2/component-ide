package de.plixo.ui.impl;


import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostRenderEvent;
import de.plixo.event.impl.ScrollEvent;
import de.plixo.ui.lib.interfaces.IMouse;
import org.jetbrains.annotations.NotNull;

public class GLFWMouse implements IMouse {
    @Override
    public float getDXWheel() {
        return y;
    }

    @Override
    public float getDYWheel() {
        return x;
    }

    static float x = 0;
    static float y = 0;
    @SubscribeEvent
    static void mouse(@NotNull ScrollEvent event) {
        y += event.delta().y;
        x += event.delta().x;
    }

    @SubscribeEvent
    static void mouse(@NotNull PostRenderEvent event) {
        x = 0;
        y = 0;
    }
}
