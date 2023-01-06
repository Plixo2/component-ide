package de.plixo.intermediate.ui;

import de.plixo.intermediate.Icon;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ContextMenu {

    public interface MenuEntry {

    }

    @RequiredArgsConstructor
    public static class Option implements MenuEntry {
        public boolean lastHovered = false;
        final @NotNull Runnable option;
        final @NotNull String text;
        final @Nullable Icon icon;
    }
    @RequiredArgsConstructor
    public static class Menu implements MenuEntry {
        public boolean lastHovered = false;
        final String name;
        List<MenuEntry> options = new ArrayList<>();
        @Nullable MenuEntry selected;
    }

    public @NotNull Menu topMenu = new Menu("top");

    final float posX, posY;
}
