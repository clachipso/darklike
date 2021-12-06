package clachipso.game;

import clachipso.gamelib.*;

import java.awt.image.BufferedImage;

public class Player implements MapObject, LightSource {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    public static final float BASE_SPEED = 64.0f;
    public static final int MAX_LIFE = 10;
    public static final int MAX_FLASH_SPELLS = 3;
    public static final int MAX_FLASH_TIMER = 2000;

    public Vector2D pos;
    public int orbCount;
    public int life;
    public int flashSpells;

    private static final int BASE_LIGHT_RADIUS = 120;
    private static final int MIN_LIGHT_RADIUS = 8;
    private static final int ANIMATION_FRAMES = 4;
    private static final int FRAME_TIME = 125;

    private enum Direction {
        DIR_UP,
        DIR_DOWN,
        DIR_LEFT,
        DIR_RIGHT
    };
    Direction currDirection;

    private float lightFlicker = 0;
    private float flickerDir = 1;

    private Sprite[] walkDownSprites;
    private Sprite[] walkUpSprites;
    private Sprite[] walkLeftSprites;
    private Sprite[] walkRightSprites;
    private Sprite currSprite;
    private int animTimer;
    private int animFrame;
    private static Sound hitSound;
    private static Sound flashSound;


    public int flashTimer;

    public Player() {
        walkDownSprites = new Sprite[ANIMATION_FRAMES];
        walkUpSprites = new Sprite[ANIMATION_FRAMES];
        walkLeftSprites = new Sprite[ANIMATION_FRAMES];
        walkRightSprites = new Sprite[ANIMATION_FRAMES];

        walkDownSprites[0] = new Sprite("/res/Priestess1.png");
        walkDownSprites[1] = new Sprite("/res/Priestess2.png");
        walkDownSprites[2] = new Sprite("/res/Priestess3.png");
        walkDownSprites[3] = new Sprite("/res/Priestess4.png");
        walkUpSprites[0] = new Sprite("/res/Priestess5.png");
        walkUpSprites[1] = new Sprite("/res/Priestess6.png");
        walkUpSprites[2] = new Sprite("/res/Priestess7.png");
        walkUpSprites[3] = new Sprite("/res/Priestess8.png");
        walkRightSprites[0] = new Sprite("/res/Priestess9.png");
        walkRightSprites[1] = new Sprite("/res/Priestess10.png");
        walkRightSprites[2] = new Sprite("/res/Priestess11.png");
        walkRightSprites[3] = new Sprite("/res/Priestess12.png");
        walkLeftSprites[0] = new Sprite("/res/Priestess13.png");
        walkLeftSprites[1] = new Sprite("/res/Priestess14.png");
        walkLeftSprites[2] = new Sprite("/res/Priestess15.png");
        walkLeftSprites[3] = new Sprite("/res/Priestess16.png");

        if (hitSound == null) {
            hitSound = new Sound("/res/PlayerHit.wav");
        }
        if (flashSound == null) {
            flashSound = new Sound("/res/FlashSound.wav");
        }

        pos = new Vector2D();
        orbCount = 0;

        life = MAX_LIFE;
        flashSpells = MAX_FLASH_SPELLS;
        flashTimer = 0;

        currDirection = Direction.DIR_DOWN;
        currSprite = walkDownSprites[0];
        animFrame = 0;
        animTimer = 0;
    }

    void update(int deltaMs, boolean moveUp, boolean moveDown, boolean moveLeft,
                boolean moveRight) {
        int radius = BASE_LIGHT_RADIUS - (orbCount * DarkOrb.LIGHT_REDUCTION);
        float flickerBase = radius * 0.25f;
        if (flickerBase < 2) flickerBase = 2;
        float flickerAmount = flickerBase * (deltaMs / 1000.0f) * flickerDir;
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
        flashTimer -= deltaMs;

        animTimer += deltaMs;
        if (moveUp) {
            if (currDirection != Direction.DIR_UP) {
                currDirection = Direction.DIR_UP;
                animTimer = 0;
            } else {
                if (animTimer >= FRAME_TIME) {
                    animTimer = 0;
                    animFrame++;
                    if (animFrame >= ANIMATION_FRAMES) animFrame = 0;
                }
            }
        } else if (moveDown) {
            if (currDirection != Direction.DIR_DOWN) {
                currDirection = Direction.DIR_DOWN;
                animTimer = 0;
            } else {
                if (animTimer >= FRAME_TIME) {
                    animTimer = 0;
                    animFrame++;
                    if (animFrame >= ANIMATION_FRAMES) animFrame = 0;
                }
            }
        } else if (moveLeft) {
            if (currDirection != Direction.DIR_LEFT) {
                currDirection = Direction.DIR_LEFT;
                animTimer = 0;
            } else {
                if (animTimer >= FRAME_TIME) {
                    animTimer = 0;
                    animFrame++;
                    if (animFrame >= ANIMATION_FRAMES) animFrame = 0;
                }
            }
        } else if (moveRight) {
            if (currDirection != Direction.DIR_RIGHT) {
                currDirection = Direction.DIR_RIGHT;
                animTimer = 0;
            } else {
                if (animTimer >= FRAME_TIME) {
                    animTimer = 0;
                    animFrame++;
                    if (animFrame >= ANIMATION_FRAMES) animFrame = 0;
                }
            }
        } else {
            animTimer = 0;
            animFrame = 0;
        }

        switch (currDirection) {
            case DIR_UP:
                currSprite = walkUpSprites[animFrame];
                break;
            case DIR_DOWN:
                currSprite = walkDownSprites[animFrame];
                break;
            case DIR_LEFT:
                currSprite = walkLeftSprites[animFrame];
                break;
            case DIR_RIGHT:
                currSprite = walkRightSprites[animFrame];
                break;
        }
    }

    public boolean isFlashing() {
        return (flashTimer > 0);
    }

    public void flash() {
        if (flashTimer <= 0 && flashSpells > 0) {
            flashTimer = MAX_FLASH_TIMER;
            flashSpells--;
            flashSound.play();
        }
    }

    void hit() {
        hitSound.play();
        life--;
        if (life < 0) life = 0;
    }

    @Override
    public Point2D getPos() {
        Point2D p = new Point2D();
        p.x = (int)pos.x;
        p.y = (int)pos.y;
        return p;
    }

    public Rectangle getBB() {
        Rectangle bb = new Rectangle();
        bb.x = (int)pos.x + 3;
        bb.y = (int)pos.y + 8;
        bb.width = 10;
        bb.height = 8;
        return bb;
    }

    public void draw(BufferedImage fb, int x, int y) {
        currSprite.draw(fb, x, y - 8);
    }

    @Override
    public Point2D getLightPos() {
        Point2D lightPos = new Point2D();
        lightPos.x = (int)pos.x + (WIDTH / 2);
        lightPos.y = (int)pos.y + (HEIGHT / 2) - 4;
        return lightPos;
    }

    @Override
    public int getLightRadius() {
        int radius = BASE_LIGHT_RADIUS - (orbCount * DarkOrb.LIGHT_REDUCTION);
        if (radius < MIN_LIGHT_RADIUS) radius = MIN_LIGHT_RADIUS;
        radius += (int)lightFlicker;
        return radius;
    }
}
