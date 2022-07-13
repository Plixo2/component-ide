package de.plixo.game;

import de.plixo.general.Tuple;
import de.plixo.rendering.Texture;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AtlasGen {
    public static @NotNull Tuple<HashMap<BufferedImage, Vector2i>, BufferedImage> generate(Collection<BufferedImage> textures, int dim) {
        BufferedImage img = new BufferedImage(dim, dim, BufferedImage.TYPE_INT_ARGB);
        final var graphics = img.getGraphics();
        val map = new HashMap<BufferedImage, Vector2i>();
        val sorted = textures.stream().sorted((t1, t0) -> t0.getWidth() - t1.getWidth());
        AtomicInteger y = new AtomicInteger();
        AtomicInteger x = new AtomicInteger();
        AtomicInteger last = new AtomicInteger();
        final var iterator = sorted.iterator();
        while (iterator.hasNext()) {
            val texture = iterator.next();
            if (y.get() > img.getHeight() - texture.getHeight() + 2) {
                x.getAndAdd(last.get());
                y.set(0);
                last.set(0);
            }

            graphics.drawImage(texture, x.get()+2, y.get()+1, texture.getWidth(), texture.getHeight(), null);
            graphics.drawImage(texture, x.get(), y.get()+1, texture.getWidth(), texture.getHeight(), null);
            graphics.drawImage(texture, x.get()+1, y.get()+2, texture.getWidth(), texture.getHeight(), null);
            graphics.drawImage(texture, x.get()+1, y.get(), texture.getWidth(), texture.getHeight(), null);

            graphics.drawImage(texture, x.get()+1, y.get()+1, texture.getWidth(), texture.getHeight(), null);

            map.put(texture, new Vector2i(x.get()+1, y.get()+1));
            y.addAndGet(texture.getHeight() + 2);
            last.set(Math.max(last.get(), texture.getWidth() + 2));
            if (x.get() + texture.getWidth() + 2 > img.getWidth()) {
                return generate(textures, dim * 2);
            }
        }

        return new Tuple<>(map, img);
    }

    public static void main(String[] args) {
        File f = new File("content/packtest");
        val images = new ArrayList<BufferedImage>();
        for (File file : f.listFiles()) {
            try {
                images.add(ImageIO.read(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        final var generate = generate(images, 64);
        try {
                final var graphics = generate.second.getGraphics();
            generate.first.forEach((img, coord) -> {
                graphics.drawRect(coord.x,coord.y,img.getWidth(),img.getHeight());
            });

            ImageIO.write(generate.second, "png", new File("content/packed.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @RequiredArgsConstructor
    public static class AtlasEntry {
        @Getter
        @Accessors(fluent = true)
        final Texture atlas;

        @Getter
        @Accessors(fluent = true)
        final float x;

        @Getter
        @Accessors(fluent = true)
        final float y;


        @Getter
        @Accessors(fluent = true)
        final float width;


        @Getter
        @Accessors(fluent = true)
        final float height;


        @Override
        public String toString() {
            return "AtlasEntry{" +
                    "atlas=" + atlas +
                    ", x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
