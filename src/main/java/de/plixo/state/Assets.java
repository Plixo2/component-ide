package de.plixo.state;

import de.plixo.ui.lib.elements.layout.UICanvas;
import org.jetbrains.annotations.NotNull;

public class Assets {

    public record WorldCanvas(@NotNull UICanvas worldCanvas) {

    }

    public record Window(long id) {

    }

    public record Fps(int fps) {

    }
}
