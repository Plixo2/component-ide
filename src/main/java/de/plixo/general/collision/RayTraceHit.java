package de.plixo.general.collision;


import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public record RayTraceHit(@NotNull EnumFacing face,
                          @NotNull Vector3f position,
                          @NotNull AABB collider) implements RayTraceResult {


    @Override
    public String toString() {
        return "RayTraceHit{" +
                "face=" + face +
                ", position=" + position +
                ", collider=" + collider +
                '}';
    }
}
