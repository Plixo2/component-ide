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
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL20C.glUseProgram;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL45.glGenerateTextureMipmap;


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
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glGenerateTextureMipmap(texture.id);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
        return texture;
    }

//    public static Texture fromIntBuffer(@NotNull IntBuffer image, int width, int height) {
//        final Texture texture = new Texture(width, height);
//        texture.bind();
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
//        return texture;
//    }

    public static Texture fromBufferedImg(@NotNull BufferedImage image, @NotNull ImgConfig imgConfig) {
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

        glTexCoord2f(0, 0);
        glVertex3d(left, bottom, 0f);
        glTexCoord2f(0, 1);
        glVertex3d(right, bottom, 0f);
        glTexCoord2f(1, 1);
        glVertex3d(right, top, 0f);
        glTexCoord2f(1, 0);
        glVertex3d(left, top, 0f);

        glEnd();

    }

    public void drawStatic(float left, float top, float right, float bottom, float uvLeft, float uvTop,
                           float uvRight, float uvBottom,

                           int color) {
        uvLeft = 1-uvLeft;
        uvRight = 1-uvRight;

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

        glTexCoord2f(1- uvRight, 1-uvTop);
        glVertex2d(left, bottom);
        glTexCoord2f(1- uvLeft, 1-uvTop);
        glVertex2d(right, bottom);
        glTexCoord2f(1- uvLeft, 1-uvBottom);
        glVertex2d(right, top);
        glTexCoord2f(1- uvRight, 1-uvBottom);
        glVertex2d(left, top);

        glEnd();

    }


    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public record ImgConfig(boolean flip, boolean mirror, boolean rotate) {

    }
}
