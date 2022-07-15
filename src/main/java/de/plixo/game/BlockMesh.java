package de.plixo.game;

import de.javagl.obj.*;
import de.plixo.general.Tuple;
import de.plixo.general.collision.RayTraceHit;
import de.plixo.general.collision.RayTraceMiss;
import de.plixo.general.collision.RayTraceResult;
import de.plixo.general.dsa.Dsa;
import de.plixo.rendering.Debug;
import de.plixo.rendering.Mesh;
import de.plixo.rendering.Shader;
import de.plixo.rendering.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

@AllArgsConstructor
public class BlockMesh {
    @Getter
    @Accessors(fluent = true)
    private @NotNull RenderObj[] objs;

    public void render() {
        for (final RenderObj obj : objs()) {
            obj.shader().flush();
            if (obj.texture() != null) {
                glEnable(GL_TEXTURE_2D);
                glActiveTexture(GL_TEXTURE0);
                obj.texture().bind();
            } else {
                glDisable(GL_TEXTURE_2D);
            }
            obj.mesh().drawElements();
        }
    }


    public static @NotNull Tuple<BlockMesh, Shader> generate(@NotNull String obj_path, @NotNull String mat_path,
                                                             @NotNull String shader_path, int flags) throws IOException {
        var obj_file = new File("content/model/" + obj_path);
        if (!obj_file.exists()) {
            obj_file = new File("content/" + obj_path);
        }
        var mtl_file = new File("content/model/" + mat_path);
        if (!mtl_file.exists()) {
            mtl_file = new File("content/" + obj_path);
        }

        val inputStream = new FileInputStream(obj_file);
        val read = MtlReader.read(new FileInputStream(mtl_file));
        val map = new HashMap<String, Mtl>();
        for (Mtl mtl : read) {
            map.put(mtl.getName(), mtl);
        }
        val full_obj = ObjUtils.convertToRenderable(
                ObjReader.read(inputStream));
        val stringObjMap = ObjSplitting.splitByMaterialGroups(full_obj);
        val shader_obj = Dsa.compileToShader(shader_path).second;
        val objs = new RenderObj[stringObjMap.size()];
        val index = new AtomicInteger();

        stringObjMap.forEach((mat, obj) -> {
            val indices = ObjData.getFaceVertexIndices(obj);
            val vertices = ObjData.getVertices(obj);
            val texCoords = ObjData.getTexCoords(obj, 2);
            val normals = ObjData.getNormals(obj);

            val mesh = Mesh.from_buffers(indices, vertices, texCoords, normals, flags | Mesh.SCALE_HALF);
            Texture texture;
            try {
                var path = "content/textures/" + mat + ".png";
                val file = new File(path);

                if (!file.exists()) {
                    final Mtl mtl = map.get(mat);
                    if (mtl == null) {
                        texture = null;
                    } else {
                        final var src = mtl.getMapKd();
                        if (src == null) {
                            System.err.println("File " + src + " does not exists");
                            texture = null;
                        } else {
                            final var file1 = new File(src);
                            if (!file1.exists()) {
                                System.err.println("File " + src + " does not exists");
                            }
                            texture = Texture.fromFile(src, new Texture.ImgConfig(true, false, false));
                        }
                    }
                } else {
                    texture = Texture.fromFile(path, new Texture.ImgConfig(true, false, false));
                }

            } catch (IOException e) {
                texture = null;
                e.printStackTrace();
            }
            val renderObj = new RenderObj(shader_obj, texture, mesh);
            objs[index.getAndIncrement()] = renderObj;
        });
        return new Tuple<>(new BlockMesh(objs), shader_obj);
    }

}
