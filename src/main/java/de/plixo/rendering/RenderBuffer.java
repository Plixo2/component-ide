package de.plixo.rendering;

import de.plixo.general.Factory;
import de.plixo.general.RenderAsset;
import de.plixo.general.Sealable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import static org.lwjgl.opengl.GL30C.*;

@AllArgsConstructor
public class RenderBuffer extends Sealable implements RenderAsset {
    @Getter
    @Accessors(fluent = true)
    private final int id;

    @Getter
    @Accessors(fluent = true)
    private final int width;

    @Getter
    @Accessors(fluent = true)
    private final int height;

    @Factory
    public static RenderBuffer generate(int width, int height) {
        return new RenderBuffer(glGenRenderbuffers(), width, height);
    }


    public void store(int format) {
        assertNotSealed();
        glRenderbufferStorage(GL_RENDERBUFFER, format, width, height);
    }


    @Override
    public void bind() {
        glBindRenderbuffer(GL_RENDERBUFFER, id);
    }

    @Override
    public void delete() {
        glDeleteRenderbuffers(id);
    }

    @Override
    public void unbind() {
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

}
