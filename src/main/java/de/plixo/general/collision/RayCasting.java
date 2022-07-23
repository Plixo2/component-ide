package de.plixo.general.collision;

import de.plixo.game.Block;
import de.plixo.game.Storage3D;
import de.plixo.general.IO;
import de.plixo.general.Tuple;
import de.plixo.general.Util;
import de.plixo.rendering.MeshBundle;
import de.plixo.rendering.RenderObj;
import de.plixo.state.Assets;
import de.plixo.event.AssetServer;
import de.plixo.systems.RenderSystem;
import lombok.val;
import org.jetbrains.annotations.NotNull;
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
            val aabb = collision.boundingBox().offset(offset.x, offset.y, offset.z);
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

    public static Tuple<Block, @NotNull RayTraceResult> rayCastStorage(Storage3D<Assets.RenderEntry<?>> renderStorage
            , @NotNull Vector3f origin, @NotNull Vector3f end) {
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
                        final Block block_ = renderEntry.block();
                        val aabb = block_.aabb();
                        val rayTraceResult = aabb.calculateIntercept(origin, end, false);
                        if (rayTraceResult instanceof RayTraceHit hit) {
                            if (hit.position().distanceSquared(origin) > minDst) {
                                continue;
                            }
                            val subMesh = rayCastBlockAABBS(block_.getCollisions(),
                                    new Vector3f(block_.veci()).add(0.5f, 0.5f, 0.5f),
                                    origin, end, false);
                            if (subMesh instanceof RayTraceHit hit2) {
                                val distance = hit2.position().distanceSquared(origin);
                                if (distance < minDst) {
                                    minDst = distance;
                                    best = hit2;
                                    block = block_;
                                }
                            }
                        }
                    }
                }
            }
        }

        return new Tuple<>(block, best);
    }

    public static RayTraceResult rayCastBlockAABBS(@NotNull AABB[] meshes, @NotNull Vector3f offset,
                                                   @NotNull Vector3f origin, @NotNull Vector3f end, boolean reverse) {
        RayTraceResult best = new RayTraceMiss();
        var minDst = Float.MAX_VALUE;

        for (AABB aabb_ : meshes) {
            val aabb = aabb_.offset(offset.x, offset.y, offset.z);
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

    public static RayTraceResult rayCastWorld(@NotNull Vector3f origin, @NotNull Vector3f end) {
        final var cube = AssetServer.get(Assets.BackgroundCube.class).bundle();
        val reversed = RayCasting.rayCastBlockMesh(cube, new Vector3f(), origin, end, true);
        val start = RayCasting.rayCastBlockMesh(cube, new Vector3f(), origin, end, false);

        EnumFacing face = null;
        Vector3f position = null;
        Block block = null;

        final var renderStorage = AssetServer.get(Assets.RenderStorage.class).renderStorage();
        if (start instanceof RayTraceHit start_hit) {

            assert reversed instanceof RayTraceHit;
            val end_hit = (RayTraceHit) reversed;
            val end_position = end_hit.position();

            face = end_hit.face();
            position = new Vector3f(end_position);
            face.removeFromPlaneReversed(position, start_hit.collider());
            val rayCast = RayCasting.rayCastStorage(renderStorage, origin, end_position);
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
//        val vec4 = new Vector4f(position, 1.0f);
//        final Vector4f mul = vec4.mul(RenderSystem.INSTANCE.projView());
//
//        val x = mul.x / mul.w;
//        var y = mul.y / mul.w;
//        y *= -1;
//        //* RenderSystem.UI_SCALE
//        final float screen_x = (RenderSystem.INSTANCE.worldCanvas().width / 2.0f) + x * (RenderSystem.INSTANCE
//        .worldCanvas().width / 2.0f);
//        final float screen_y = (RenderSystem.INSTANCE.worldCanvas().height / 2.0f) + y * (RenderSystem.INSTANCE
//        .worldCanvas().height / 2.0f);
//        return new Vector2f(screen_x, screen_y);
        return null;
    }

    public static Tuple<Vector3f, Vector3f> screenToWorld(float x, float y) {
        final var worldCanvas = AssetServer.get(Assets.WorldCanvas.class).worldCanvas();
        final var matrix = AssetServer.get(Assets.ProjView.class).matrix();
        val xNDC = (2 * (x / (worldCanvas.width * RenderSystem.UI_SCALE)) - 1f);
        val yNDC = (-2 * (y / (worldCanvas.height * RenderSystem.UI_SCALE)) + 1f);
        val camera_inverse_matrix = (new Matrix4f(matrix)).invert();
        val near = (new Vector4f(xNDC, yNDC, 0, 1)).mul(camera_inverse_matrix);
        val far = (new Vector4f(xNDC, yNDC, 1, 1)).mul(camera_inverse_matrix);

        val near_ = new Vector3f(near.x, near.y, near.z).div(near.w);
        val far_ = new Vector3f(far.x, far.y, far.z).div(far.w);
        val dir = far_.sub(near_);
        val origin = new Vector3f(near_.x, near_.y, near_.z);

        return new Tuple<>(origin, dir.normalize());
    }


    public static RayTraceResult raycast_camera() {
        val dir_ = RayCasting.screenToWorld(IO.getCanvasMouse().x, IO.getCanvasMouse().y);
        val forward = new Vector3f(dir_.first).add(new Vector3f(dir_.second).mul(1000f));
        return RayCasting.rayCastWorld(dir_.first, forward);
    }
}
