package de.plixo.game;

import de.javagl.obj.*;
import de.plixo.event.SubscribeEvent;
import de.plixo.event.impl.PostInitEvent;
import de.plixo.game.impl.Wool;
import de.plixo.general.Color;
import de.plixo.general.Tuple;
import de.plixo.general.dsa.Dsa;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MeshRegistry {
    private static final HashMap<Class<?>, BlockMesh> registeredMeshes = new HashMap<>();
    private static final HashMap<Class<?>, BlockRenderer<?>> registeredRenderers = new HashMap<>();

    private static final ArrayList<BufferedImage> atlasTextures = new ArrayList<>();

    private static Tuple<HashMap<BufferedImage, Vector2i>, Texture> atlasEntries;

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


    private static Mesh block;


    @SubscribeEvent
    public static void register(@NotNull PostInitEvent event) throws IOException {

        BufferedImage simple = ImageIO.read(new File("content/textures/exp.png"));
        atlasTextures.add(simple);
        BufferedImage test0 = ImageIO.read(new File("content/packtest/beehive_front_honey.png"));
        atlasTextures.add(test0);

        generateAtlas();
        final AtlasGen.AtlasEntry simpleEntry = atlasEntry(simple);
        final AtlasGen.AtlasEntry test0Entry = atlasEntry(test0);


//        block = Mesh.from_raw(injectUVs(vertices, 3, 4, simpleEntry), indices, new Shader.Attribute[]{
//                Shader.Attribute.Vec3,
//                Shader.Attribute.Vec2,
//                Shader.Attribute.Float
//                ,});


//        val shader = Dsa.compileToShader("content/shader/box.toml").second;
//        final Texture texture = Texture.fromFile("content/textures/exp.png", new Texture.ImgConfig(true, false,
//        true));
//        registeredMeshes.put(Simple.class, new BlockMesh(Color.WHITE, shader,
//                new Texture[]{simpleEntry.atlas()}, new Mesh[]{block}));

        InputStream inputStream = new FileInputStream("content/pipe.obj");
        final List<Mtl> read = MtlReader.read(new FileInputStream("content/pipe.mtl"));
        val map = new HashMap<String,Mtl>();
        for (Mtl mtl : read) {
            System.out.println(mtl.getName());
            map.put(mtl.getName(), mtl);
        }
        val full_obj = ObjUtils.convertToRenderable(
                ObjReader.read(inputStream));
        val stringObjMap = ObjSplitting.splitByMaterialGroups(full_obj);
        val shader_obj = Dsa.compileToShader("content/shader/obj.toml").second;
        val objs = new RenderObj[stringObjMap.size()];
        val index = new AtomicInteger();

        stringObjMap.forEach((mat, obj) -> {
            IntBuffer indices = ObjData.getFaceVertexIndices(obj);
            FloatBuffer vertices = ObjData.getVertices(obj);
            FloatBuffer texCoords = ObjData.getTexCoords(obj, 2);
            FloatBuffer normals = ObjData.getNormals(obj);

            val mesh = Mesh.from_buffers(indices, vertices, texCoords, normals, 0.5f);
            final Texture texture;
            try {
                var path = "content/textures/" + mat + ".png";
                val file = new File(path);
                if(!file.exists()) {
                    final Mtl mtl = map.get(mat);
                    assert mtl != null : mat;
                    path = mtl.getMapKd();
                }

                texture = Texture.fromFile(path, new Texture.ImgConfig(true, false,
                        false));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            val renderObj = new RenderObj(shader_obj, texture, mesh);
            objs[index.getAndIncrement()] = renderObj;
        });
        registeredMeshes.put(Wool.class, new BlockMesh(Color.WHITE, objs));

//        registeredRenderers.put(Simple.class, new SimpleRenderer(meshByClass(Simple.class).shader()));
        registeredRenderers.put(Wool.class, new SimpleRenderer(shader_obj));


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

    private static void generateAtlas() {
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

    }
}
