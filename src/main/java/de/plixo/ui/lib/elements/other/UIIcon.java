package de.plixo.ui.lib.elements.other;

import de.plixo.intermediate.Icon;
import de.plixo.rendering.Texture;
import de.plixo.ui.lib.elements.UIElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class UIIcon extends UIElement {

    @Setter
    @Nullable Icon icon;
    public UIIcon() {
        setColor(-1);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        assert icon != null;
        icon.texture.drawStatic(x, y, x + width, y + height,0,0,1,1,getColor());
    }

    public void load(@NotNull String str) {
        this.icon = new Icon(str);
    }
}
