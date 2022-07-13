package de.plixo.rendering;

import de.plixo.general.Color;
import de.plixo.general.reference.Reference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.util.LinkedList;

import static org.lwjgl.opengl.GL43C.*;

public class Shader {

    @Getter
    @Accessors(fluent = true)
    private int program;

    private LinkedList<Uniform> uniforms = new LinkedList<>();

    public static Shader fromSource(String vertex, String fragment) {
        final int program = glCreateProgram();
        final int vert = shader(vertex, GL_VERTEX_SHADER);
        final int frag = shader(fragment, GL_FRAGMENT_SHADER);
        glAttachShader(program, vert);
        glAttachShader(program, frag);
        glLinkProgram(program);
        glDeleteShader(vert);
        glDeleteShader(frag);

        final int linked = glGetProgrami(program, GL_LINK_STATUS);
        final String programLog = glGetProgramInfoLog(program);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
            System.exit(-1);
            throw new RuntimeException(programLog);
        }
        if (linked == 0)
            throw new AssertionError("Could not link program");

        final Shader shader = new Shader();
        shader.program = program;

        return shader;
    }

    public void flush() {
        bind();
        uniforms.forEach(Uniform::flush);
        uniforms.clear();
    }

    private static int shader(String src, int type) {
        final int shader = glCreateShader(type);
        glShaderSource(shader, src);
        glCompileShader(shader);

        final int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        final String log = glGetShaderInfoLog(shader);
        if (log.trim().length() > 0) {
            System.err.println(log);
            System.exit(-1);
            throw new RuntimeException(log);
        }
        if (compiled == 0)
            throw new AssertionError("Could not compile shader: " + src);

        return shader;
    }

    public void bind() {
        glUseProgram(program());
    }

    public Uniform uniform(@NotNull String str) {
        return new Uniform(this, location(str), str);
    }

    private int location(@NotNull String str) {
        glUseProgram(program);
        return glGetUniformLocation(program, str);
    }

    public void unbind() {
        glUseProgram(0);
    }


    @AllArgsConstructor
    public enum Attribute {
        //        Mat4x4(4 * 4,4 * 4 * 4),
//        Mat3x3(3 * 3,3 * 3 * 4),
        Vec4(4, 4 * 4),
        Vec3(3, 3 * 4),
        Vec2(2, 2 * 4),
        Float(1, 4);
        final int size;
        final int byte_size;
    }

    @RequiredArgsConstructor
    public static class Uniform {
        final @NotNull Shader shader;
        final int location;
        final @NotNull String name;

        Object t;

        public void loadReference(@Nullable Reference<?> ref) {
            this.t = ref;
            linkShader();
        }


        public void load(@Nullable Matrix4f t) {
            this.t = t;
            linkShader();
        }

        public void load(@Nullable Vector3f t) {
            this.t = t;
            linkShader();
        }

        public void load(@Nullable Color t) {
            this.t = t;
            linkShader();
        }

        public void load(@Nullable Vector2f t) {
            this.t = t;
            linkShader();
        }

        public void load(float t) {
            this.t = t;
            linkShader();
        }

        private void linkShader() {
            this.shader.uniforms.add(this);
        }

        private void flush() {
            boolean isRef = t instanceof Reference<?>;

            Object upload = this.t;
            if (upload == null) {
                return;
            } else if (isRef) {
                upload = ((Reference<?>) t).getValue();
            } else {
                this.t = null;
            }
            shader.bind();
            if (upload instanceof Matrix4f matrix) {
                try (final MemoryStack stack = MemoryStack.stackPush()) {
                    glUniformMatrix4fv(location, false, matrix.get(stack.mallocFloat(16)));
                }
            } else if (upload instanceof Color color) {
                glUniform4fv(location, color.toArray());
            } else if (upload instanceof Vector3f vector) {
                glUniform3f(location, vector.x, vector.y, vector.z);
            } else if (upload instanceof Vector2f vector) {
                glUniform2f(location, vector.x, vector.y);
            } else if (upload instanceof Float float_) {
                glUniform1f(location, float_);
            } else {
                throw new UnsupportedOperationException("cant upload from object " + upload.getClass());
            }

        }
    }

}