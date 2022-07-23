package de.plixo.game;

import de.plixo.game.item.Material;
import de.plixo.general.collision.AABB;
import de.plixo.general.collision.EnumFacing;
import de.plixo.rendering.MeshTexture;
import de.plixo.rendering.targets.Shader;
import de.plixo.state.Assets;
import de.plixo.event.AssetServer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Accessors(fluent = true)
public abstract class Block {

    public static List<TRunnable> inits = new ArrayList<>();

    public interface TRunnable {
        void run() throws IOException;
    }

    public static void init(TRunnable runnable) {
        inits.add(runnable);
    }

    public static Shader defaultBlockShader;
    public static Shader.Uniform defaultProjView;
    public static Shader.Uniform defaultModel;

    static {
        init(() -> {
            defaultBlockShader = Shader.fromDSA("light_obj.toml");
            defaultModel = defaultBlockShader.uniform("model");
            defaultProjView = defaultBlockShader.uniform("projview");
            defaultBlockShader.seal();
        });
    }

    Material material;

    EnumFacing orientation;


    private int x = -1;
    private int y = -1;
    private int z = -1;
    private @NotNull AABB aabb = new AABB(0, 0, 0, 0, 0, 0);

    public void setPosition(int x, int y, int z) {
        if (this.x != -1 || this.y != -1 || this.z != -1) {
            throw new RuntimeException("Block position was already set");
        }
        this.x = x;
        this.y = y;
        this.z = z;
        this.aabb = new AABB(x, y, z, x + 1, y + 1, z + 1);
    }

    public boolean interact() {
        return false;
    }

    public void onNeighborUpdate() {

    }

    public abstract @NotNull Shader getShader();

    public abstract @NotNull MeshTexture[] getMeshes();

    public AABB[] getCollisions() {
        final var meshes = getMeshes();
        AABB[] collisions = new AABB[meshes.length];
        var index = 0;
        for (MeshTexture mesh : meshes) {
            final var collision = mesh.mesh().collision();
            if (collision != null)
                collisions[index++] = collision.boundingBox();
        }
        return collisions;
    }

    public void onUpdate() {

    }


    public void onDraw() {

    }

    public @Nullable Block get(@NotNull EnumFacing facing) {
        final var normal = facing.normal();
        final var worldSystem = AssetServer.get(Assets.RenderStorage.class);
        return worldSystem.get(x + normal.x, y + normal.y, z + normal.z);
    }


    public Vector3i veci() {
        return new Vector3i(x, y, z);
    }


}
