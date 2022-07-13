package de.plixo.event.impl;

import de.plixo.event.Event;

public record MouseClickEvent(int button, float mouseX, float mouseY) implements Event {
}
