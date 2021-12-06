package clachipso.game;

import clachipso.gamelib.Point2D;
import clachipso.gamelib.Rectangle;
import clachipso.gamelib.Sprite;

import java.awt.image.BufferedImage;

public class Bullet implements MapObject, LightSource {
    public static final int WIDTH = 6;
    public static final int HEIGHT = 6;
    public static final int VELOCITY = 64;

    public static Sprite[] sprites;
    public Point2D pos;
    public Point2D velocity;

    private int animSeq = 0;
    private int animTime = 0;

    public Bullet() {
        if (sprites == null) {
            sprites = new Sprite[3];
            sprites[0] = new Sprite("/res/Bullet1.png");
            sprites[1] = new Sprite("/res/Bullet2.png");
            sprites[2] = new Sprite("/res/Bullet3.png");
        }

        pos = new Point2D();
        velocity = new Point2D();
    }

    public void update(int deltaMs) {
        animTime += deltaMs;
        if (animTime >= 80) {
            animTime = 0;
            animSeq++;
            if (animSeq > 2) {
                animSeq = 0;
            }
        }

        pos.x += (int)(velocity.x * (deltaMs / 1000.0f));
        pos.y += (int)(velocity.y * (deltaMs / 1000.0f));
    }

    @Override
    public Point2D getPos() {
        return pos;
    }

    public Rectangle getBB() {
        Rectangle bb = new Rectangle();
        bb.x = pos.x;
        bb.y = pos.y;
        bb.width = WIDTH;
        bb.height = HEIGHT;
        return bb;
    }

    @Override
    public void draw(BufferedImage fb, int x, int y) {
        sprites[animSeq].draw(fb, x, y);
    }

    @Override
    public Point2D getLightPos() {
        Point2D lightPos = new Point2D();
        lightPos.x = pos.x + 3;
        lightPos.y = pos.y + 3;
        return lightPos;
    }

    @Override
    public int getLightRadius() {
        return 16 + animSeq;
    }
}
