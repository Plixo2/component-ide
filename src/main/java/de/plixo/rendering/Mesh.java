package de.plixo.rendering;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30C.*;


public class Mesh {
    @Getter
    @Accessors(fluent = true)
    private int vao;

    @Getter
    @Accessors(fluent = true)
    private int elements;

    public static Mesh from_buffers(
            IntBuffer indices,
            FloatBuffer vertices,
            FloatBuffer texCoords,
            FloatBuffer normals,float scale) {

        val list = new float[vertices.remaining() + texCoords.remaining() + normals.remaining()];
        var size = 0;

        while (vertices.hasRemaining()) {
            list[size++] = vertices.get() * scale;
            list[size++] = vertices.get() * scale;
            list[size++] = vertices.get() * scale;

            list[size++] = texCoords.get();
            list[size++] = texCoords.get();

            list[size++] = normals.get();
            list[size++] = normals.get();
            list[size++] = normals.get();
        }
        return from_raw(list, toIntArray(indices),
                new Shader.Attribute[]{
                        Shader.Attribute.Vec3,
                        Shader.Attribute.Vec2,
                        Shader.Attribute.Vec3,
                });


    }

    public static Mesh from_raw(float @NotNull [] verticies, int @NotNull [] indicies,
                                Shader.Attribute @NotNull [] layout) {
        val vertex_size = computeVertexSize(layout);
        val vao = glGenVertexArrays();
        val vbo = glGenBuffers();
        val ebo = glGenBuffers();
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        val floatBuffer = BufferUtils.createFloatBuffer(verticies.length);
        floatBuffer.put(verticies).rewind();
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_STATIC_DRAW);


        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        val buffer = BufferUtils.createIntBuffer(indicies.length);
        buffer.put(indicies).rewind();
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

        final Mesh mesh = new Mesh();
        mesh.vao = vao;
        mesh.elements = indicies.length;
        return mesh;
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
}
