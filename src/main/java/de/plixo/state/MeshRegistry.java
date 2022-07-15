package de.plixo.state;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.game.AtlasGen;
import de.plixo.game.BlockMesh;
import de.plixo.game.impl.Wool;
import de.plixo.general.Tuple;
import de.plixo.rendering.Mesh;
import de.plixo.rendering.Texture;
import de.plixo.rendering.blockrendering.BlockRenderer;
import de.plixo.rendering.blockrendering.blocks.SimpleRenderer;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MeshRegistry {
    private static final HashMap<Class<?>, BlockMesh> registeredMeshes = new HashMap<>();
    private static final HashMap<Class<?>, BlockRenderer<?>> registeredRenderers = new HashMap<>();

    private static final ArrayList<BufferedImage> atlasTextures = new ArrayList<>();

    private static Tuple<HashMap<BufferedImage, Vector2i>, Texture> atlasEntries;
/*

    static float vertices[] = {
            0, 0, 0, 1.0f, 0.0f, 0.9f,
            0, 0, 1, 0.0f, 0.0f, 0.9f,
            0, 1, 1, 0.0f, 1.0f, 0.9f,
            0, 1, 0, 1.0f, 1.0f, 0.9f,

            1, 0, 0, 1.0f, 0.0f, 0.7f,
            1, 0, 1, 0.0f, 0.0f, 0.7f,
            1, 1, 1, 0.0f, 1.0f, 0.7f,
            1, 1, 0, 1.0f, 1.0f, 0.7f,

            0, 1, 0, 1.0f, 0.0f, 0.9f,
            1, 1, 0, 0.0f, 0.0f, 0.9f,
            1, 1, 1, 0.0f, 1.0f, 0.9f,
            0, 1, 1, 1.0f, 1.0f, 0.9f,

            0, 0, 0, 1.0f, 0.0f, 0.8f,
            1, 0, 0, 0.0f, 0.0f, 0.8f,
            1, 0, 1, 0.0f, 1.0f, 0.8f,
            0, 0, 1, 1.0f, 1.0f, 0.8f,

            0, 0, 1, 1.0f, 0.0f, 0.95f,
            1, 0, 1, 0.0f, 0.0f, 0.95f,
            1, 1, 1, 0.0f, 1.0f, 0.95f,
            0, 1, 1, 1.0f, 1.0f, 0.95f,

            0, 0, 0, 1.0f, 0.0f, 0.75f,
            1, 0, 0, 0.0f, 0.0f, 0.75f,
            1, 1, 0, 0.0f, 1.0f, 0.75f,
            0, 1, 0, 1.0f, 1.0f, 0.75f,

    };
    static int indices[] = {
            0, 1, 3,
            1, 2, 3,

            7, 5, 4,
            7, 6, 5,

            11, 9, 8,
            11, 10, 9,

            12, 13, 15,
            13, 14, 15,

            16, 17, 19,
            17, 18, 19,

            23, 21, 20,
            23, 22, 21,
    };
*/

    @SubscribeEvent
    public static void register(@NotNull PostInitEvent event) throws IOException {
        final var value = BlockMesh.generate("cube.obj", "cube.mtl", "obj.toml", 0);
        registeredMeshes.put(Wool.class, value.first);
        registeredRenderers.put(Wool.class, new SimpleRenderer(value.second));
    }

    private static float[] injectUVs(float @NotNull [] uvs, int base, int offset,
                                     @NotNull AtlasGen.AtlasEntry entry) {
        final float[] list = Arrays.copyOf(uvs, uvs.length);
        for (int i = base; i < list.length; i += offset + 2) {
            val x = list[i];
            val y = list[i + 1];
            list[i] = 1 - (entry.x() + ((1 - x) * entry.width()));
            list[i + 1] = 1 - (entry.y() + ((1 - y) * entry.height()));
        }

        return list;
    }

    public static @NotNull BlockMesh meshByClass(Class<?> cls) {
        final BlockMesh blockMesh = registeredMeshes.get(cls);
        if (blockMesh == null) {
            throw new RuntimeException("unknown mesh for class " + cls);
        }
        return blockMesh;
    }

    public static @NotNull BlockRenderer<?> rendererByClass(Class<?> cls) {
        final BlockRenderer<?> renderer = registeredRenderers.get(cls);
        if (renderer == null) {
            throw new RuntimeException("unknown mesh for class " + cls);
        }
        return renderer;
    }

/*    private static void generateAtlas() {
        val generate = AtlasGen.generate(atlasTextures, 64);
        atlasEntries = new Tuple<>();
        atlasEntries.first = generate.first;
        atlasEntries.second = Texture.fromBufferedImg(generate.second, new Texture.ImgConfig(true, false, false));

        try {
            ImageIO.write(generate.second, "png", new File("content/atlas.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static @NotNull AtlasGen.AtlasEntry atlasEntry(@NotNull BufferedImage img) {
        val pos = atlasEntries.first.get(img);
        if (pos == null) {
            throw new RuntimeException("Unknown Atlas Image");
        }
        final float atlas_size = atlasEntries.second.width();
        return new AtlasGen.AtlasEntry(
                atlasEntries.second,
                pos.x / atlas_size,
                pos.y / atlas_size,
                img.getWidth() / atlas_size,
                img.getHeight() / atlas_size);

    }*/
}
