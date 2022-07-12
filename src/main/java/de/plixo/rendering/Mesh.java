package de.plixo.rendering;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL30C.*;


public class Mesh {
    @Getter
    @Accessors(fluent = true)
    private int vbo;

    public static Mesh from_raw(float @NotNull [] verticies, int @NotNull [] indicies,
                                Shader.Attribute @NotNull [] layout) {
        val vertex_size = computeVertexSize(layout);
        var vao = glGenVertexArrays();
        val vbo = glGenBuffers();
        var ebo = glGenBuffers();
        glBindVertexArray(vao);

        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        final FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(verticies.length);
        floatBuffer.put(verticies).rewind();
        glBufferData(GL_ARRAY_BUFFER, verticies, GL_STATIC_DRAW);


        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        final IntBuffer buffer = BufferUtils.createIntBuffer(indicies.length);
        buffer.put(indicies).rewind();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        int offset = 0;
        for (int i = 0; i < layout.length; i++) {
            final Shader.Attribute attrib = layout[i];
            glVertexAttribPointer(i, attrib.size, GL_FLOAT, false, vertex_size, (long) offset);

            offset += attrib.byte_size;
            glEnableVertexAttribArray(i);
        }
        glBindBuffer(GL_ARRAY_BUFFER,0);
        glBindVertexArray (0);

        final Mesh mesh = new Mesh();
        mesh.vbo = vbo;
        return mesh;
    }

    private static int computeVertexSize(@NotNull Shader.Attribute[] layout) {
        int size = 0;
        for (Shader.Attribute attribute : layout) {
            size += attribute.byte_size;
        }
        return size;
    }
}
