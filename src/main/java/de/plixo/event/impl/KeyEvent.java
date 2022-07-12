package de.plixo.event.impl;

import de.plixo.event.Event;

public record KeyEvent(int key, int scancode, int action, int mods) implements Event {
}
