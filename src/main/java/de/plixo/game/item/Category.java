package de.plixo.game.item;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public enum Category {
    INVENTORY("Inventory"),
    LOGIC("Logic"),
    MISC("Misc"),
    MACHINE("Machine"),
    MATERIALS("Materials"),
    TOOLS("Tools"),
    TRANSPORTATION("Transportation");

    @Getter
    @Accessors(fluent = true)
    final @NotNull String uniqueName;

    Category(@NotNull String uniqueName) {
        this.uniqueName = uniqueName;
    }
}
