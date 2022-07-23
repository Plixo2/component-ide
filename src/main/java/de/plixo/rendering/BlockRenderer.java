package de.plixo.rendering;

import de.plixo.game.Block;
import de.plixo.rendering.MeshBundle;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public abstract class BlockRenderer<T extends Block> {
    public void draw(@NotNull T block, @NotNull Matrix4f projection) {
        for (MeshTexture mesh : block.getMeshes()) {
            final var texture = mesh.texture();
            if (texture != null) {
                glEnable(GL_TEXTURE_2D);
                glActiveTexture(GL_TEXTURE0);
                texture.bind();
            } else {
                glDisable(GL_TEXTURE_2D);
            }
            mesh.mesh().render();
        }
    }
}
