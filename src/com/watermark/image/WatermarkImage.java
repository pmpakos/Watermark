package com.watermark.image;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.GradientPaint;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class WatermarkImage{
    public static void main(String[] args) {
        // Everything was created using help from https://www.richardnichols.net/2010/09/how-to-add-dynamic-watermark-to-jpeg-java/. Thanks

        String watermark = "\u00a9 TeraFUN "; // Special character (Copyright)
        for (String s: args)
        {
            String str = s;

            File origFile = new File(str);

            ImageIcon icon = new ImageIcon(origFile.getPath());

            // create BufferedImage object of same width and height as of bufferedImage image
            BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);

            // create graphics object and add bufferedImage image to it
            Graphics2D graphics = bufferedImage.createGraphics();
            graphics.drawImage(icon.getImage(), 0, 0, null);

            graphics.scale(1, 1);
            graphics.addRenderingHints(
                    new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON));
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


            Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
            GlyphVector fontGV = font.createGlyphVector(graphics.getFontRenderContext(), watermark);
            Rectangle size = fontGV.getPixelBounds(graphics.getFontRenderContext(), 0, 0);
            Shape textShape = fontGV.getOutline();
            double textWidth = size.getWidth();
            double textHeight = size.getHeight();
            AffineTransform rotate45 = AffineTransform.getRotateInstance(Math.PI / 4d);
            Shape rotatedText = rotate45.createTransformedShape(textShape);

            // use a gradient that repeats 4 times
            graphics.setPaint(new GradientPaint(0, 0,
                    new Color(0f, 0f, 0f, 0.1f),
                    bufferedImage.getWidth() / 2, bufferedImage.getHeight() / 2,
                    new Color(1f, 1f, 1f, 0.1f)));
            graphics.setStroke(new BasicStroke(0.5f));

            // step in y direction is calc'ed using pythagoras + 5 pixel padding
            double yStep = Math.sqrt(textWidth * textWidth / 2) + 5;

            // step over image rendering watermark text
            for (double x = -textHeight * 3; x < bufferedImage.getWidth(); x += (textHeight * 3)) {
                double y = -yStep;
                for (; y < bufferedImage.getHeight(); y += yStep) {
                    graphics.draw(rotatedText);
                    graphics.fill(rotatedText);
                    graphics.translate(0, yStep);
                }
                graphics.translate(textHeight * 3, -(y + yStep));
            }

            graphics.dispose();

            int ind = str.lastIndexOf(".");
            if( ind>=0 )
                str = new StringBuilder(str).replace(ind, ind+1,"_teraFUN.").toString();
            File newFile = new File(str);

            try {
                ImageIO.write(bufferedImage, "png", newFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(newFile.getPath() + " created successfully!");
        }
    }

}