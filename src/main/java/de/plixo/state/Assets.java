package de.plixo.state;

import de.plixo.game.Block;
import de.plixo.game.Storage3D;
import de.plixo.rendering.BlockRenderer;
import de.plixo.rendering.MeshBundle;
import de.plixo.rendering.targets.Shader;
import de.plixo.systems.MeshSystem;
import de.plixo.ui.lib.elements.layout.UICanvas;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

public class Assets {


    public record BackgroundCube(@NotNull MeshBundle bundle, @NotNull Shader shader) {
        public void render() {
            shader.uniform("model").loadIdentity();
            shader.uniform("projview").loadProjView();
            shader.flush();
            bundle.render();
        }
    }

    public record ProjView(Matrix4f matrix) {
    }
    public record RenderStorage(Storage3D<RenderEntry<?>> renderStorage) implements Storage3D<Block> {

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
                final BlockRenderer<?> renderer = MeshSystem.rendererByClass(obj.getClass());
                obj.setPosition(x, y, z);
                final var insert = renderStorage.insert(new RenderEntry(obj, renderer), x, y, z);
                updateNeighbors(x, y, z);
                obj.onNeighborUpdate();
                return insert;
            }
        }

        @Override
        public int size() {
            return renderStorage.size();
        }

        private void updateNeighbors(int x, int y, int z) {
            var block = get(x - 1, y, z);
            if (block != null) {
                block.onNeighborUpdate();
            }
            block = get(x + 1, y, z);
            if (block != null) {
                block.onNeighborUpdate();
            }
            block = get(x, y - 1, z);
            if (block != null) {
                block.onNeighborUpdate();
            }
            block = get(x, y + 1, z);
            if (block != null) {
                block.onNeighborUpdate();
            }
            block = get(x, y, z - 1);
            if (block != null) {
                block.onNeighborUpdate();
            }
            block = get(x, y, z + 1);
            if (block != null) {
                block.onNeighborUpdate();
            }
        }
    }

    public record RenderEntry<T extends Block>(@NotNull T block, @NotNull BlockRenderer<T> renderer) {
    }

    public record WorldCanvas(@NotNull UICanvas worldCanvas) {

    }

    public record Window(long id) {

    }
}
