package de.plixo.rendering.blockrendering;

import de.plixo.game.Block;
import de.plixo.rendering.MeshBundle;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public abstract class BlockRenderer<T extends Block> {
    public void draw(@NotNull T block, @NotNull MeshBundle meshBundle, @NotNull Matrix4f projection) {
        meshBundle.render();
    }
}
