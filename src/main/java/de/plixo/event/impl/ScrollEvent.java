package de.plixo.event.impl;

import de.plixo.event.Event;
import org.joml.Vector2d;
import org.joml.Vector2f;

public record ScrollEvent(Vector2d delta) implements Event {
}
