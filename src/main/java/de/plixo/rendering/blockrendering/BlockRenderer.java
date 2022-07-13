package de.plixo.rendering.blockrendering;

import de.plixo.game.Block;
import de.plixo.game.BlockMesh;
import de.plixo.game.RenderObj;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public abstract class BlockRenderer<T extends Block> {
    public void draw(@NotNull T block, @NotNull BlockMesh blockMesh) {
        for (final RenderObj obj : blockMesh.objs()) {
            obj.shader().flush();
            if (obj.texture() != null) {
                glEnable(GL_TEXTURE_2D);
                glActiveTexture(GL_TEXTURE0);
                obj.texture().bind();
            } else {
                glDisable(GL_TEXTURE_2D);
            }
            obj.mesh().drawElements();
        }
    }
}
