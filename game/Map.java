
package clachipso.game;

import clachipso.gamelib.Point2D;
import clachipso.gamelib.Rectangle;
import clachipso.gamelib.Sprite;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

public class Map {
    public static final int TILE_SIZE = 16;
    public static final int VIEW_TILES_WIDE = Darklike.RES_WIDTH / TILE_SIZE;
    public static final int VIEW_TILES_HIGH = Darklike.RES_HEIGHT / TILE_SIZE;
    public static final int MAP_MAX_DIMENSION = 255;
    public static final int MAX_STATUES = 255;
    public static final int MAX_ORBS = 8;

    public int spacesWide;
    public int spacesHigh;
    public int []tiles;
    public boolean []walkFlags;
    public Point2D startSpace;
    public Point2D exitSpace;

    public Player player;
    public LinkedList<DarkOrb> orbs;
    public LinkedList<Statue> statues;
    public LinkedList<Bullet> bullets;
    public Heart heart;

    public static final int TILE_FLOOR_BASIC = 2;
    public static final int TILE_FLOOR_ACCENT_1 = 3;
    public static final int TILE_FLOOR_ACCENT_2 = 4;
    public static final int TILE_EXIT_COVERED = 10;
    public static final int TILE_EXIT_OPEN = 11;
    public static final int TILE_WALL_TOP = 20;
    public static final int TILE_WALL_CORNER = 21;
    public static final int TILE_WALL_LEFT = 22;
    public static final int TILE_WALL_RIGHT = 23;
    public static final int TILE_WALL_BOTTOM = 24;


    private Random rand;
    private LinkedList<MapObject> drawables;
    private LinkedList<MapObject> drawSorted;
    private float[] lightMask;
    private LinkedList<LightSource> lights;


    Sprite floorBase;
    Sprite floorAccent1;
    Sprite floorAccent2;
    Sprite wallTop;
    Sprite wallCorner;
    Sprite wallLeft;
    Sprite wallRight;
    Sprite wallBottom;
    Sprite exitCovered;
    Sprite exitOpened;

    public Map() {
        drawables = new LinkedList<>();
        drawSorted = new LinkedList<>();
        lightMask = new float[Darklike.RES_WIDTH * Darklike.RES_HEIGHT];
        lights = new LinkedList<>();
        statues = new LinkedList<>();
        bullets = new LinkedList<>();
        floorBase = new Sprite("/res/TileBasic.png");
        floorAccent1 = new Sprite("/res/TileAccent1.png");
        floorAccent2 = new Sprite("/res/TileAccent2.png");
        wallTop = new Sprite("/res/WallTop.png");
        wallCorner = new Sprite("/res/WallCorner.png");
        wallLeft = new Sprite("/res/WallLeft.png");
        wallRight = new Sprite("/res/WallRight.png");
        wallBottom = new Sprite("/res/WallBottom.png");
        exitCovered = new Sprite("/res/ExitClosed.png");
        exitOpened = new Sprite("/res/ExitOpen.png");
    }

    public void setSeed(long seed) {
        rand = new Random(System.currentTimeMillis());
    }

