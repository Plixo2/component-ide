package de.plixo.general.collision;

import de.plixo.general.Color;
import de.plixo.rendering.Debug;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.function.BiPredicate;

public class AABB {

    public final float minX;
    public final float minY;
    public final float minZ;
    public final float maxX;
    public final float maxY;
    public final float maxZ;

    public AABB(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    public AABB(@NotNull Vector3f a, @NotNull Vector3f b) {
        this(a.x, a.y, a.z, b.x, b.y, b.z);
    }

    public float width() {
        return maxX - minX;
    }

    public float height() {
        return maxY - minY;
    }

    public float depth() {
        return maxZ - minZ;
    }


    /**
     * from minecraft 1.8.8
     */
    public RayTraceResult calculateIntercept(@NotNull Vector3f vecA, @NotNull Vector3f vecB, boolean reverse) {
        var vec3 = getIntermediateWithXValue(vecB, vecA, this.minX);
        var vec31 = getIntermediateWithXValue(vecB, vecA, this.maxX);
        var vec32 = getIntermediateWithYValue(vecB, vecA, this.minY);
        var vec33 = getIntermediateWithYValue(vecB, vecA, this.maxY);
        var vec34 = getIntermediateWithZValue(vecB, vecA, this.minZ);
        var vec35 = getIntermediateWithZValue(vecB, vecA, this.maxZ);

        BiPredicate<Float, Float> comparator = (a, b) -> a < b;
        if (reverse) {
            comparator = (a, b) -> a > b;
        }

        if (this.isNotVecInYZ(vec3)) {
            vec3 = null;
        }

        if (this.isNotVecInYZ(vec31)) {
            vec31 = null;
        }

        if (this.isNotVecInXZ(vec32)) {
            vec32 = null;
        }

        if (this.isNotVecInXZ(vec33)) {
            vec33 = null;
        }

        if (this.isNotVecInXY(vec34)) {
            vec34 = null;
        }

        if (this.isNotVecInXY(vec35)) {
            vec35 = null;
        }

        Vector3f vec36 = null;

        if (vec3 != null) {
            vec36 = vec3;
        }

        if (vec31 != null && (vec36 == null || comparator.test(vecA.distanceSquared(vec31),
                vecA.distanceSquared(vec36)))) {
            vec36 = vec31;
        }

        if (vec32 != null && (vec36 == null || comparator.test(vecA.distanceSquared(vec32),
                vecA.distanceSquared(vec36)))) {
            vec36 = vec32;
        }

        if (vec33 != null && (vec36 == null || comparator.test(vecA.distanceSquared(vec33),
                vecA.distanceSquared(vec36)))) {
            vec36 = vec33;
        }

        if (vec34 != null && (vec36 == null || comparator.test(vecA.distanceSquared(vec34),
                vecA.distanceSquared(vec36)))) {
            vec36 = vec34;
        }

        if (vec35 != null && (vec36 == null || comparator.test(vecA.distanceSquared(vec35),
                vecA.distanceSquared(vec36)))) {
            vec36 = vec35;
        }

        if (vec36 == null) {
            return new RayTraceMiss();
        } else {
            EnumFacing enumfacing;
            if (vec36 == vec3) {
                enumfacing = EnumFacing.WEST;
            } else if (vec36 == vec31) {
                enumfacing = EnumFacing.EAST;
            } else if (vec36 == vec32) {
                enumfacing = EnumFacing.DOWN;
            } else if (vec36 == vec33) {
                enumfacing = EnumFacing.UP;
            } else if (vec36 == vec34) {
                enumfacing = EnumFacing.NORTH;
            } else {
                enumfacing = EnumFacing.SOUTH;
            }

            return new RayTraceHit(enumfacing, vec36, this);
        }
    }

    private boolean isNotVecInYZ(@Nullable Vector3f vec) {
        return vec == null || !(vec.y >= this.minY) || !(vec.y <= this.maxY) || !(vec.z >= this.minZ) || !(vec.z <= this.maxZ);
    }

    private boolean isNotVecInXZ(@Nullable Vector3f vec) {
        return vec == null || !(vec.x >= this.minX) || !(vec.x <= this.maxX) || !(vec.z >= this.minZ) || !(vec.z <= this.maxZ);
    }

    private boolean isNotVecInXY(@Nullable Vector3f vec) {
        return vec == null || !(vec.x >= this.minX) || !(vec.x <= this.maxX) || !(vec.y >= this.minY) || !(vec.y <= this.maxY);
    }

    public @Nullable Vector3f getIntermediateWithXValue(@NotNull Vector3f vec, @NotNull Vector3f this_, float x) {
        float d0 = vec.x - this_.x;
        float d1 = vec.y - this_.y;
        float d2 = vec.z - this_.z;

        if (d0 * d0 < 1.0000000116860974E-7D) {
            return null;
        } else {
            float d3 = (x - this_.x) / d0;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3f(this_.x + d0 * d3, this_.y + d1 * d3, this_.z + d2 * d3) :
                    null;
        }
    }

    public @Nullable Vector3f getIntermediateWithYValue(@NotNull Vector3f vec, @NotNull Vector3f this_, float y) {
        float d0 = vec.x - this_.x;
        float d1 = vec.y - this_.y;
        float d2 = vec.z - this_.z;

        if (d1 * d1 < 1.0000000116860974E-7D) {
            return null;
        } else {
            float d3 = (y - this_.y) / d1;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3f(this_.x + d0 * d3, this_.y + d1 * d3, this_.z + d2 * d3) :
                    null;
        }
    }

    public @Nullable Vector3f getIntermediateWithZValue(@NotNull Vector3f vec, @NotNull Vector3f this_, float z) {
        float d0 = vec.x - this_.x;
        float d1 = vec.y - this_.y;
        float d2 = vec.z - this_.z;

        if (d2 * d2 < 1.0000000116860974E-7D) {
            return null;
        } else {
            float d3 = (z - this_.z) / d2;
            return d3 >= 0.0D && d3 <= 1.0D ? new Vector3f(this_.x + d0 * d3, this_.y + d1 * d3, this_.z + d2 * d3) :
                    null;
        }
    }
    /*
     * end of minecraft code
     */

    public void draw() {
            Debug.offset = new Vector3f(minX, minY, minZ);
            Debug.drawLinedCube(new Vector3f(width(), height(), depth()), Color.YELLOW);
            Debug.offset = new Vector3f(0, 0, 0);
    }

    @Override
    public String toString() {
        return "AABB{" + "minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ + '}';
    }

    public AABB offset(float x, float y, float z)
    {
        return new AABB(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }
}

