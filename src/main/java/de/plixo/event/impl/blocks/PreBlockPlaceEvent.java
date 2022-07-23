package de.plixo.event.impl.blocks;

import de.plixo.event.Event;
import org.joml.Vector3i;

public record PreBlockPlaceEvent(Vector3i position) implements Event {
}
