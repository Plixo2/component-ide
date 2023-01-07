package de.plixo.intermediate.tokens;

import de.plixo.ui.lib.general.Icon;
import org.lwjgl.glfw.GLFW;

public class NotToken extends Token {
    static Icon icon = new Icon("not");

    @Override
    public Icon icon() {
        return icon;
    }

    @Override
    public int key() {
        return SHIFT | GLFW.GLFW_KEY_1;
    }
}
