package de.plixo.ui.lib.elements.other.menu;

import org.jetbrains.annotations.NotNull;

public record MenuObject<O>(@NotNull O object) implements MenuEntry<O> {
}
