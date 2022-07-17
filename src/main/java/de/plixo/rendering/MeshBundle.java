package de.plixo.rendering;

import de.javagl.obj.*;
import de.plixo.game.RenderObj;
import de.plixo.general.Factory;
import de.plixo.general.RenderAsset;
import de.plixo.general.Tuple;
import de.plixo.rendering.targets.Shader;
import de.plixo.rendering.targets.Texture;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public class MeshBundle implements RenderAsset {

    @Getter
    @Accessors(fluent = true)
    private @NotNull RenderObj[] objs;

    @Getter
    @Accessors(fluent = true)
    private @Nullable Shader commonShader = null;

    public MeshBundle(@NotNull RenderObj[] objs) {
        this.objs = objs;
        computeCommonShader();
    }


    @Factory
    public static @NotNull MeshBundle generate(@NotNull Mesh mesh, @NotNull String shader_path) {
        val shader_obj = Shader.fromDSA(shader_path);
        return new MeshBundle(new RenderObj[]{new RenderObj(shader_obj, null, mesh)});
    }

    @Factory
    public static @NotNull Tuple<MeshBundle, Shader> generate(@NotNull String obj_path, @NotNull String mat_path,
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
        var full_obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));
        val stringObjMap = ObjSplitting.splitByMaterialGroups(full_obj);
        val shader_obj = Shader.fromDSA(shader_path);
        val objs = new RenderObj[stringObjMap.size()];
        val index = new AtomicInteger();

        stringObjMap.forEach((mat, obj) -> {
            val indices = ObjData.getFaceVertexIndices(obj);
            val vertices = ObjData.getVertices(obj);
            val texCoords = ObjData.getTexCoords(obj, 2);
            val normals = ObjData.getNormals(obj);

            val mesh = Mesh.from_buffers(indices, vertices, texCoords, normals,
                    flags | Mesh.SCALE_HALF | Mesh.GENERATE_COLLISION);
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
                            System.err.println("Image in " + mtl + " does not exists");
                            texture = null;
                        } else {
                            final var file1 = new File(src);
                            if (!file1.exists()) {
                                System.err.println("File " + src + " does not exists");
                            }
                            texture = Texture.fromFile(src);
                        }
                    }
                } else {
                    texture = Texture.fromFile(path);
                }

            } catch (IOException e) {
                texture = null;
                e.printStackTrace();
            }
            if (texture != null) {
                texture.seal();
            }
            val renderObj = new RenderObj(shader_obj, texture, mesh);
            objs[index.getAndIncrement()] = renderObj;
        });
        shader_obj.seal();
        return new Tuple<>(new MeshBundle(objs), shader_obj);
    }

    private void computeCommonShader() {
        if (objs.length == 0) {
            this.commonShader = null;
            return;
        }
        this.commonShader = objs[0].shader();
        for (RenderObj obj : objs) {
            if (!commonShader.equals(obj.shader())) {
                this.commonShader = null;
                return;
            }
        }
    }

    @Override
    public void render() {
        for (final RenderObj obj : objs) {
            obj.shader().flush();
            if (obj.texture() != null) {
//                glEnable(GL_TEXTURE_2D);
                glActiveTexture(GL_TEXTURE0);
                obj.texture().bind();
            } else {
//                glDisable(GL_TEXTURE_2D);
            }
            obj.mesh().render();
        }
    }


}
