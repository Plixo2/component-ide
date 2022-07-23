package de.plixo.game.item;

import de.plixo.game.Block;
import de.plixo.rendering.Mesh;
import de.plixo.rendering.MeshBundle;
import de.plixo.rendering.MeshTexture;
import de.plixo.rendering.targets.Shader;
import de.plixo.rendering.targets.Texture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Item {

    @Getter
    @Setter
    @Accessors(fluent = true)
    private @NotNull String localName;

    @Getter
    @Accessors(fluent = true)
    private @Nullable Item container = null;

    @Getter
    @Accessors(fluent = true)
    private @Nullable Material material = null;

    @Getter
    @Accessors(fluent = true)
    private @NotNull Rarity rarity = Rarity.COMMON;

    @Getter
    @Accessors(fluent = true)
    boolean is3D = false;

    @Getter
    @Accessors(fluent = true)
    boolean isTool = false;

    @Getter
    @Accessors(fluent = true)
    @Nullable Shader shader;
    @Getter
    @Accessors(fluent = true)
    @NotNull List<MeshTexture> meshes = new ArrayList<>();

    public Item(@NotNull String localName) {
        this.localName = localName;
    }

    public void startUsing(@NotNull Block block, @NotNull ItemStack stack) {

    }

    public void stopUsing(@NotNull Block block, @NotNull ItemStack stack) {

    }

    public void whileUsing(@NotNull Block block, @NotNull ItemStack stack) {

    }

    public boolean canRemove(@NotNull Block block, @NotNull ItemStack stack) {
        return false;
    }

    public boolean dropLoot(@NotNull Block block, @NotNull ItemStack stack) {
        return true;
    }


    protected void addSprite(@NotNull String name) {
        try {
            if(!name.endsWith(".png")) {
                name += ".png";
            }
            final var generate = MeshTexture.generate("item.obj", "item.mtl");
            final var texture = Texture.fromFile("content/textures/items/" + name);
            this.meshes.add(new MeshTexture(texture,generate[0].mesh()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    protected void loadDefaultItemShader() {
        this.shader = Shader.fromDSA("obj.toml");
        this.shader.seal();
    }
}
