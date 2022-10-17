package de.plixo.rendering;


import de.plixo.general.Color;
import de.plixo.general.Factory;
import de.plixo.general.RenderAsset;
import de.plixo.general.Sealable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30C.glBindFramebuffer;

@RequiredArgsConstructor
public class RenderTarget extends Sealable implements RenderAsset {
    private static Stack<Integer> buffers = new Stack<>();
    private static int boundObj = 0;

    static {
        buffers.add(boundObj);
    }

    public static void pushStatic(int id) {
        buffers.push(boundObj);
        boundObj = id;
        RenderTarget.bindTop();
    }

    public static void popStatic() {
        boundObj = buffers.pop();
        RenderTarget.bindTop();
    }

    private static void bindTop() {
        glBindFramebuffer(GL_FRAMEBUFFER, boundObj);
    }

    @Getter
    @Accessors(fluent = true)
    private final @NotNull Framebuffer framebuffer;

    private final @NotNull Map<Integer, RenderAttachment> attachments;


    @Factory
    public static RenderTarget generate(int width, int height) {
        val framebuffer = Framebuffer.generate(width, height);
        return new RenderTarget(framebuffer, new HashMap<>());
    }

    public void pushBuffer(@Nullable Color color) {
        assertSealed();
        RenderTarget.pushStatic(framebuffer.id());
        RenderTarget.bindTop();

        if (color != null) {
            Framebuffer.clear(color);
        }
    }

    public void popBuffer() {
        assertSealed();
        RenderTarget.popStatic();
    }

    @Override
    public void seal() {
        super.seal();
        framebuffer.assertState();
        framebuffer.unbind();
    }

    @Override
    public void bind() {
        framebuffer.bind();
    }

    @Override
    public void unbind() {
        framebuffer.unbind();
    }

    @Override
    public void delete() {
        framebuffer.delete();

        attachments.forEach((k, v) -> {
            if (v.is_buffer()) {
                v.as_buffer().delete();
            } else if (v.is_texture()) {
                v.as_texture().delete();
            } else {
                throw new RuntimeException("unknown object " + v.object);
            }
        });
    }

    public Texture new_texture(int target) {
        assertNotSealed();
        framebuffer.bind();
        val attachment = Texture.createAttachment(framebuffer.width(), framebuffer().height());
        attach_texture(attachment, target);
        framebuffer.unbind();
        attachment.seal();
        return attachment;
    }

    public RenderBuffer new_buffer(int attachment, int storage) {
        assertNotSealed();
        framebuffer.bind();
        val attachment_ = RenderBuffer.generate(framebuffer.width(), framebuffer.height());
        attachment_.bind();
        attachment_.store(storage);
        attach_buffer(attachment_, attachment);
        attachment_.unbind();
        attachment_.seal();
        framebuffer.unbind();
        return attachment_;
    }

    public void attach_buffer(@NotNull RenderBuffer buffer, int attachment) {
        assertNotSealed();
        framebuffer.attach_buffer(buffer, attachment);
        attachments.put(attachment, new RenderAttachment(buffer));
    }

    public void attach_texture(@NotNull Texture texture, int target) {
        assertNotSealed();
        framebuffer.attach_texture(texture, target);
        attachments.put(target, new RenderAttachment(texture));
    }

    public @NotNull RenderAttachment get(int target) {
        assertSealed();
        return Objects.requireNonNull(attachments.get(target));
    }

    public void render_texture(float x, float y, float x2, float y2, int type) {
        assertSealed();
        val texture = get(type).as_texture();
        texture.drawStatic(x, y, x2, y2, -1);
    }

    @AllArgsConstructor
    public static class RenderAttachment {

        private Object object;

        public Texture as_texture() {
            assert object instanceof Texture;
            return ((Texture) object);
        }

        public RenderBuffer as_buffer() {
            assert object instanceof RenderBuffer;
            return ((RenderBuffer) object);
        }

        public boolean is_buffer() {
            return object instanceof RenderBuffer;
        }

        public boolean is_texture() {
            return object instanceof Texture;
        }
    }
}
