package clachipso.gamelib;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;

/**
 * A renderable font that uses bitmap images for glyphs. BitmapFonts can be 
 * loaded from AngelCode BMFont files.
 */
public class BitmapFont {

    public class Glyph {
        public final int sourceX;
        public final int sourceY;
        public final int width;
        public final int height;
        public final int xOffset;
        public final int yOffset;
        public final int xAdvance;

        public Glyph(int sourceX, int sourceY, int width, int height,
                int xOffset, int yOffset, int xAdvance) {
            this.sourceX = sourceX;
            this.sourceY = sourceY;
            this.width = width;
            this.height = height;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.xAdvance = xAdvance;
        }
    }

    private BufferedImage bitmap;
    private int lineHeight;
    private int base;
    private String faceName;
    private Glyph [] glyphs;

    public BitmapFont(BufferedImage fontBitmap, String fontData) {
        bitmap = fontBitmap;

        // Parse the fond data line by line.
        glyphs = new Glyph[256];
        String [] lines = fontData.split("[\r\n]+");
        for (String line : lines) {
            // The first word on each line indicates the type of data in the line.
            int i = line.indexOf(" ");
            if (i <= 0) {
                continue;
            }
            String lineType = line.substring(0, i);

            if (lineType.equals("info")) {
                i = line.indexOf("face");
                i = line.indexOf("=", i);
                int faceStart = i + 2;
                int faceEnd = line.indexOf("\"", faceStart);
                faceName = line.substring(faceStart, faceEnd);
            }
            else if (lineType.equals("common")) {
                i = line.indexOf("lineHeight");
                i = line.indexOf("=", i);
                String lineHeightString =
                        line.substring(i + 1, line.indexOf(" ", i));
                lineHeight = Integer.parseInt(lineHeightString);
                i = line.indexOf("base");
                i = line.indexOf("=", i);
                String baseString = line.substring(i + 1, line.indexOf(" ", i));
                base = Integer.parseInt(baseString);
            }
            else if (lineType.equals("char")) {
                i = line.indexOf("id");
                i = line.indexOf("=", i);
                String idString = line.substring(i + 1, line.indexOf(" ", i));
                int charId = Integer.parseInt(idString);
                i = line.indexOf("x");
                i = line.indexOf("=", i);
                String xLocString = line.substring(i + 1, line.indexOf(" ", i));
                int sourceX = Integer.parseInt(xLocString);
                i = line.indexOf("y");
                i = line.indexOf("=", i);
                String yLocString = line.substring(i + 1, line.indexOf(" ", i));
                int sourceY = Integer.parseInt(yLocString);
                i = line.indexOf("width");
                i = line.indexOf("=", i);
                String widthString =
                        line.substring(i + 1, line.indexOf(" ", i));
                int width = Integer.parseInt(widthString);
                i = line.indexOf("height");
                i = line.indexOf("=", i);
                String heightString =
                        line.substring(i + 1, line.indexOf(" ", i));
                int height = Integer.parseInt(heightString);
                i = line.indexOf("xoffset");
                i = line.indexOf("=", i);
                String xOffsetString =
                        line.substring(i + 1, line.indexOf(" ", i));
                int xOffset = Integer.parseInt(xOffsetString);
                i = line.indexOf("yoffset");
                i = line.indexOf("=", i);
                String yOffsetString =
                        line.substring(i + 1, line.indexOf(" ", i));
                int yOffset = Integer.parseInt(yOffsetString);
                i = line.indexOf("xadvance");
                i = line.indexOf("=", i);
                String advanceString =
                        line.substring(i + 1, line.indexOf(" ", i));
                int xAdvance = Integer.parseInt(advanceString);
                Glyph glyph = new Glyph(sourceX, sourceY, width, height,
                        xOffset, yOffset, xAdvance);
                glyphs[charId] = glyph;
            }
        }
    }

    public int getLineHeight()
    {
        return lineHeight;
    }

    public int getBase()
    {
        return base;
    }

    public String getFaceName()
    {
        return faceName;
    }

    public Glyph getGlyph(int asciiCode) {
        if (asciiCode < 0 || asciiCode > 255) {
            return null;
        }
        return glyphs[asciiCode];
    }

    public Image getBitmap()
    {
        return bitmap;
    }

    public void drawText(BufferedImage frameBuffer, String text, int x, int y,
            Color color) {
        int cursorX = x;
        int cursorY = y;
        int rgb = color.getRGB();
        for (int i = 0; i < text.length(); i++) {
            BitmapFont.Glyph glyph = getGlyph(text.charAt(i));
            if (glyph == null) {
                glyph = getGlyph('?');
            }
            int destX = cursorX + glyph.xOffset;
            int destY = cursorY + glyph.yOffset;

            for (int bitmapY = 0; bitmapY < glyph.height; bitmapY++) {
                for (int bitmapX = 0; bitmapX < glyph.width; bitmapX++) {
                    int pixelX = destX + bitmapX;
                    int pixelY = destY + bitmapY;

                    if (pixelX < 0 || pixelX >= frameBuffer.getWidth() ||
                        pixelY < 0 || pixelY >= frameBuffer.getHeight()) {
                        continue;
                    }

                    int pixelData = bitmap.getRGB(glyph.sourceX + bitmapX,
                            glyph.sourceY + bitmapY);
                    if ((pixelData & 0xFF000000) == 0) {
                        continue;
                    }
                    frameBuffer.setRGB(pixelX, pixelY, rgb);
                }
            }
            cursorX += glyph.xAdvance;
        }
    }

    public int getStringWidth(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            int character = text.charAt(i);
            width += glyphs[character].xAdvance;
        }
        return width;
    }

    public static BitmapFont loadFromFiles(String fontFilePath,
            String fontBitmapPath) throws IOException {
        InputStream stream =
                BitmapFont.class.getResourceAsStream(fontBitmapPath);
        BufferedImage image = ImageIO.read(stream);

        InputStream fileData =
                BitmapFont.class.getResourceAsStream(fontFilePath);
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(fileData));
        String fontLines = "";
        String currLine;
        while ((currLine = reader.readLine()) != null) {
            fontLines += currLine + "\n";
        }
        return new BitmapFont(image, fontLines);
    }
}

