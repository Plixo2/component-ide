package de.plixo.impl.block;

import de.plixo.game.ItemInventory;
import de.plixo.game.blocks.InventoryBlock;
import de.plixo.rendering.MeshBundle;
import de.plixo.rendering.MeshTexture;
import de.plixo.rendering.targets.Shader;
import org.jetbrains.annotations.NotNull;


public class Chest extends InventoryBlock {

    static MeshTexture box;

    static {
        init(() -> {
            final var bundle = MeshBundle.generate("container.obj", "container.mtl", "obj.toml", 0);
            box = bundle.first.extract(0);
        });
    }

    public Chest() {
        inventory = new ItemInventory(5, 5);
    }

    @Override
    public @NotNull Shader getShader() {
        return defaultBlockShader;
    }

    @Override
    public @NotNull MeshTexture[] getMeshes() {
        return new MeshTexture[]{box};
    }
}