    public void generate(int levelNum) {
        int dimension = 15 + levelNum;
        if (dimension > MAP_MAX_DIMENSION) dimension = MAP_MAX_DIMENSION;
        spacesWide = dimension;
        spacesHigh = dimension;

        int numSpaces = spacesWide * spacesHigh;
        tiles = new int[numSpaces];
        walkFlags = new boolean[numSpaces];

        for (int y = 0; y < spacesHigh; y++) {
            for (int x = 0; x < spacesWide; x++) {
                int tileIndex = y * spacesWide + x;
                if (y == 0) {
                    tiles[tileIndex] = TILE_WALL_TOP;
                    walkFlags[tileIndex] = false;
                } else if (x == 0) {
                    tiles[tileIndex] = TILE_WALL_LEFT;
                    walkFlags[tileIndex] = false;
                } else if (x == (spacesWide - 1)) {
                    tiles[tileIndex] = TILE_WALL_RIGHT;
                    walkFlags[tileIndex] = false;
                } else if (y == (spacesHigh - 1)) {
                    tiles[tileIndex] = TILE_WALL_BOTTOM;
                    walkFlags[tileIndex] = false;
                } else {
                    int tile = (rand.nextInt() & Integer.MAX_VALUE) % 16;
                    if (tile == 15) {
                        int accent = (rand.nextInt() & Integer.MAX_VALUE) % 2;
                        if (accent == 1) {
                            tiles[tileIndex] = TILE_FLOOR_ACCENT_1;
                        } else {
                            tiles[tileIndex] = TILE_FLOOR_ACCENT_2;
                        }
                    } else {
                        tiles[tileIndex] = TILE_FLOOR_BASIC;
                    }
                    walkFlags[tileIndex] = true;
                }
            }
        }

        tiles[0] = TILE_WALL_CORNER;
        tiles[spacesWide - 1] = TILE_WALL_CORNER;
        tiles[(spacesHigh - 1) * spacesWide] = TILE_WALL_CORNER;
        tiles[(spacesHigh - 1) * spacesWide + (spacesWide - 1)] = TILE_WALL_CORNER;

        orbs = new LinkedList<>();
        int numOrbs = levelNum;
        if (numOrbs > MAX_ORBS) numOrbs = MAX_ORBS;
        int placedOrbs = 0;
        while (placedOrbs != numOrbs) {
            DarkOrb orb = new DarkOrb();
            orb.pos.x = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesWide - 2);
            orb.pos.y = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesHigh - 2);
            orb.pos.x *= TILE_SIZE;
            orb.pos.y *= TILE_SIZE;

