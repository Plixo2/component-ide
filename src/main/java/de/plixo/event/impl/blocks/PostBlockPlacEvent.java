package de.plixo.event.impl.blocks;

import de.plixo.event.Event;
import de.plixo.game.Block;
import org.jetbrains.annotations.NotNull;

public record PostBlockPlacEvent(@NotNull Block block)  implements Event {
}
