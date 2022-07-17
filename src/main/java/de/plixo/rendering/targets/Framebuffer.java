package de.plixo.rendering.targets;

import de.plixo.general.Color;
import de.plixo.general.Factory;
import de.plixo.general.RenderAsset;
import de.plixo.general.Sealable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30C.*;


@AllArgsConstructor
public class Framebuffer extends Sealable implements RenderAsset {
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
    public static Framebuffer generate(int width, int height) {
        return new Framebuffer(glGenFramebuffers(), width, height);
    }

    public void attach_texture(@NotNull Texture texture, int target) {
        assert texture.width() == width && texture.height() == height;
        assertNotSealed();
        bind();
        glFramebufferTexture2D(GL_FRAMEBUFFER, target, GL_TEXTURE_2D, texture.id(), 0);
        unbind();
    }

    public void attach_buffer(@NotNull RenderBuffer buffer, int attachment) {
        assert buffer.width() == width && buffer.height() == height;
        assertNotSealed();
        bind();
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, attachment, GL_RENDERBUFFER, buffer.id());
        unbind();
    }

    public void assertState() {
        bind();
        assert glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
        unbind();
    }


    @Override
    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id());
    }

    @Override
    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void delete() {
        glDeleteFramebuffers(id);
    }


    public static void clear() {
        glClearColor(1f, 1f, 1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public static void clear(Color color) {
        float alpha = (float) (color.getRgba() >> 24 & 255) / 255.0f;
        float red = (float) (color.getRgba() >> 16 & 255) / 255.0f;
        float green = (float) (color.getRgba() >> 8 & 255) / 255.0f;
        float blue = (float) (color.getRgba() & 255) / 255.0f;
        glClearColor(red, green, blue, alpha);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }


}
