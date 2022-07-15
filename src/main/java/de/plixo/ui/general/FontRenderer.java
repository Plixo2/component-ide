package de.plixo.ui.general;


import de.plixo.rendering.Texture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.lwjgl.opengl.GL11.*;

/**
 * from slick
 */
public class FontRenderer {


    private final Metric[] charArray = new Metric[256];

    private final int fontSize;

    private int fontHeight;

    private Texture fontTexture;

    private final int textureWidth = 1024;


    private final int textureHeight = 1024;

    private final Font font;


    private FontMetrics fontMetrics;


    private static class Metric {

        public int width;

        public int height;

        public int uvX;

        public int uvY;
    }


    public FontRenderer(Font font) {
        this.font = font;
        this.fontSize = font.getSize();

        createSet();
    }


    private BufferedImage getFontImage(char ch) {
        BufferedImage tempfontImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(font);
        fontMetrics = g.getFontMetrics();
        int charwidth = fontMetrics.charWidth(ch);

        if (charwidth <= 0) {
            charwidth = 1;
        }
        int charheight = fontMetrics.getHeight();
        if (charheight <= 0) {
            charheight = fontSize;
        }

        BufferedImage fontImage;
        fontImage = new BufferedImage(charwidth, charheight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gt = (Graphics2D) fontImage.getGraphics();
        gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gt.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        gt.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        gt.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        gt.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        gt.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        gt.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gt.setFont(font);

        gt.setColor(Color.WHITE);
        int charx = 0;
        int chary = 0;
        gt.drawString(String.valueOf(ch), charx, chary + fontMetrics.getAscent());

        return fontImage;

    }

    private void createSet() {


        try {

            final BufferedImage imgTemp =
                    new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = (Graphics2D) imgTemp.getGraphics();

            g.setColor(new Color(255, 255, 255, 0));
            g.fillRect(0, 0, textureWidth, textureHeight);

            int rowHeight = 0;
            int positionX = 0;
            int positionY = 0;


            for (int i = 0; i < 256; i++) {
                char ch = (char) i;

                final BufferedImage fontImage = getFontImage(ch);

                final Metric newMetric = new Metric();

                newMetric.width = fontImage.getWidth();
                newMetric.height = fontImage.getHeight();

                if (positionX + newMetric.width >= textureWidth) {
                    positionX = 0;
                    positionY += rowHeight;
                    rowHeight = 0;
                }

                newMetric.uvX = positionX;
                newMetric.uvY = positionY;

                if (newMetric.height > fontHeight) {
                    fontHeight = newMetric.height;
                }

                if (newMetric.height > rowHeight) {
                    rowHeight = newMetric.height;
                }

                g.drawImage(fontImage, positionX, positionY, null);
                positionX += newMetric.width + 4;


                charArray[i] = newMetric;
            }

            fontTexture = Texture.fromBufferedImg(imgTemp, new Texture.ImgConfig(true, false, false));
//            final File output = new File("content/font.png");
//            ImageIO.write(imgTemp, "png", output);
//            Desktop.getDesktop().open(output);


        } catch (Exception e) {
            System.err.println("Failed to create font.");
            e.printStackTrace();
        }
    }


    private void drawQuad(float drawX, float drawY, float drawX2, float drawY2, float srcX, float srcY,
                          float srcX2, float srcY2) {
        float DrawWidth = drawX2 - drawX;
        float DrawHeight = drawY2 - drawY;
        float TextureSrcX = srcX / textureWidth;
        float TextureSrcY = srcY / textureHeight;
        float SrcWidth = srcX2 - srcX;
        float SrcHeight = srcY2 - srcY;
        float RenderWidth = (SrcWidth / textureWidth);
        float RenderHeight = (SrcHeight / textureHeight);

        glTexCoord2f(TextureSrcX, TextureSrcY);
        glVertex2f(drawX, drawY);
        glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
        glVertex2f(drawX, drawY + DrawHeight);
        glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
        glVertex2f(drawX + DrawWidth, drawY + DrawHeight);
        glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
        glVertex2f(drawX + DrawWidth, drawY);
    }


    public int getWidth(String str) {
        int totalWidth = 0;

        for (int i = 0; i < str.length(); i++) {
            final char currentChar = str.charAt(i);
            final Metric metric;
            metric = charArray[currentChar];


            if (metric != null) {
                totalWidth += metric.width;
            }
        }
        return totalWidth;
    }


    public void drawString(float x, float y, String str, int color, boolean shadow) {
        drawString(x, y, str, color, 0, str.length() - 1, shadow);
    }


    public void drawString(float x, float y, String str, int color, int startIndex, int endIndex,
                           boolean shadow) {

        float alpha = (float) (color >> 24 & 255) / 255.0f;
        float red = (float) (color >> 16 & 255) / 255.0f;
        float green = (float) (color >> 8 & 255) / 255.0f;
        float blue = (float) (color & 255) / 255.0f;
        glColor4f(red, green, blue, alpha);

        glEnable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        int totalWidth = 0;
        for (int i = 0; i < str.length(); i++) {
            final char charCurrent = str.charAt(i);
            final Metric metric;
            metric = charArray[charCurrent];

            if (metric != null) {
                final int width = metric.width;
                final int height = metric.height;
                if ((i >= startIndex) || (i <= endIndex)) {
                    if (shadow) {
                        fontTexture.drawStatic((x + totalWidth) + 2, y + 2, (x + totalWidth + width), (y + height),
                                metric.uvX / (float) textureWidth,
                                metric.uvY / (float) textureHeight,
                                (metric.uvX + width) / (float) textureWidth,
                                (metric.uvY + height) / (float) textureHeight, 0xFF000000);
                    }
//
//                    drawQuad((x + totalWidth), y, (x + totalWidth + width), (y + height), metric.uvX,
//                            metric.uvY, metric.uvX + width, metric.uvY + height);

//                    UIElement.GUI.drawRect((x + totalWidth),y ,  (x + totalWidth + width), (y + height) , color);
//
                    fontTexture.drawStatic((x + totalWidth), y, (x + totalWidth + width), (y + height),
                            metric.uvX / (float) textureWidth,
                            metric.uvY / (float) textureHeight,
                            (metric.uvX + width) / (float) textureWidth,
                            (metric.uvY + height) / (float) textureHeight, color);

                }
                totalWidth += width;
            }
        }
    }
}
