package de.plixo.rendering;

import de.plixo.general.Factory;
import de.plixo.general.RenderAsset;
import de.plixo.game.collision.CollisionMesh;
import de.plixo.rendering.targets.Shader;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30C.*;


public class Mesh implements RenderAsset {

    public static final int NONE = 0;
    public static final int GENERATE_COLLISION = 1 << 1;
    public static final int SCALE_HALF = 1 << 2;

    public static final int DYNAMIC_DRAW = 1 << 3;


    private int vbo;
    private int elements;

    @Getter
    @Accessors(fluent = true)
    private int vao;

    @Getter
    @Accessors(fluent = true)
    @Nullable CollisionMesh collision = null;

    @Factory
    public static Mesh from_buffers(@NotNull IntBuffer indices,
                                    @NotNull FloatBuffer vertices,
                                    @NotNull FloatBuffer texCoords,
                                    @NotNull FloatBuffer normals, int flags) {

        val list = new float[vertices.remaining() + texCoords.remaining() + normals.remaining()];
        var size = 0;

        float scale = 1f;
        if ((flags & SCALE_HALF) == SCALE_HALF) {
            scale = 0.5f;
        }

        ArrayList<CollisionMesh.Vertex> vertices_ = new ArrayList<>();

        while (vertices.hasRemaining()) {
            final var x = vertices.get() * scale;
            final var y = vertices.get() * scale;
            final var z = vertices.get() * scale;
            list[size++] = x;
            list[size++] = y;
            list[size++] = z;

            vertices_.add(new CollisionMesh.Vertex(x, y, z));

            list[size++] = texCoords.get();
            list[size++] = texCoords.get();

            list[size++] = normals.get();
            list[size++] = normals.get();
            list[size++] = normals.get();
        }
        ArrayList<Integer> indices_ = new ArrayList<>();
        final int[] indices_List = new int[indices.remaining()];
        int builder = 0;
        assert indices.remaining() % 3 == 0;
        while (indices.hasRemaining()) {
            indices_.add(indices_List[builder++] = indices.get());
            indices_.add(indices_List[builder++] = indices.get());
            indices_.add(indices_List[builder++] = indices.get());
        }

        final var collision = (flags & GENERATE_COLLISION) == GENERATE_COLLISION ? new CollisionMesh(vertices_,
                indices_) : null;
        return from_raw(list, indices_List,
                new Shader.Attribute[]{
                        Shader.Attribute.Vec3,
                        Shader.Attribute.Vec2,
                        Shader.Attribute.Vec3,
                },
                flags,
                collision);


    }

    @Factory
    public static Mesh from_raw(float @NotNull [] vertices,
                                int @NotNull [] indices,
                                Shader.Attribute @NotNull [] layout,
                                int flags,
                                @Nullable CollisionMesh collision) {
        val vertex_size = computeVertexSize(layout);
        val vao = glGenVertexArrays();
        val vbo = glGenBuffers();
        val ebo = glGenBuffers();
        glBindVertexArray(vao);

        boolean drawStatic = (flags & DYNAMIC_DRAW) == DYNAMIC_DRAW;

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, drawStatic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW);


        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, drawStatic ? GL_DYNAMIC_DRAW : GL_STATIC_DRAW);

        int offset = 0;
        for (int i = 0; i < layout.length; i++) {
            final Shader.Attribute attrib = layout[i];
            glVertexAttribPointer(i, attrib.size(), GL_FLOAT, false, vertex_size, offset);

            offset += attrib.byte_size();
            glEnableVertexAttribArray(i);
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        final Mesh mesh = new Mesh();
        mesh.collision = collision;
        mesh.vao = vao;
        mesh.vbo = vbo;
        mesh.elements = indices.length;
        return mesh;
    }

    private static int computeVertexSize(@NotNull Shader.Attribute[] layout) {
        int size = 0;
        for (Shader.Attribute attribute : layout) {
            size += attribute.byte_size();
        }
        return size;
    }

    public void put_dynamic_data(float @NotNull [] vertices) {
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    @Override
    public void render() {
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, elements, GL_UNSIGNED_INT, 0);
    }

    @Override
    public void bind() {
        glBindVertexArray(vao);
    }

    @Override
    public void unbind() {
        glBindVertexArray(0);
    }

    @Override
    public void delete() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }

}
