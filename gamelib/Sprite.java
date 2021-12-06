package clachipso.gamelib;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Sprite {
    public BufferedImage image;
    public Rectangle srcRect;

    public void draw(BufferedImage frameBuffer, int x, int y) {
        Graphics2D g2d = (Graphics2D)frameBuffer.getGraphics();
        g2d.drawImage(image, x, y, x + srcRect.width, y + srcRect.height,
                srcRect.x, srcRect.y, srcRect.x + srcRect.width,
                srcRect.y + srcRect.height, null);
    }

    public Sprite(String path) {
        InputStream input = Sprite.class.getResourceAsStream(path);
        try {
            image = ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        srcRect = new Rectangle();
        srcRect.x = 0;
        srcRect.y = 0;
        srcRect.width = image.getWidth();
        srcRect.height = image.getHeight();

    }
}
