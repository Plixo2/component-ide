package de.plixo.ui.lib.elements.other.menu;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MenuList<O>(@NotNull List<MenuEntry<O>> list,@NotNull String name) implements MenuEntry<O> {
    public void add(MenuEntry<O> entry) {
        this.list.add(entry);
    }
}
