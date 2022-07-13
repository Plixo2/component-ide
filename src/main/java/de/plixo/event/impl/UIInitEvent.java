package de.plixo.event.impl;

import de.plixo.event.Event;
import de.plixo.ui.elements.layout.UICanvas;
import org.jetbrains.annotations.NotNull;

public record UIInitEvent(@NotNull UICanvas canvas) implements Event {
}
