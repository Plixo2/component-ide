package de.plixo.impl.render;

import de.plixo.game.blocks.InventoryBlock;
import de.plixo.rendering.BlockRenderer;
import de.plixo.rendering.MeshTexture;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class InventoryBlockRenderer extends BlockRenderer<InventoryBlock> {


    @Override
    public void draw(@NotNull InventoryBlock block, @NotNull Matrix4f projection) {
        final var shader = block.getShader();
        shader.uniform("projview").load(projection);
        final Matrix4f t = new Matrix4f();
        t.translate(block.x() + 0.5f, block.y() + 0.5f, block.z() + 0.5f);
        shader.uniform("model").load(t);
        shader.flush();
        super.draw(block,projection);
    }
}
