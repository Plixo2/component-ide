package de.plixo.game.item;

import de.plixo.game.Block;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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


}