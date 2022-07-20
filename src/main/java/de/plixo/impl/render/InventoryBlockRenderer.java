package de.plixo.impl.render;

import de.plixo.game.blocks.InventoryBlock;
import de.plixo.impl.block.Simple;
import de.plixo.rendering.BlockRenderer;
import de.plixo.rendering.MeshBundle;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class InventoryBlockRenderer extends BlockRenderer<InventoryBlock> {


    @Override
    public void draw(@NotNull InventoryBlock block, @NotNull MeshBundle meshBundle, @NotNull Matrix4f projection) {
        final var shader = meshBundle.commonShader();
        assert shader != null;
        shader.uniform("projview").load(projection);
        final Matrix4f t = new Matrix4f();
        t.translate(block.x() + 0.5f, block.y() + 0.5f, block.z() + 0.5f);
        shader.uniform("model").load(t);

        super.draw(block, meshBundle, projection);
    }
}
