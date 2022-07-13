package de.plixo.state;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.MouseClickEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.event.impl.Render3DEvent;
import de.plixo.game.*;
import de.plixo.game.impl.Simple;
import de.plixo.game.impl.Wool;
import de.plixo.rendering.blockrendering.BlockRenderer;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class World implements Storage3D<Block> {
    public static World INSTANCE = new World();


    @Getter
    @Accessors(fluent = true)
    private int size;
    @Getter
    @Accessors(fluent = true)
    private Storage3D<RenderEntry<?>> renderStorage;

    @SubscribeEvent
    void init(@NotNull PostInitEvent event) {
        this.size = 10;
        renderStorage = new ArrayStorage3D<>(size);
    }

    @SubscribeEvent
    void click(@NotNull MouseClickEvent event) {
        if (event.button() == 0) {
//            insert(new Simple(), 5, 5, 5);
            insert(new Wool(), 5, 5, 5);
        }
    }

    @SubscribeEvent
    <T extends Block> void render(@NotNull Render3DEvent event) {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                for (int z = 0; z < size; z++) {
                    final RenderEntry<?> renderEntry = renderStorage.get(x, y, z);
                    if (renderEntry != null) {
                        final T block = (T) renderEntry.block;
                        final BlockRenderer<T> renderer = (BlockRenderer<T>) renderEntry.renderer;
                        renderer.draw(block, renderEntry.mesh);
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
            obj.x = x;
            obj.y = y;
            obj.z = z;
            final BlockMesh mesh = MeshRegistry.meshByClass(obj.getClass());
            final BlockRenderer<?> renderer = MeshRegistry.rendererByClass(obj.getClass());
            renderStorage.insert(new RenderEntry(obj, mesh, renderer), x, y, z);
        }
    }

    record RenderEntry<T extends Block>(@NotNull T block, @NotNull BlockMesh mesh, @NotNull BlockRenderer<T> renderer) {

    }
}
