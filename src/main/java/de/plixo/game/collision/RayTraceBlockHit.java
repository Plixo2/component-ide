package de.plixo.game.collision;


import de.plixo.game.Block;
import de.plixo.systems.WorldSystem;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record RayTraceBlockHit(@NotNull EnumFacing face, @NotNull Vector3f position,
                               @NotNull WorldSystem.RenderEntry<?> block) implements RayTraceResult {


}
