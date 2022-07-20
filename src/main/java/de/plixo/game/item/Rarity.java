package de.plixo.game.item;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public enum Rarity {
    COMMON("Common"),
    UNCOMMON("Uncommon"),
    RARE("Rare"),
    EPIC("Epic");


    @Getter
    @Accessors(fluent = true)
    final @NotNull String uniqueName;

    Rarity(@NotNull String uniqueName) {
        this.uniqueName = uniqueName;
    }
}
