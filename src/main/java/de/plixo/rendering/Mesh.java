package de.plixo.rendering;

import de.plixo.general.collision.CollisionMesh;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30C.*;


public class Mesh {

    public static final int NONE = 0;
    public static final int GENERATE_COLLISION = 1 << 1;
    public static final int SCALE_HALF = 1 << 2;

    @Getter
    @Accessors(fluent = true)
    private int vao;

    @Getter
    @Accessors(fluent = true)
    private int elements;

    private int flags;

    @Getter
    @Accessors(fluent = true)
    @Nullable CollisionMesh collision;

    public Mesh(@Nullable CollisionMesh collision) {
        this.collision = collision;
    }

    private static int computeVertexSize(@NotNull Shader.Attribute[] layout) {
        int size = 0;
        for (Shader.Attribute attribute : layout) {
            size += attribute.byte_size;
        }
        return size;
    }


    public void drawElements() {
        glBindVertexArray(vao);
        glDrawElements(GL_TRIANGLES, elements, GL_UNSIGNED_INT, 0);
    }

    public static int[] toIntArray(IntBuffer b) {
        if (b.hasArray()) {
            if (b.arrayOffset() == 0)
                return b.array();

            return Arrays.copyOfRange(b.array(), b.arrayOffset(), b.array().length);
        }

        b.rewind();
        int[] foo = new int[b.remaining()];
        b.get(foo);

        return foo;
    }

    public static float[] toFloatArray(FloatBuffer b) {
        if (b.hasArray()) {
            if (b.arrayOffset() == 0)
                return b.array();

            return Arrays.copyOfRange(b.array(), b.arrayOffset(), b.array().length);
        }

        b.rewind();
        float[] foo = new float[b.remaining()];
        b.get(foo);

        return foo;
    }

    public static Mesh from_buffers(
            IntBuffer indices,
            FloatBuffer vertices,
            FloatBuffer texCoords,
            FloatBuffer normals, int flags) {

        val list = new float[vertices.remaining() + texCoords.remaining() + normals.remaining()];
        var size = 0;

        float scale = 1f;
        if ((flags & SCALE_HALF) == SCALE_HALF) {
            scale = 0.5f;
        }

        ArrayList<CollisionMesh.Vertex> triangles = new ArrayList<>();

        while (vertices.hasRemaining()) {
            final var x = vertices.get() * scale;
            final var y = vertices.get() * scale;
            final var z = vertices.get() * scale;
            list[size++] = x;
            list[size++] = y;
            list[size++] = z;

            triangles.add(new CollisionMesh.Vertex(x, y, z));

            list[size++] = texCoords.get();
            list[size++] = texCoords.get();

            list[size++] = normals.get();
            list[size++] = normals.get();
            list[size++] = normals.get();
        }
        final var collision = (flags & GENERATE_COLLISION) == GENERATE_COLLISION ? new CollisionMesh(triangles) : null;
        return from_raw(list, toIntArray(indices),
                new Shader.Attribute[]{
                        Shader.Attribute.Vec3,
                        Shader.Attribute.Vec2,
                        Shader.Attribute.Vec3,
                },
                flags,
                collision);


    }

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

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        val floatBuffer = BufferUtils.createFloatBuffer(vertices.length);
        floatBuffer.put(vertices).rewind();
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);


        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        val buffer = BufferUtils.createIntBuffer(indices.length);
        buffer.put(indices).rewind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        int offset = 0;
        for (int i = 0; i < layout.length; i++) {
            final Shader.Attribute attrib = layout[i];
            glVertexAttribPointer(i, attrib.size, GL_FLOAT, false, vertex_size, (long) offset);

            offset += attrib.byte_size;
            glEnableVertexAttribArray(i);
        }
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        final Mesh mesh = new Mesh(collision);
        mesh.vao = vao;
        mesh.elements = indices.length;
        return mesh;
    }

}
