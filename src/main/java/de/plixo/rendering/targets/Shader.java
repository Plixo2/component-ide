package de.plixo.rendering.targets;

import de.plixo.general.Color;
import de.plixo.general.Factory;
import de.plixo.general.RenderAsset;
import de.plixo.general.Sealable;
import de.plixo.general.dsa.Dsa;
import de.plixo.general.reference.Reference;
import de.plixo.systems.RenderSystem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.opengl.GL43C.*;

public class Shader extends Sealable implements RenderAsset {

    private final LinkedList<Uniform> uniforms = new LinkedList<>();
    private final Map<String, Uniform> uniformCache = new HashMap<>();

    @Getter
    @Accessors(fluent = true)
    private int program;

    private final int hash;

    public Shader(int program, int hash) {
        this.program = program;
        this.hash = hash;
    }

    @Factory
    public static Shader fromDSA(String str) {
        val configShader = Dsa.compileToShader(str);
        //preload all locations
        if (configShader.first.uniform != null) {
            configShader.first.uniform.forEach((name, type) -> {
                configShader.second.uniform(name);
            });
        }
        return configShader.second;
    }

    @Factory
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

        return new Shader(program, Objects.hash(vertex, fragment));
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

    public void flush() {
        assertSealed();
        bind();
        uniforms.forEach(Uniform::flush);
        uniforms.clear();
    }

    public Uniform uniform(@NotNull String str) {
        return uniformCache.computeIfAbsent(str, (s) -> new Uniform(this, location(str), str));
    }

    private int location(@NotNull String str) {
        bind();
        assertNotSealed();
        return glGetUniformLocation(program, str);
    }

    @Override
    public void bind() {
        glUseProgram(program());
    }

    @Override
    public void unbind() {
        glUseProgram(0);
    }

    @Override
    public void delete() {
        glDeleteProgram(program());
    }

    @AllArgsConstructor
    public enum Attribute {
        Vec4(4, 4 * 4),
        Vec3(3, 3 * 4),
        Vec2(2, 2 * 4),
        Float(1, 4);

        @Getter
        @Accessors(fluent = true)
        final int size;

        @Getter
        @Accessors(fluent = true)
        final int byte_size;
    }

    @RequiredArgsConstructor
    public static class Uniform {
        final @NotNull Shader shader;
        final int location;
        final @NotNull String name;

        Object t;

        public void loadProjView() {
            load(RenderSystem.INSTANCE.projView());
        }

        public void loadIdentity() {
            load(new Matrix4f());
        }

        public void loadReference(@Nullable Reference<?> ref) {
            this.t = ref;
            linkShader();
        }

        public void load(@Nullable Matrix4f t) {
            this.t = t;
            linkShader();
        }

        public void load(@Nullable Matrix2f t) {
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
            if (upload instanceof Matrix4f matrix) {
                try (final MemoryStack stack = MemoryStack.stackPush()) {
                    glUniformMatrix4fv(location, false, matrix.get(stack.mallocFloat(16)));
                }
            } else if (upload instanceof Matrix2f matrix) {
                try (final MemoryStack stack = MemoryStack.stackPush()) {
                    glUniformMatrix2fv(location, false, matrix.get(stack.mallocFloat(4)));
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