            boolean collided = false;
            for (DarkOrb o : orbs) {
                if (o.pos.x == orb.pos.x && o.pos.y == orb.pos.y) {
                    collided = true;
                    break;
                }
            }
            if (!collided) {
                orbs.push(orb);
                placedOrbs++;
            }
        }

        boolean startPlaced = false;
        while (!startPlaced) {
            startSpace = new Point2D();
            startSpace.x = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesWide - 2);
            startSpace.y = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesHigh - 2);

            boolean collided = false;
            for (DarkOrb orb : orbs) {
                if (orb.pos.x == (startSpace.x * TILE_SIZE) && orb.pos.y == (startSpace.y * TILE_SIZE)) {
                    collided = true;
                    break;
                }
            }

            if (!collided) {
                startPlaced = true;
            }
        }

        boolean exitPlaced = false;
        while (!exitPlaced) {
            exitSpace = new Point2D();
            exitSpace.x = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesWide - 2);
            exitSpace.y = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesHigh - 2);

            boolean collided = false;
            for (DarkOrb orb : orbs) {
                if (orb.pos.x == (exitSpace.x * TILE_SIZE) && orb.pos.y == (exitSpace.y * TILE_SIZE)) {
                    collided = true;
                    break;
                }
            }
            if (startSpace.x == exitSpace.x && startSpace. y == exitSpace.y) {
                collided = true;
            }

            if (!collided) {
                exitPlaced = true;
                tiles[exitSpace.y * spacesWide + exitSpace.x] = 10;
                walkFlags[exitSpace.y * spacesWide + exitSpace.x] = false;
            }
        }

        int numStatues = levelNum;
        if (numStatues > MAX_STATUES) numStatues = MAX_STATUES;
        statues.clear();
        for (int s = 0; s < numStatues; s++) {
            int posX = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesWide - 2);
            int posY = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesHigh - 2);
            int spaceX = posX;
            int spaceY = posY;
            posX *= TILE_SIZE;
            posY *= TILE_SIZE;

            boolean canPlace = true;
            for (DarkOrb currOrb : orbs) {
                if (currOrb.pos.x == posX && currOrb.pos.y == posY) {
                    canPlace = false;
                }
            }
            for (Statue currStatue : statues) {
                if (currStatue.pos.x == posX && currStatue.pos.y == posY) {
                    canPlace = false;
                }
            }
            if ((startSpace.x * TILE_SIZE) == posX ||
                (startSpace.y * TILE_SIZE) == posY) {
                canPlace = false;
            }
            if ((exitSpace.x * TILE_SIZE) == posX ||
                (exitSpace.y * TILE_SIZE) == posY) {
                canPlace = false;
            }

            if (canPlace) {
                Statue statue = new Statue();
                statue.pos.x = posX;
                statue.pos.y = posY;
                statues.push(statue);
                walkFlags[spaceY * spacesWide + spaceX] = false;
            }
        }

        // try to place a heart for the player.
        heart = null;
        boolean canPlace = true;
        Point2D heartPoint = new Point2D();
        heartPoint.x = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesWide - 2);
        heartPoint.y = 1 + (rand.nextInt() & Integer.MAX_VALUE) % (spacesHigh - 2);
        canPlace = walkFlags[heartPoint.y * spacesWide + heartPoint.x];
        heartPoint.x *= TILE_SIZE;
        heartPoint.y *= TILE_SIZE;
        if (heartPoint.x == (startSpace.x * TILE_SIZE) && heartPoint.y == (startSpace.y * TILE_SIZE)) {
            canPlace = false;
        }
        for (DarkOrb orb : orbs) {
            if (heartPoint.x == orb.pos.x && heartPoint.y == orb.pos.y) {
                canPlace = false;
            }
        }
        if (canPlace) {
            heart = new Heart();
            heart.pos = heartPoint;
        }

        bullets.clear();
    }

    public void draw(BufferedImage fb) {
        Graphics2D g2d = (Graphics2D)fb.getGraphics();

        Point2D cam = new Point2D();
        cam.x = ((int)player.pos.x + Player.WIDTH / 2) - Darklike.RES_WIDTH / 2;
        if (cam.x < 0) cam.x = 0;
        if ((cam.x + Darklike.RES_WIDTH) >= (TILE_SIZE * spacesWide)) {
            cam.x = (TILE_SIZE * spacesWide) - Darklike.RES_WIDTH - 1;
        }
        cam.y = ((int)player.pos.y + Player.HEIGHT / 2) - Darklike.RES_HEIGHT / 2;
        if (cam.y < 0) cam.y = 0;
        if ((cam.y + Darklike.RES_HEIGHT) >= (TILE_SIZE * spacesHigh)) {
            cam.y = (TILE_SIZE * spacesHigh) - Darklike.RES_HEIGHT - 1;
        }
        int startTileX = cam.x / TILE_SIZE;
        int startTileY = cam.y / TILE_SIZE;
        int offsetX = cam.x % TILE_SIZE;
        int offsetY = cam.y % TILE_SIZE;

        // Draw tiles
        Color floorcolor = new Color(46, 30, 36);
        for (int tileY = 0; tileY < (VIEW_TILES_HIGH + 1); tileY++) {
            for (int tileX = 0; tileX < (VIEW_TILES_WIDE + 1); tileX++) {
                int spaceX = tileX + startTileX;
                int spaceY = tileY + startTileY;

                int viewX = tileX * TILE_SIZE - offsetX;
                int viewY = tileY * TILE_SIZE - offsetY;

                Color c = Color.white;
                int tileIndex = spaceY * spacesWide + spaceX;
                int tileType = tiles[tileIndex];
                switch(tileType) {
                    case 0:
                        break;
                    case 1:
                        c = Color.DARK_GRAY;
                        g2d.setColor(c);
                        g2d.fillRect(viewX, viewY, TILE_SIZE, TILE_SIZE);
                        break;
                    case TILE_FLOOR_BASIC:
                        floorBase.draw(fb, viewX, viewY);
                        break;
                    case TILE_FLOOR_ACCENT_1:
                        floorAccent1.draw(fb, viewX, viewY);
                        break;
                    case TILE_FLOOR_ACCENT_2:
                        floorAccent2.draw(fb, viewX, viewY);
                        break;
                    case TILE_WALL_TOP:
                        wallTop.draw(fb, viewX, viewY);
                        break;
                    case TILE_WALL_CORNER:
                        wallCorner.draw(fb, viewX, viewY);
                        break;
                    case TILE_WALL_LEFT:
                        wallLeft.draw(fb, viewX, viewY);
                        break;
                    case TILE_WALL_RIGHT:
                        wallRight.draw(fb, viewX, viewY);
                        break;
                    case TILE_WALL_BOTTOM:
                        wallBottom.draw(fb, viewX, viewY);
                        break;
                    case TILE_EXIT_COVERED:
                        exitCovered.draw(fb, viewX, viewY);
                        break;
                    case TILE_EXIT_OPEN:
                        exitOpened.draw(fb, viewX, viewY);
                        break;
                }


            }
        }

        // Compile list of drawables.
        drawables.clear();
        drawSorted.clear();
        drawables.push(player);
        for (DarkOrb orb : orbs) {
            drawables.push(orb);
        }
        for (Statue statue : statues) {
            drawables.push(statue);
        }
        for (Bullet bullet : bullets) {
            drawables.push(bullet);
        }
        if (heart != null) {
            drawables.push(heart);
        }

        // Sort using painter's algorithm (by y coordinate).
        Rectangle viewBB = new Rectangle();
        viewBB.x = 0;
        viewBB.y = 0;
        viewBB.width = Darklike.RES_WIDTH;
        viewBB.height = Darklike.RES_HEIGHT;
        for (MapObject obj : drawables) {
            int viewPosX = obj.getPos().x - cam.x;
            int viewPosY = obj.getPos().y - cam.y;
            //if (!viewBB.containsPoint(viewPosX, viewPosY)) {
            //    continue;
            //}
            if (drawSorted.isEmpty()) {
                drawSorted.push(obj);
                continue;
            }
            ListIterator<MapObject> it = drawSorted.listIterator();
            boolean added = false;
            while (it.hasNext()) {
                MapObject currObj = it.next();
                if (currObj.getPos().y > obj.getPos().y) {
                    it.previous();
                    it.add(obj);
                    added = true;
                    break;
                }
            }
            if (!added) {
                drawSorted.add(obj);
            }
        }
        for (MapObject obj : drawSorted) {
            int viewPosX = obj.getPos().x - cam.x;
            int viewPosY = obj.getPos().y - cam.y;
            obj.draw(fb, viewPosX, viewPosY);
        }

        // Calculate light mask.
        for (int i = 0; i < lightMask.length; i++) lightMask[i] = 0;
        lights.clear();
        lights.add(player);
        for (DarkOrb orb : orbs) {
            lights.add(orb);
        }
        for (Bullet bullet : bullets) {
            lights.add(bullet);
        }

        Rectangle lightBB = new Rectangle();
        for (LightSource light : lights) {
            lightBB.x = (light.getLightPos().x - light.getLightRadius()) - cam.x;
            lightBB.y = (light.getLightPos().y - light.getLightRadius()) - cam.y;
            lightBB.width = light.getLightRadius() * 2;
            lightBB.height = light.getLightRadius() * 2;

            int lightViewX = light.getLightPos().x - cam.x;
            int lightViewY = light.getLightPos().y - cam.y;

            for (int y = 0; y < lightBB.height; y++) {
                int py = lightBB.y + y;
                if (py < 0 || py >= Darklike.RES_HEIGHT) continue;
                for (int x = 0; x < lightBB.width; x++) {
                    int px = lightBB.x + x;
                    if (px < 0 || px >= Darklike.RES_WIDTH) continue;
                    int deltaX = Math.abs(lightViewX - px);
                    int deltaY = Math.abs(lightViewY - py);
                    int distance = (int)Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                    if (distance < light.getLightRadius()) {
                        lightMask[py * Darklike.RES_WIDTH + px] = 1.0f;
                    }
                }
            }
        }


        if (!player.isFlashing()) {
            Color maskColor = new Color(0, 0, 0, 255);
            for (int y = 0; y < Darklike.RES_HEIGHT; y++) {
                for (int x = 0; x < Darklike.RES_WIDTH; x++) {
                    if (lightMask[y * Darklike.RES_WIDTH + x] < 1.0f) {
                        g2d.setColor(maskColor);
                        g2d.fillRect(x, y, 1, 1);
                    }

                }
            }
        } else {
            float fraction = (float)player.flashTimer / (float)Player.MAX_FLASH_TIMER;
            int alpha = (int)(128 * fraction);
            Color maskColor = new Color(255, 255, 255, alpha);
            g2d.setColor(maskColor);
            g2d.fillRect(0, 0, Darklike.RES_WIDTH, Darklike.RES_HEIGHT);
        }

    }

}
