
package clachipso.game;

import clachipso.gamelib.Game;
import clachipso.gamelib.Point2D;
import clachipso.gamelib.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DarkOrb implements MapObject, LightSource {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final int LIGHT_RADIUS = 20;
    public static final int LIGHT_REDUCTION = 4;

    public Point2D pos;

    private float lightFlicker = 0;
    private float flickerDir = 1;

    private static Sprite orb1;
    private static Sprite orb2;
    private static Sprite orb3;
    private static Sprite orb4;
    private int animSeq = 0;
    private int animCounter = 0;

    public DarkOrb() {
        pos = new Point2D();
        if (orb1 == null) {
            orb1 = new Sprite("/res/Orb1.png");
        }
        if (orb2 == null) {
            orb2 = new Sprite("/res/Orb2.png");
        }
        if (orb3 == null) {
            orb3 = new Sprite("/res/Orb3.png");
        }
        if (orb4 == null) {
            orb4 = new Sprite("/res/Orb4.png");
        }
    }

    public void update(int deltaMs) {
        float flickerBase = LIGHT_RADIUS * 0.25f;
        if (flickerBase < 2) flickerBase = 2;
        float flickerAmount = flickerBase * (deltaMs / 866.0f) * flickerDir;
        lightFlicker += flickerAmount;
        if (flickerDir == 1) {
            if (lightFlicker >= flickerBase) {
                lightFlicker = flickerBase;
                flickerDir = -1;
            }
        } else {
            if (lightFlicker < 0) {
                lightFlicker = 0;
                flickerDir = 1;
            }
        }

        animCounter += deltaMs;
        if (animCounter >= 80) {
            animCounter = 0;
            animSeq++;
            if (animSeq > 3) animSeq = 0;
        }

    }

    @Override
    public Point2D getPos() {
        return pos;
    }

    public void draw(BufferedImage fb, int x, int y) {
        Graphics2D g2d = (Graphics2D)fb.getGraphics();
        switch (animSeq) {
            case 0:
                orb1.draw(fb, x, y);
                break;
            case 1:
                orb2.draw(fb, x, y);
                break;
            case 2:
                orb3.draw(fb, x, y);
                break;
            case 3:
                orb4.draw(fb, x , y);
                break;
        }
    }

    @Override
    public Point2D getLightPos() {
        Point2D lightPos = new Point2D();
        lightPos.x = pos.x + (WIDTH / 2);
        lightPos.y = pos.y + 3 + animSeq;
        return lightPos;
    }

    @Override
    public int getLightRadius() {
        return LIGHT_RADIUS + (int)lightFlicker;
    }
}
