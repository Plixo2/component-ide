package de.plixo.event.impl;

import de.plixo.event.Event;
import org.joml.Vector2d;

public record MouseMoveEvent(Vector2d mouse, Vector2d delta) implements Event {
}
