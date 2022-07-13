package de.plixo.event.impl;

import de.plixo.event.Event;

public record MouseReleaseEvent(int button, float mouseX, float mouseY) implements Event {
}
