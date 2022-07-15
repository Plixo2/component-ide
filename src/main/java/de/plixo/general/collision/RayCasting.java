package de.plixo.general.collision;

import de.plixo.game.Block;
import de.plixo.game.BlockMesh;
import de.plixo.game.RenderObj;
import de.plixo.game.Storage3D;
import de.plixo.general.Tuple;
import de.plixo.general.Util;
import de.plixo.rendering.Debug;
import de.plixo.state.World;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class RayCasting {


    public static RayTraceResult rayCastBlockMesh(@NotNull BlockMesh mesh, @NotNull Vector3f origin,
                                                  @NotNull Vector3f end,
                                                  boolean reverse) {
        RayTraceResult best = new RayTraceMiss();
        var minDst = Float.MAX_VALUE;

        for (RenderObj obj : mesh.objs()) {
            val collision = obj.mesh().collision();
            if (collision == null)
                continue;
            val aabb = collision.boundingBox();
            val rayTraceResult = aabb.calculateIntercept(origin, end, reverse);
            if (rayTraceResult instanceof RayTraceHit hit) {
                val distance = hit.position().distanceSquared(origin);
                if (distance < minDst) {
                    minDst = distance;
                    best = hit;
                }
            }
        }
        if (reverse && best instanceof RayTraceHit hit) {
            return new RayTraceHit(hit.facing().opposite(), hit.position(), hit.collider());
        }

        return best;
    }

    public static Tuple<@Nullable Block, @NotNull RayTraceResult> rayCastStorage(Storage3D<World.RenderEntry<?>> renderStorage,
                                                                                 @NotNull Vector3f origin,
                                                                                 @NotNull Vector3f end) {
        val bounds = new AABB(origin, end);
        val minX = Util.clamp((int) (bounds.minX - 1), renderStorage.size(), 0);
        val minY = Util.clamp((int) (bounds.minY - 1), renderStorage.size(), 0);
        val minZ = Util.clamp((int) (bounds.minZ - 1), renderStorage.size(), 0);
        val maxX = Util.clamp((int) (bounds.maxX + 1), renderStorage.size(), 0);
        val maxY = Util.clamp((int) (bounds.maxY + 1), renderStorage.size(), 0);
        val maxZ = Util.clamp((int) (bounds.maxZ + 1), renderStorage.size(), 0);

        RayTraceResult best = new RayTraceMiss();
        Block block = null;
        var minDst = Float.MAX_VALUE;

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    val renderEntry = renderStorage.get(x, y, z);
                    if (renderEntry != null) {
                        val aabb = renderEntry.block().aabb();
                        final var rayTraceResult = aabb.calculateIntercept(origin, end, false);
                        if (rayTraceResult instanceof RayTraceHit hit) {
                            val distance = hit.position().distanceSquared(origin);
                            if (distance < minDst) {
                                minDst = distance;
                                best = hit;
                            }
                        }
                        block = renderEntry.block();
                    }
                }
            }
        }

        return new Tuple<>(block, best);
    }

    public static Tuple<@Nullable Tuple<@NotNull EnumFacing, @NotNull Vector3f>, @Nullable Block> rayCastWorld(@NotNull Vector3f origin, @NotNull Vector3f end) {
        val reversed = RayCasting.rayCastBlockMesh(World.INSTANCE.cubeMesh(), origin, end, true);
        val start = RayCasting.rayCastBlockMesh(World.INSTANCE.cubeMesh(), origin, end, false);

        EnumFacing face = null;
        Vector3f position = null;
        Block block = null;

        if (start instanceof RayTraceHit start_hit) {
            assert reversed instanceof RayTraceHit;
            val end_hit = (RayTraceHit) reversed;
            val end_position = end_hit.position();

            face = end_hit.facing();
            position = new Vector3f(end_position);
            face.removeFromPlaneReversed(position, start_hit.collider());

            final var rayCast = RayCasting.rayCastStorage(World.INSTANCE.renderStorage(), origin, end_position);
            if (rayCast.second instanceof RayTraceHit blockHit) {
                assert rayCast.first != null;
                face = blockHit.facing();
                val hit_position = blockHit.position();
                position = new Vector3f(hit_position.x(), hit_position.y(), hit_position.z());
                face.removeFromPlane(position, blockHit.collider());
                block = rayCast.first;
            }
        }
        if (face == null) {
            return new Tuple<>(null, null);
        }
        return new Tuple<>(new Tuple<>(face, position), block);
    }

}
