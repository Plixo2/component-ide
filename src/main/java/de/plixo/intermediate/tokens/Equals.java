package de.plixo.intermediate.tokens;

import de.plixo.intermediate.Icon;
import org.lwjgl.glfw.GLFW;

public class Equals extends Token {
    static Icon icon = new Icon("equal");

    @Override
    public Icon icon() {
        return icon;
    }

    @Override
    public int key() {
        return SHIFT | GLFW.GLFW_KEY_0;
    }
}
