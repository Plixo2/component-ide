package de.plixo.systems;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.game.ArrayStorage3D;
import de.plixo.game.Block;
import de.plixo.game.Storage3D;
import de.plixo.game.blocks.Wool;
import de.plixo.rendering.Mesh;
import de.plixo.rendering.MeshBundle;
import de.plixo.rendering.blockrendering.BlockRenderer;
import de.plixo.rendering.targets.Shader;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class WorldSystem implements Storage3D<Block> {
    public static WorldSystem INSTANCE;


    @Getter
    @Accessors(fluent = true)
    private int size;
    @Getter
    @Accessors(fluent = true)
    private Storage3D<RenderEntry<?>> renderStorage;


    private Shader cubeShader;

    @Getter
    @Accessors(fluent = true)

    private MeshBundle cubeMesh;

    Shader.Uniform projview;
    Shader.Uniform model;

    @SubscribeEvent
    void init(@NotNull PostInitEvent event) throws IOException {
        this.size = 10;
        renderStorage = new ArrayStorage3D<>(size);
        val inverseCube = MeshBundle.generate("background.obj", "background.mtl", "obj.toml", Mesh.GENERATE_COLLISION);
        cubeMesh = inverseCube.first;
        cubeShader = inverseCube.second;

        projview = cubeShader.uniform("projview");
        model = cubeShader.uniform("model");
    }

    @SubscribeEvent
    <T extends Block> void render(@NotNull Render3DEvent event) {
        projview.loadProjView();
        model.loadIdentity();
        cubeShader.flush();
        cubeMesh.render();


        val projection = RenderSystem.INSTANCE.projView();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    final RenderEntry<?> renderEntry = renderStorage.get(x, y, z);
                    if (renderEntry != null) {
                        final T block = (T) renderEntry.block;
                        final BlockRenderer<T> renderer = (BlockRenderer<T>) renderEntry.renderer;
                        renderer.draw(block, renderEntry.mesh, projection);
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
    public boolean insert(@Nullable Block obj, int x, int y, int z) {
        if (obj == null) {
            return renderStorage.insert(null, x, y, z);
        } else {
            final MeshBundle mesh = MeshSystem.meshByClass(obj.getClass());
            final BlockRenderer<?> renderer = MeshSystem.rendererByClass(obj.getClass());
            obj.setPosition(x, y, z);
            return renderStorage.insert(new RenderEntry(obj, mesh, renderer), x, y, z);
        }
    }


    public record RenderEntry<T extends Block>(@NotNull T block, @NotNull MeshBundle mesh,
                                               @NotNull BlockRenderer<T> renderer) {

    }
}
