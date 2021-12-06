package clachipso.game;

import clachipso.gamelib.Game;
import clachipso.gamelib.Point2D;
import clachipso.gamelib.Sound;
import clachipso.gamelib.Sprite;

import java.awt.image.BufferedImage;

public class Statue implements MapObject {
    public static final int HEIGHT = 16;
    public static final int WIDTH = 16;

    public enum FireDirection {
        FIRE_UP,
        FIRE_DOWN,
        FIRE_LEFT,
        FIRE_RIGHT
    }

    public static Sprite sprite;
    public static Sprite fireSprite;
    public Point2D pos;
    public static Sound shootSound;

    private int rechargeTime = 0;

    public Statue() {
        if (sprite == null) {
            sprite = new Sprite("/res/Statue.png");
        }
        if (fireSprite == null) {
            fireSprite = new Sprite("/res/StatueFire.png");
        }
        if (shootSound == null) {
            shootSound = new Sound("/res/StatueShoot.wav");
        }
        pos = new Point2D();
    }

    public void update(int deltaMs) {
        rechargeTime -= deltaMs;
        if (rechargeTime < 0) rechargeTime = 0;
    }

    public boolean canFire() {
        if (rechargeTime == 0) return true;
        return false;
    }

    public Bullet fire(FireDirection dir) {
        shootSound.play();
        Bullet bullet = new Bullet();
        bullet.pos.x = pos.x + 5;
        bullet.pos.y = pos.y + 5;
        switch (dir) {
            case FIRE_UP:
                bullet.velocity.x = 0;
                bullet.velocity.y = -Bullet.VELOCITY;
                break;
            case FIRE_DOWN:
                bullet.velocity.x = 0;
                bullet.velocity.y = Bullet.VELOCITY;
                break;
            case FIRE_LEFT:
                bullet.velocity.x = -Bullet.VELOCITY;
                bullet.velocity.y = 0;
                break;
            case FIRE_RIGHT:
                bullet.velocity.x = Bullet.VELOCITY;
                bullet.velocity.y = 0;
                break;
        }
        rechargeTime = 1000;
        return bullet;
    }

    @Override
    public Point2D getPos() {
        return pos;
    }

    @Override
    public void draw(BufferedImage fb, int x, int y) {
        if (rechargeTime > 600) {
            fireSprite.draw(fb, x, y - 8);
        } else {
            sprite.draw(fb, x, y - 8);
        }
    }
}
