package de.plixo.rendering.blockrendering;

import de.plixo.game.Block;
import de.plixo.game.BlockMesh;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public abstract class BlockRenderer<T extends Block> {
    public void draw(@NotNull T block, @NotNull BlockMesh blockMesh, @NotNull Matrix4f projection) {
        blockMesh.render();
    }
}
