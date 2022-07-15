package de.plixo.state;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseClickEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.game.ArrayStorage3D;
import de.plixo.game.Block;
import de.plixo.game.BlockMesh;
import de.plixo.game.Storage3D;
import de.plixo.game.impl.Wool;
import de.plixo.general.Tuple;
import de.plixo.general.collision.EnumFacing;
import de.plixo.general.collision.RayCasting;
import de.plixo.general.collision.RayTraceHit;
import de.plixo.rendering.Debug;
import de.plixo.rendering.Mesh;
import de.plixo.rendering.Shader;
import de.plixo.rendering.blockrendering.BlockRenderer;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.IOException;

public class World implements Storage3D<Block> {
    public static World INSTANCE = new World();


    @Getter
    @Accessors(fluent = true)
    private int size;
    @Getter
    @Accessors(fluent = true)
    private Storage3D<RenderEntry<?>> renderStorage;


    private Shader cubeShader;

    @Getter
    @Accessors(fluent = true)

    private BlockMesh cubeMesh;

    @SubscribeEvent
    void init(@NotNull PostInitEvent event) throws IOException {
        this.size = 10;
        renderStorage = new ArrayStorage3D<>(size);
        val inverseCube = BlockMesh.generate("background.obj", "background.mtl", "obj.toml", Mesh.GENERATE_COLLISION);
        cubeMesh = inverseCube.first;
        cubeShader = inverseCube.second;
        insert(new Wool(), 5, 5, 5);
    }

    @SubscribeEvent
    void click(@NotNull MouseClickEvent event) {

    }

    @Nullable Block hoveredBlock;
    @Nullable EnumFacing face;
    @Nullable Vector3f hoveredPosition;


    @SubscribeEvent
    <T extends Block> void render(@NotNull Render3DEvent event) {

        val dir_ = Window.INSTANCE.screenToWorld(IO.getMouse().x, IO.getMouse().y);
        val forward = new Vector3f(dir_.first).add(new Vector3f(dir_.second).mul(1000f));
        final var result = RayCasting.rayCastWorld(dir_.first, forward);
        if(result.first != null) {
            face = result.first.first;
            hoveredPosition = result.first.second;
        } else {
            face = null;
            hoveredPosition = null;
        }
        hoveredBlock = result.second;


        final var projview = Window.INSTANCE.projview();
        cubeShader.uniform("projview").load(projview);
        cubeShader.uniform("model").load(new Matrix4f());
        cubeShader.flush();
        cubeMesh.render();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    final RenderEntry<?> renderEntry = renderStorage.get(x, y, z);
                    if (renderEntry != null) {
                        final T block = (T) renderEntry.block;
                        final BlockRenderer<T> renderer = (BlockRenderer<T>) renderEntry.renderer;
                        renderer.draw(block, renderEntry.mesh, projview);
                    }
                }
            }
        }
    }


    @Override
    public @Nullable Block get(int x, int y, int z) {
        final RenderEntry<?> renderEntry = renderStorage.get(x, y, z);
        if (renderEntry == null) {
            return null;
        }
        return renderEntry.block();
    }


    @Override
    public void insert(@Nullable Block obj, int x, int y, int z) {
        if (obj == null) {
            renderStorage.insert(null, x, y, z);
        } else {
            final BlockMesh mesh = MeshRegistry.meshByClass(obj.getClass());
            final BlockRenderer<?> renderer = MeshRegistry.rendererByClass(obj.getClass());
            obj.setPosition(x, y, z);
            renderStorage.insert(new RenderEntry(obj, mesh, renderer), x, y, z);
        }
    }


    public record RenderEntry<T extends Block>(@NotNull T block, @NotNull BlockMesh mesh,
                                               @NotNull BlockRenderer<T> renderer) {

    }
}
