package de.plixo.event.impl;

import de.plixo.event.Event;
import de.plixo.ui.lib.elements.layout.UICanvas;
import org.jetbrains.annotations.NotNull;

public record UIChildEvent(@NotNull UICanvas canvas) implements Event {
}
