package de.plixo.event.impl;

import de.plixo.event.Event;
import de.plixo.game.Block;
import de.plixo.general.collision.RayTraceResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

public record WorldHoverEvent(@NotNull RayTraceResult hit, @NotNull Vector3i position) implements Event {
}
