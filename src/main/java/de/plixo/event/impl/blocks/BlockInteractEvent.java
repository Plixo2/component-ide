package de.plixo.event.impl.blocks;

import de.plixo.event.Event;
import de.plixo.game.Block;
import org.jetbrains.annotations.Nullable;

public record BlockInteractEvent(@Nullable Block block) implements Event {
}
