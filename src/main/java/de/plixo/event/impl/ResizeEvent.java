package de.plixo.event.impl;

import de.plixo.event.Event;
import org.joml.Vector2i;

public record ResizeEvent(Vector2i size) implements Event {
}
