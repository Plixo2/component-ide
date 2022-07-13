package de.plixo.event.impl;

import de.plixo.event.Event;
import de.plixo.game.Block;
import org.jetbrains.annotations.Nullable;

public record BlockClickEvent(@Nullable Block block) implements Event {
}
