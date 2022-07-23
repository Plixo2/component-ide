package de.plixo.systems;

import de.plixo.event.AssetServer;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.game.ArrayStorage3D;
import de.plixo.game.Block;
import de.plixo.rendering.BlockRenderer;
import de.plixo.rendering.Mesh;
import de.plixo.rendering.MeshBundle;
import de.plixo.state.Assets;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class WorldSystem {


    @Getter
    @Accessors(fluent = true)
    private int size;
//    @Getter
//    @Accessors(fluent = true)
//    private Storage3D<RenderEntry<?>> renderStorage;

//
//    private Shader cubeShader;
//
//    @Getter
//    @Accessors(fluent = true)
//
//    private MeshBundle cubeMesh;


    @SubscribeEvent
    void init(@NotNull PostInitEvent event) throws IOException {
        this.size = 10;
        AssetServer.insert(new Assets.RenderStorage(new ArrayStorage3D<>(size)));

        val inverseCube = MeshBundle.generate("background.obj", "background.mtl", "background_cube.toml",
                Mesh.GENERATE_COLLISION);
        AssetServer.insert(new Assets.BackgroundCube(inverseCube.first,inverseCube.second));

//        for (int x = 0; x < size; x++) {
//            for (int y = 0; y < size; y++) {
//                for (int z = 0; z < size; z++) {
//                    insert(new Pipe(),x,y,z);
//                }
//            }
//        }

    }

    @SubscribeEvent
    <T extends Block> void render(@NotNull Render3DEvent event) {
       AssetServer.get(Assets.BackgroundCube.class).render();
        final var renderStorage = AssetServer.get(Assets.RenderStorage.class).renderStorage();
        val projection = AssetServer.get(Assets.ProjView.class).matrix();
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    final Assets.RenderEntry<?> renderEntry = renderStorage.get(x, y, z);
                    if (renderEntry != null) {
                        final T block = (T) renderEntry.block();
                        final BlockRenderer<T> renderer = (BlockRenderer<T>) renderEntry.renderer();
                        renderer.draw(block, projection);
                    }
                }
            }
        }
    }







}
