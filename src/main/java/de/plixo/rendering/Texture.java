package de.plixo.rendering;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;


@Getter
@Accessors(fluent = true)
public class Texture {
    final int width;
    final int height;
    final int id;

    public Texture(int width, int height) {
        this.width = width;
        this.height = height;
        this.id = glGenTextures();
    }

    private Texture(int width, int height, int id) {
        this.width = width;
        this.height = height;
        this.id = id;
    }


    public static Texture fromFile(@NotNull String path, @NotNull ImgConfig imgConfig) throws IOException {
        final BufferedImage read = ImageIO.read(new File(path));
        return fromBufferedImg(read, imgConfig);
    }

    public static Texture fromBuffer(@NotNull ByteBuffer image, int width, int height) {
        final Texture texture = new Texture(width, height);
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        return texture;
    }

    public static Texture fromIntBuffer(@NotNull IntBuffer image, int width, int height) {
        final Texture texture = new Texture(width, height);
        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        return texture;
    }

    public static Texture fromBufferedImg(@NotNull BufferedImage image, @NotNull ImgConfig imgConfig) throws
            IOException {
        final int width = image.getWidth();
        final int height = image.getHeight();

        final ByteBuffer buffer = MemoryUtil.memAlloc(width * height * 4);

        final byte[] array = new byte[width * height * 4];
        int index = 0;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xUv = y;
                int yUv = x;
                if (!imgConfig.flip()) {
                    yUv = width - x - 1;
                }
                if (!imgConfig.mirror()) {
                    xUv = height - y - 1;
                }
//                if (imgConfig.mirror()) {
//                    yUv = height - y - 1;
//                }
                final int rgb = image.getRGB(xUv, yUv);
                array[index++] = (byte) (rgb >> 16 & 255);
                array[index++] = (byte) (rgb >> 8 & 255);
                array[index++] = (byte) (rgb & 255);
                array[index++] = (byte) (rgb >> 24 & 255);
            }
        }
        buffer.put(array).rewind();
        return fromBuffer(buffer, width, height);


    }


    public void drawStatic(float left, float top, float right, float bottom, int color) {

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
        bind();
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        float alpha = (float) (color >> 24 & 255) / 255.0f;
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;
        glColor4f(red, green, blue, alpha);
        glBegin(GL_QUADS);

        glVertex2d(left, bottom);
        glTexCoord2f(0, 0);
        glVertex2d(right, bottom);
        glTexCoord2f(0, 1);
        glVertex2d(right, top);
        glTexCoord2f(1, 1);
        glVertex2d(left, top);
        glTexCoord2f(1, 0);

        glEnd();

    }

    public void drawStatic(float left, float top, float right, float bottom, float uvLeft, float uvTop,
                           float uvRight, float uvBottom,

                           int color) {

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


        if (uvLeft < uvRight) {
            temp = uvLeft;
            uvLeft = uvRight;
            uvRight = temp;
        }

        if (uvTop < uvBottom) {
            temp = uvTop;
            uvTop = uvBottom;
            uvBottom = temp;
        }

        bind();
        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_BLEND);
        float alpha = (float) (color >> 24 & 255) / 255.0f;
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;
        glColor4f(red, green, blue, alpha);
        glBegin(GL_QUADS);

        glVertex2d(left, bottom);
        glTexCoord2f(uvTop, uvLeft);
        glVertex2d(right, bottom);
        glTexCoord2f(uvTop, uvRight);
        glVertex2d(right, top);
        glTexCoord2f(uvBottom, uvRight);
        glVertex2d(left, top);
        glTexCoord2f(uvBottom, uvLeft);

        glEnd();

    }


    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public record ImgConfig(boolean flip, boolean mirror) {

    }
}
