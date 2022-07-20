package de.plixo.systems;

import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.game.AtlasGen;
import de.plixo.impl.block.Chest;
import de.plixo.impl.render.InventoryBlockRenderer;
import de.plixo.rendering.MeshBundle;
import de.plixo.impl.block.Wool;
import de.plixo.rendering.targets.Texture;
import de.plixo.rendering.BlockRenderer;
import de.plixo.impl.render.SimpleRenderer;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MeshSystem {
    @Getter
    @Accessors(fluent = true)
    private static final HashMap<Class<?>, MeshBundle> registeredMeshes = new HashMap<>();

    @Getter
    @Accessors(fluent = true)
    private static final HashMap<Class<?>, BlockRenderer<?>> registeredRenderers = new HashMap<>();



//    private static final ArrayList<BufferedImage> atlasTextures = new ArrayList<>();
//
//    private static Tuple<HashMap<BufferedImage, Vector2i>, Texture> atlasEntries;


    private static Texture itemTexture;

    @SubscribeEvent
    public static void register(@NotNull PostInitEvent event) throws IOException {
        val pipe = MeshBundle.generate("pipe.obj", "pipe.mtl", "light_obj.toml", 0);
        registeredMeshes.put(Wool.class, pipe.first);
        registeredRenderers.put(Wool.class, new SimpleRenderer(pipe.second));
        val chest = MeshBundle.generate("container.obj", "container.mtl", "light_obj.toml", 0);
        registeredMeshes.put(Chest.class, chest.first);
        registeredRenderers.put(Chest.class, new InventoryBlockRenderer());


        final var file = new File("content/textures/items");
        final var files = file.listFiles();
        assert files != null;
        List<BufferedImage> images = new ArrayList<>();
        for (File item : files) {
            final var read = ImageIO.read(item);
            images.add(read);
        }
        final var generated = AtlasGen.generate(images, 128,false);
        ImageIO.write(generated.second,"png",new File("content/item-atlas.png"));

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

    public static @NotNull MeshBundle meshByClass(Class<?> cls) {
        final MeshBundle meshBundle = registeredMeshes.get(cls);
        if (meshBundle == null) {
            throw new RuntimeException("unknown mesh for class " + cls);
        }
        return meshBundle;
    }

    public static @NotNull BlockRenderer<?> rendererByClass(Class<?> cls) {
        final BlockRenderer<?> renderer = registeredRenderers.get(cls);
        if (renderer == null) {
            throw new RuntimeException("unknown mesh for class " + cls);
        }
        return renderer;
    }/*

    private static void generateAtlas() {
        val generate = AtlasGen.generate(atlasTextures, 64);
        atlasEntries = new Tuple<>();
        atlasEntries.first = generate.first;
        atlasEntries.second = Texture.fromBufferedImg(generate.second);

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
