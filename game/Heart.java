package clachipso.game;

import clachipso.gamelib.Point2D;
import clachipso.gamelib.Rectangle;
import clachipso.gamelib.Sprite;

import java.awt.image.BufferedImage;

public class Heart implements MapObject {
    public Point2D pos;

    public static Sprite sprite;

    public Heart() {
        if (sprite == null) {
            sprite = new Sprite("/res/Heart.png");
        }
        pos = new Point2D();
    }

    Rectangle getBB() {
        Rectangle bb = new Rectangle();
        bb.x = pos.x + 1;
        bb.y = pos.y + 5;
        bb.width = 14;
        bb.height = 10;
        return bb;
    }

    @Override
    public Point2D getPos() {
        return pos;
    }

    @Override
    public void draw(BufferedImage fb, int x, int y) {
        sprite.draw(fb, x, y);
    }
}
