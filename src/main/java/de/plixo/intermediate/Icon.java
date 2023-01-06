package de.plixo.intermediate;

import de.plixo.rendering.Texture;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;

public class Icon {
    @NotNull String name;
    @NotNull
    public Texture texture;
    @SneakyThrows
    public Icon(@NotNull String name) {
        this.name = name;
        this.texture = Texture.fromFile("content/icons/" + name + ".png");
        this.texture.tex_parameter(GL_TEXTURE_MIN_FILTER,GL_LINEAR);
        this.texture.tex_parameter(GL_TEXTURE_MAG_FILTER,GL_LINEAR);
        this.texture.seal();
    }

    public void draw(float x, float y, float size) {
        this.texture.drawStatic(x, y, x + size, y + size, 0, 0, 1, 1, -1);
    }
}
