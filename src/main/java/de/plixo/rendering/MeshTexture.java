package de.plixo.rendering;

import de.javagl.obj.*;
import de.plixo.general.Factory;
import de.plixo.general.Tuple;
import de.plixo.rendering.targets.Shader;
import de.plixo.rendering.targets.Texture;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class MeshTexture {
    @Getter
    @Accessors(fluent = true)
    private @Nullable Texture texture;
    @Getter
    @Accessors(fluent = true)
    private @NotNull Mesh mesh;

    @Factory
    public static @NotNull MeshTexture[] generate(@NotNull String obj_path, @NotNull String mat_path) throws IOException {
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
        String mat_ = "";
        for (Mtl mtl : read) {
            mat_ = mtl.getName();
            map.put(mtl.getName(), mtl);
        }
        String mat = mat_;
        final var input = ObjReader.read(inputStream);
//        System.out.println("s");
//        System.out.println(ObjSplitting.splitByMaterialGroups(input).size());
//        System.out.println(input.getNumGroups());
        var full_obj = ObjUtils.convertToRenderable(input);
        val stringObjMap = ObjSplitting.splitByGroups(full_obj);
        val objs = new MeshTexture[stringObjMap.size()];
        val index = new AtomicInteger();

        stringObjMap.forEach((unused,obj) -> {
            val indices = ObjData.getFaceVertexIndices(obj);
            val vertices = ObjData.getVertices(obj);
            val texCoords = ObjData.getTexCoords(obj, 2);
            val normals = ObjData.getNormals(obj);
            val mesh = Mesh.from_buffers(indices, vertices, texCoords, normals, Mesh.SCALE_HALF | Mesh.GENERATE_COLLISION);
            Texture texture;
            try {
                var path = "content/textures/" + mat + ".png";
                val file = new File(path);
                if (!file.exists()) {
                    final Mtl mtl = map.get(mat);
                    if (mtl == null) {
                        texture = null;
                    } else {
                        val src = mtl.getMapKd();
                        if (src == null) {
                            System.err.println("Image in " + mtl + " does not exists");
                            texture = null;
                        } else {
                            val file1 = new File(src);
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

            val renderObj = new MeshTexture(texture, mesh);

            objs[index.getAndIncrement()] = renderObj;
        });
        return  Arrays.copyOf(objs,index.get());
    }
}
