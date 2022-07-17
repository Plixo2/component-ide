package de.plixo.game.collision;

import de.plixo.game.Block;
import de.plixo.rendering.MeshBundle;
import de.plixo.game.RenderObj;
import de.plixo.game.Storage3D;
import de.plixo.general.Tuple;
import de.plixo.general.Util;
import de.plixo.systems.RenderSystem;
import de.plixo.systems.WorldSystem;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class RayCasting {


    public static RayTraceResult rayCastBlockMesh(@NotNull MeshBundle mesh, @NotNull Vector3f offset,
                                                  @NotNull Vector3f origin, @NotNull Vector3f end, boolean reverse) {
        RayTraceResult best = new RayTraceMiss();
        var minDst = Float.MAX_VALUE;

        for (RenderObj obj : mesh.objs()) {
            val collision = obj.mesh().collision();
            if (collision == null)
                continue;
            val aabb = collision.boundingBox().offset(offset.x,offset.y,offset.z);
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
            return new RayTraceHit(hit.face().opposite(), hit.position(), hit.collider());
        }

        return best;
    }

    public static Tuple<WorldSystem.@Nullable RenderEntry<?>, @NotNull RayTraceResult> rayCastStorage(Storage3D<WorldSystem.RenderEntry<?>> renderStorage, @NotNull Vector3f origin, @NotNull Vector3f end) {
        val bounds = new AABB(origin, end);
        val minX = Util.clamp((int) (bounds.minX - 1), renderStorage.size(), 0);
        val minY = Util.clamp((int) (bounds.minY - 1), renderStorage.size(), 0);
        val minZ = Util.clamp((int) (bounds.minZ - 1), renderStorage.size(), 0);
        val maxX = Util.clamp((int) (bounds.maxX + 1), renderStorage.size(), 0);
        val maxY = Util.clamp((int) (bounds.maxY + 1), renderStorage.size(), 0);
        val maxZ = Util.clamp((int) (bounds.maxZ + 1), renderStorage.size(), 0);

        RayTraceResult best = new RayTraceMiss();
        WorldSystem.RenderEntry<?> block = null;
        var minDst = Float.MAX_VALUE;


        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    val renderEntry = renderStorage.get(x, y, z);
                    if (renderEntry != null) {
                        final Block block_ = renderEntry.block();
                        val aabb = block_.aabb();
                        final var rayTraceResult = aabb.calculateIntercept(origin, end, false);
                        if (rayTraceResult instanceof RayTraceHit hit) {
                            if(hit.position().distanceSquared(origin) > minDst) {
                               continue;
                            }
                            final var subMesh = rayCastBlockMesh(renderEntry.mesh(),
                                    new Vector3f(block_.veci()).add(0.5f,0.5f,0.5f),
                                    origin, end, false);
                            if (subMesh instanceof RayTraceHit hit2) {
                                val distance = hit2.position().distanceSquared(origin);
                                if (distance < minDst) {
                                    minDst = distance;
                                    best = hit2;
                                    block = renderEntry;
                                }
                            }
                        }
                    }
                }
            }
        }

        return new Tuple<>(block, best);
    }

    public static RayTraceResult rayCastWorld(@NotNull Vector3f origin, @NotNull Vector3f end) {
        val reversed = RayCasting.rayCastBlockMesh(WorldSystem.INSTANCE.cubeMesh(), new Vector3f(), origin, end, true);
        val start = RayCasting.rayCastBlockMesh(WorldSystem.INSTANCE.cubeMesh(), new Vector3f(), origin, end, false);

        EnumFacing face = null;
        Vector3f position = null;
        WorldSystem.RenderEntry<?> block = null;

        if (start instanceof RayTraceHit start_hit) {

            assert reversed instanceof RayTraceHit;
            val end_hit = (RayTraceHit) reversed;
            val end_position = end_hit.position();

            face = end_hit.face();
            position = new Vector3f(end_position);
            face.removeFromPlaneReversed(position, start_hit.collider());

            final var rayCast = RayCasting.rayCastStorage(WorldSystem.INSTANCE.renderStorage(), origin, end_position);
            if (rayCast.second instanceof RayTraceHit blockHit) {
                assert rayCast.first != null;
                face = blockHit.face();
                val hit_position = blockHit.position();
                position = new Vector3f(hit_position.x(), hit_position.y(), hit_position.z());
                face.removeFromPlane(position, blockHit.collider());
                block = rayCast.first;
            }
        }
        if (face == null) {
            return new RayTraceMiss();
        }
        if (block == null) {
            return new RayTraceHit(face, position, ((RayTraceHit) start).collider());
        }
        return new RayTraceBlockHit(face, position, block);
    }

    public static Vector2f worldToScreen(Vector3f position) {
        val vec4 = new Vector4f(position, 1.0f);
        final Vector4f mul = vec4.mul(RenderSystem.INSTANCE.projView());

        val x = mul.x / mul.w;
        var y = mul.y / mul.w;
        y *= -1;
        final float screen_x = (RenderSystem.INSTANCE.width() / 2.0f) + x * (RenderSystem.INSTANCE.width() / 2.0f);
        final float screen_y = (RenderSystem.INSTANCE.height() / 2.0f) + y * (RenderSystem.INSTANCE.height() / 2.0f);
        return new Vector2f(screen_x, screen_y);
    }

    public static Tuple<Vector3f, Vector3f> screenToWorld(float x, float y) {
        val xNDC = (2 * (x / RenderSystem.INSTANCE.width()) - 1f);
        val yNDC = (-2 * (y / RenderSystem.INSTANCE.height()) + 1f);
        val camera_inverse_matrix = (new Matrix4f(RenderSystem.INSTANCE.projView())).invert();
        val near = (new Vector4f(xNDC, yNDC, 0, 1)).mul(camera_inverse_matrix);
        val far = (new Vector4f(xNDC, yNDC, 1, 1)).mul(camera_inverse_matrix);

        val near_ = new Vector3f(near.x, near.y, near.z).div(near.w);
        val far_ = new Vector3f(far.x, far.y, far.z).div(far.w);
        val dir = far_.sub(near_);
        val origin = new Vector3f(near_.x, near_.y, near_.z);

        return new Tuple<>(origin, dir.normalize());
    }


}
