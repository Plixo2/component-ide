package de.plixo.game.collision;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.joml.Vector3i;

/**
 * from minecraft 1.8.8
 */
@AllArgsConstructor
public enum EnumFacing {

    DOWN(EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vector3i(0, -1, 0)),
    UP(EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vector3i(0, 1, 0)),
    NORTH(EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vector3i(0, 0, -1)),
    SOUTH(EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vector3i(0, 0, 1)),
    WEST(EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vector3i(-1, 0, 0)),
    EAST(EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vector3i(1, 0, 0));

    @Getter
    @Accessors(fluent = true)
    @NotNull
    final AxisDirection facing;

    @Getter
    @Accessors(fluent = true)
    @NotNull
    final Axis axis;

    @Getter
    @Accessors(fluent = true)
    @NotNull
    final Vector3i normal;

    @AllArgsConstructor
    public enum Axis {
        X(EnumFacing.Plane.HORIZONTAL), Y(EnumFacing.Plane.VERTICAL), Z(EnumFacing.Plane.HORIZONTAL);

        @Getter
        @Accessors(fluent = true)
        @NotNull
        final Plane plane;
    }

    public enum Plane {
        HORIZONTAL, VERTICAL;
    }

    public enum AxisDirection {
        POSITIVE, NEGATIVE
    }

    public EnumFacing opposite() {
        return switch (this) {
            case DOWN -> UP;
            case UP -> DOWN;
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
        };
    }

    public void removeFromPlane(Vector3f vector, @NotNull AABB aabb) {
        switch (this) {
            case DOWN -> vector.y = aabb.minY;
            case UP -> vector.y = aabb.maxY;
            case NORTH -> vector.z = aabb.minZ;
            case SOUTH -> vector.z = aabb.maxZ;
            case WEST -> vector.x = aabb.minX;
            case EAST -> vector.x = aabb.maxX;
        }
    }
    public void removeFromPlaneReversed(Vector3f vector, @NotNull AABB aabb) {
        switch (this) {
            case DOWN -> vector.y = aabb.maxY;
            case UP -> vector.y = aabb.minY;
            case NORTH -> vector.z = aabb.maxZ;
            case SOUTH -> vector.z = aabb.minZ;
            case WEST -> vector.x = aabb.maxX;
            case EAST -> vector.x = aabb.minX;
        }
    }

}
