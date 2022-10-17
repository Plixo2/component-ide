package de.plixo.rendering;

import de.plixo.general.Factory;
import de.plixo.general.RenderAsset;
import de.plixo.general.Sealable;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL45.glGenerateTextureMipmap;
import static org.lwjgl.system.MemoryUtil.NULL;


public class Texture extends Sealable implements RenderAsset {
    @Getter
    @Accessors(fluent = true)
    private final int width;
    @Getter
    @Accessors(fluent = true)
    private final int height;
    @Getter
    @Accessors(fluent = true)
    private final int id;


    private Texture(int width, int height) {
        this.width = width;
        this.height = height;
        this.id = glGenTextures();
    }

    @Factory
    public static Texture createAttachment(int width, int height) {
        val texture = new Texture(width, height);
        texture.bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        return texture;
    }

    @Factory
    public static Texture fromFile(@NotNull String path) throws IOException {
        final BufferedImage read = ImageIO.read(new File(path));
        return fromBufferedImg(read);
    }

    @Factory
    public static Texture fromBuffer(@NotNull ByteBuffer image, int width, int height) {
        final Texture texture = new Texture(width, height);
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glGenerateTextureMipmap(texture.id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        return texture;
    }

    @Factory
    public static Texture fromBufferedImg(@NotNull BufferedImage image) {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);

        final byte[] array = new byte[width * height * 4];
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                final int rgb = image.getRGB(x, height - y - 1);
                array[index++] = (byte) (rgb >> 16 & 255);
                array[index++] = (byte) (rgb >> 8 & 255);
                array[index++] = (byte) (rgb & 255);
                array[index++] = (byte) (rgb >> 24 & 255);
            }
        }
        buffer.put(array).rewind();
        return fromBuffer(buffer, width, height);
    }

    public void tex_parameter(int parameter, int state) {
        bind();
        assertNotSealed();
        glTexParameteri(GL_TEXTURE_2D, parameter, state);
    }

    public void drawStatic(float left, float top, float right, float bottom, int color) {
        drawStatic(left, top, right, bottom, 0, 0, 1, 1, color);
    }

    public void drawStatic(float left, float top, float right, float bottom, float uvLeft, float uvTop,
                           float uvRight, float uvBottom,

                           int color) {

        assertSealed();
        uvLeft = 1 - uvLeft;
        uvRight = 1 - uvRight;

        float temp;


        if (left < right) {
            temp = left;
            left = right;
            right = temp;
        }

        if (top < bottom) {
            temp = top;
            top = bottom;
            bottom = temp;
        }

        float alpha = (float) (color >> 24 & 255) / 255.0f;
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;
        glColor4f(red, green, blue, alpha);

        glUseProgram(0);
        glBindTexture(GL_TEXTURE_2D, id);
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);

        glBegin(GL_QUADS);

        glTexCoord2f(1 - uvRight, 1 - uvTop);
        glVertex2d(left, bottom);
        glTexCoord2f(1 - uvLeft, 1 - uvTop);
        glVertex2d(right, bottom);
        glTexCoord2f(1 - uvLeft, 1 - uvBottom);
        glVertex2d(right, top);
        glTexCoord2f(1 - uvRight, 1 - uvBottom);
        glVertex2d(left, top);

        glEnd();

    }


    @Override
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    @Override
    public void delete() {
        glDeleteTextures(id);
    }
}
