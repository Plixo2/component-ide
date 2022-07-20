package de.plixo.general.collision;


import de.plixo.game.Block;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record RayTraceBlockHit(@NotNull EnumFacing face, @NotNull Vector3f position,
                               @NotNull Block block) implements RayTraceResult {


}
