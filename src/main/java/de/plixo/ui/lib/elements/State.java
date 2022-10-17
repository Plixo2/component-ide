package de.plixo.ui.lib.elements;

import org.jetbrains.annotations.NotNull;

public interface State {
    @NotNull Object copy();
}
