package de.plixo.event.impl;

import de.plixo.event.Event;

public record RenderEvent(float delta) implements Event {
}
