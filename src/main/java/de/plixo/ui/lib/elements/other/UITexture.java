package de.plixo.ui.lib.elements.other;

import de.plixo.rendering.Texture;
import de.plixo.ui.lib.elements.UIElement;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class UITexture extends UIElement {
    @Getter
    @Setter
    @Accessors(fluent = true)
    Texture texture;

    public UITexture() {
        setColor(-1);
    }

    @Override
    public void drawScreen(float mouseX, float mouseY) {
        assert texture != null;
        texture.drawStatic(x, y, x + width, y + height,0,0,1,1,getColor());
    }

    public void load(@NotNull String str) {
        try {
            this.texture = Texture.fromFile(str);
            this.texture.tex_parameter(GL_TEXTURE_MIN_FILTER,GL_LINEAR);
            this.texture.tex_parameter(GL_TEXTURE_MAG_FILTER,GL_LINEAR);
            this.texture.seal();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
