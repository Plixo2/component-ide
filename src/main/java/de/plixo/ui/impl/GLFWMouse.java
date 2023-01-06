package de.plixo.ui.impl;


import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostRenderEvent;
import de.plixo.event.impl.ScrollEvent;
import de.plixo.general.IO;
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

    @Override
    public boolean mouseDown(int i) {
        return IO.mouseDown(i);
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
