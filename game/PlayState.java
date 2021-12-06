package clachipso.game;

import clachipso.gamelib.*;
import clachipso.gamelib.Rectangle;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Iterator;

public class PlayState {

    private static final Integer UP_KEY = new Integer(KeyEvent.VK_W);
    private static final Integer DOWN_KEY = new Integer(KeyEvent.VK_S);
    private static final Integer LEFT_KEY = new Integer(KeyEvent.VK_A);
    private static final Integer RIGHT_KEY = new Integer(KeyEvent.VK_D);
    private static final Integer FLASH_KEY = new Integer(KeyEvent.VK_SPACE);

    private Map map;
    public Player player;
    public int levelNum;

    private Sprite fullHeart;
    private Sprite halfHeart;
    private Sprite emptyHeart;
    private Sprite deathBlip;
    private boolean deathSpiral;
    private int deathSpiralTimer;

    private int exitNotifyTimer;

    private static Sound pickupSound;
    private static Sound exitOpenSound;
    private static Sound deathSound;
    private static Sound descentSound;
    private boolean deathSoundPlayed;


    public void enter() {
        levelNum = 1;

        map = new Map();
        map.setSeed(1000);
        map.generate(levelNum);

        player = new Player();
        player.pos = new Vector2D();
        player.pos.x = map.startSpace.x * Map.TILE_SIZE;
        player.pos.y = map.startSpace.y * Map.TILE_SIZE;
        map.player = player;

        if (fullHeart == null) fullHeart = new Sprite("/res/HeartFull.png");
        if (halfHeart == null) halfHeart = new Sprite("/res/HeartHalf.png");
        if (emptyHeart == null) emptyHeart = new Sprite("/res/HeartEmpty.png");
        if (deathBlip == null) deathBlip = new Sprite("/res/DeathBlip.png");

        if (pickupSound == null) pickupSound = new Sound("/res/PickupSound.wav");
        if (exitOpenSound == null) exitOpenSound = new Sound("/res/ExitOpenSound.wav");
        if (deathSound == null) {
            deathSound = new Sound("/res/DeathSound.wav");
        }
        if (descentSound == null) {
            descentSound = new Sound("/res/Descend.wav");
        }
        deathSoundPlayed = false;

        deathSpiral = false;
        deathSpiralTimer = 2000;
        exitNotifyTimer = 0;
    }

    public Darklike.GameState update(int deltaMs, BufferedImage fb, Mouse mouse,
                                     Keyboard kb) {

        if (deathSpiral) {
            doDeath(deltaMs, fb);
            if (deathSpiralTimer == 0) {
                return Darklike.GameState.GAME_STATE_GAME_OVER;
            }
            return Darklike.GameState.GAME_STATE_PLAY;
        }

        // Handle Player movement.
        boolean pushUp = false;
        boolean pushDown = false;
        boolean pushLeft = false;
        boolean pushRight = false;
        boolean pushFlash = false;
        if (kb.pushedKeys.contains(UP_KEY)) pushUp = true;
        if (kb.pushedKeys.contains(DOWN_KEY)) pushDown = true;
        if (kb.pushedKeys.contains(LEFT_KEY)) pushLeft = true;
        if (kb.pushedKeys.contains(RIGHT_KEY)) pushRight = true;
        if (kb.pushedKeys.contains(FLASH_KEY)) pushFlash = true;

        float moveDelta = Player.BASE_SPEED * (deltaMs / 1000.0f);
        Vector2D velocity = new Vector2D();
        if (pushUp) velocity.y -= moveDelta;
        if (pushDown) velocity.y += moveDelta;
        if (pushLeft) velocity.x -= moveDelta;
        if (pushRight) velocity.x += moveDelta;

        Rectangle mapBB = new Rectangle();
        mapBB.x = 16;
        mapBB.y = 16;
        mapBB.width = (map.spacesWide - 1) * Map.TILE_SIZE;
        mapBB.height = (map.spacesHigh - 1) * Map.TILE_SIZE;

        Vector2D newPos = new Vector2D();
        newPos.x = player.pos.x + velocity.x;
        newPos.y = player.pos.y;
        boolean inBounds = true;
        if (newPos.x < 0 || (newPos.x + (Player.WIDTH - 1)) >= mapBB.width &&
            newPos.y < 0 || (newPos.y + (Player.HEIGHT - 1)) >= mapBB.height) {
            inBounds = false;
        }
        boolean xMoveValid = false;
        boolean yMoveValid = false;

        Rectangle playerBB = player.getBB();
        if (pushLeft && !pushRight && inBounds) {
            int ulSpaceX = (int)newPos.x / Map.TILE_SIZE;
            int ulSpaceY = (int)newPos.y / Map.TILE_SIZE;
            int llSpaceX = (int)newPos.x / Map.TILE_SIZE;
            int llSpaceY = (int)(newPos.y + Player.HEIGHT - 1) / Map.TILE_SIZE;
            boolean uFlag = map.walkFlags[ulSpaceY * map.spacesWide + ulSpaceX];
            boolean lFlag = map.walkFlags[llSpaceY * map.spacesWide + llSpaceX];
            if (uFlag && lFlag) {
                xMoveValid = true;
            }
        }
        if (pushRight && !pushLeft && inBounds) {
            int urSpaceX = (int)(newPos.x + Player.WIDTH - 1) / Map.TILE_SIZE;
            int urSpaceY = (int)newPos.y / Map.TILE_SIZE;
            int lrSpaceX = (int)(newPos.x + Player.WIDTH - 1) / Map.TILE_SIZE;
            int lrSpaceY = (int)(newPos.y + Player.HEIGHT - 1) / Map.TILE_SIZE;
            boolean uFlag = map.walkFlags[urSpaceY * map.spacesWide + urSpaceX];
            boolean lFlag = map.walkFlags[lrSpaceY * map.spacesWide + lrSpaceX];
            if (uFlag && lFlag) {
                xMoveValid = true;
            }
        }

        newPos.x = player.pos.x;
        newPos.y = player.pos.y + velocity.y;
        inBounds = true;
        if (newPos.x < 0 || (newPos.x + (Player.WIDTH - 1)) >= mapBB.width &&
            newPos.y < 0 || (newPos.y + (Player.HEIGHT - 1)) >= mapBB.height) {
            inBounds = false;
        }
        if (pushDown && !pushUp && inBounds) {
            int llSpaceX = (int)newPos.x / Map.TILE_SIZE;
            int llSpaceY = (int)(newPos.y + Player.HEIGHT - 1) / Map.TILE_SIZE;
            int lrSpaceX = (int)(newPos.x + Player.HEIGHT - 1) / Map.TILE_SIZE;
            int lrSpaceY = (int)(newPos.y + Player.HEIGHT - 1) / Map.TILE_SIZE;
            boolean lFlag = map.walkFlags[llSpaceY * map.spacesWide + llSpaceX];
            boolean rFlag = map.walkFlags[lrSpaceY * map.spacesWide + lrSpaceX];
            if (lFlag && rFlag) {
                yMoveValid = true;
            }
        }
        if (pushUp && !pushDown && inBounds) {
            int ulSpaceX = (int)newPos.x / Map.TILE_SIZE;
            int ulSpaceY = (int)newPos.y / Map.TILE_SIZE;
            int urSpaceX = (int)(newPos.x + Player.HEIGHT - 1) / Map.TILE_SIZE;
            int urSpaceY = (int)newPos.y / Map.TILE_SIZE;
            boolean lFlag = map.walkFlags[ulSpaceY * map.spacesWide + ulSpaceX];
            boolean rFlag = map.walkFlags[urSpaceY * map.spacesWide + urSpaceX];
            if (lFlag && rFlag) {
                yMoveValid = true;
            }
        }

        Vector2D movement = new Vector2D();
        if (xMoveValid) movement.x += velocity.x;
        if (yMoveValid) movement.y += velocity.y;
        if (xMoveValid && yMoveValid) {
            movement.x *= 0.85f;
            movement.y *= 0.85f;
        }
        player.pos.x += movement.x;
        player.pos.y += movement.y;

        if (exitNotifyTimer > 0) {
            exitNotifyTimer -= deltaMs;
            if (exitNotifyTimer < 0) exitNotifyTimer = 0;
        }

        // Check if player collides with orbs.
        Rectangle targetBB = new Rectangle();
        Iterator<DarkOrb> orbIt = map.orbs.iterator();
        while (orbIt.hasNext()) {
            DarkOrb orb = orbIt.next();
            targetBB.x = orb.pos.x;
            targetBB.y = orb.pos.y;
            targetBB.width = DarkOrb.WIDTH;
            targetBB.height = DarkOrb.HEIGHT;

            if (Rectangle.collides(playerBB, targetBB)) {
                player.orbCount++;
                orbIt.remove();
                if (map.orbs.size() == 0) {
                    exitOpenSound.play();
                    exitNotifyTimer = 1500;
                }
                pickupSound.play();
            }
        }


        // Check if player flashed.
        if (pushFlash) player.flash();

        // Check if exit is open.
        if (map.orbs.size() == 0) {
            int exitIndex = map.exitSpace.y * map.spacesWide + map.exitSpace.x;
            map.walkFlags[exitIndex] = true;
            map.tiles[exitIndex] = Map.TILE_EXIT_OPEN;
        }

        // Check if player made it to exit.
        targetBB.x = (map.exitSpace.x * Map.TILE_SIZE) + 4;
        targetBB.y = (map.exitSpace.y * Map.TILE_SIZE) + 4;
        targetBB.width = Map.TILE_SIZE / 2;
        targetBB.height = Map.TILE_SIZE / 2;
        if (Rectangle.collides(playerBB, targetBB)) {
            // Hooray!
            descentSound.play();
            levelNum++;
            map.generate(levelNum);
            player.pos.x = map.startSpace.x * Map.TILE_SIZE;
            player.pos.y = map.startSpace.y * Map.TILE_SIZE;
            map.player = player;
        }

        // Update relevant stuff.
        player.update(deltaMs, pushUp, pushDown, pushLeft, pushRight);
        for (DarkOrb orb : map.orbs) {
            orb.update(deltaMs);
        }
        Iterator<Bullet> bullIt = map.bullets.iterator();
        mapBB.x = 0;
        mapBB.y = 0;
        mapBB.width = (map.spacesWide * Map.TILE_SIZE);
        mapBB.height = (map.spacesHigh * Map.TILE_SIZE);
        while (bullIt.hasNext()) {
            Bullet bullet = bullIt.next();
            Rectangle bulletBB = bullet.getBB();

            if (Rectangle.collides(playerBB, bulletBB)) {
                player.hit();
                bullIt.remove();
            }

            bullet.update(deltaMs);

            // Remove if out of bounds.
            if (!Rectangle.collides(mapBB, bulletBB)) {
                bullIt.remove();
            }
        }
        for (Statue statue : map.statues) {
            statue.update(deltaMs);

            // Check if player is to left.
            Rectangle statueBB = new Rectangle();
            statueBB.x = 0;
            statueBB.y = statue.pos.y;
            statueBB.width = statue.pos.x;
            statueBB.height = Statue.HEIGHT;
            if (Rectangle.collides(playerBB, statueBB) && statue.canFire()) {
                map.bullets.push(statue.fire(Statue.FireDirection.FIRE_LEFT));
                continue;
            }

            // Check if player is to right
            statueBB.x = statue.pos.x;
            statueBB.y = statue.pos.y;
            statueBB.width = (map.spacesWide * Map.TILE_SIZE) - statueBB.x;
            statueBB.height = Statue.HEIGHT;
            if (Rectangle.collides(playerBB, statueBB) && statue.canFire()) {
                map.bullets.push(statue.fire(Statue.FireDirection.FIRE_RIGHT));
                continue;
            }

            // Check if player is above.
            statueBB.x = statue.pos.x;
            statueBB.y = 0;
            statueBB.width = Statue.WIDTH;
            statueBB.height = statue.pos.y;
            if (Rectangle.collides(playerBB, statueBB) && statue.canFire()) {
                map.bullets.push(statue.fire(Statue.FireDirection.FIRE_UP));
                continue;
            }

            // Check if player is below.
            statueBB.x = statue.pos.x;
            statueBB.y = statue.pos.y;
            statueBB.width = Statue.WIDTH;
            statueBB.height = (map.spacesHigh * Map.TILE_SIZE) - statue.pos.y;
            if (Rectangle.collides(playerBB, statueBB) && statue.canFire()) {
                map.bullets.push(statue.fire(Statue.FireDirection.FIRE_DOWN));
                continue;
            }
        }

        // Check if player picked up a heart.
        if (map.heart != null) {
            if (Rectangle.collides(playerBB, map.heart.getBB())) {
                player.life += 2;
                if (player.life > Player.MAX_LIFE) player.life = Player.MAX_LIFE;
                map.heart = null;
                pickupSound.play();
             }
        }

        // Check if the player died :(
        if (player.life <= 0) {
            deathSpiral = true;
        }

        // Draw the scene.
        map.draw(fb);

        for (int i = 0; i < 5; i++) {
            int dx = 180 + i * 12;
            int dy = 8;
            int heartVal = player.life - (8 - i * 2);
            if (heartVal <= 0) {
                emptyHeart.draw(fb, dx, dy);
            } else if (heartVal == 1) {
                halfHeart.draw(fb, dx, dy);
            } else {
                fullHeart.draw(fb, dx, dy);
            }
        }

        Darklike.GAME_FONT.drawText(fb, "ORBS:" + player.orbCount, 8, 4,
                Color.WHITE);
        Darklike.GAME_FONT.drawText(fb, "LEVEL:" + levelNum, 8,
                Darklike.RES_HEIGHT - 15, Color.WHITE);
        Darklike.GAME_FONT.drawText(fb, "FLASHES:" + player.flashSpells,
                160, Darklike.RES_HEIGHT - 15, Color.WHITE);

        if (exitNotifyTimer > 0) {
            int strSize = Darklike.GAME_FONT.getStringWidth("EXIT OPENED");
            int strX = (Darklike.RES_WIDTH - strSize) / 2;
            Darklike.GAME_FONT.drawText(fb, "EXIT OPENED", strX, 92,
                    Color.WHITE);
        }


        return Darklike.GameState.GAME_STATE_PLAY;
    }

    public void doDeath(int deltaMs, BufferedImage fb) {
        deathSpiralTimer -= deltaMs;
        if (deathSpiralTimer <= 0) {
            deathSpiralTimer = 0;
        }

        Graphics2D g2d = (Graphics2D)fb.getGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, Darklike.RES_WIDTH, Darklike.RES_HEIGHT);
        Point2D cam = new Point2D();
        cam.x = ((int)player.pos.x + Player.WIDTH / 2) - Darklike.RES_WIDTH / 2;
        if (cam.x < 0) cam.x = 0;
        if ((cam.x + Darklike.RES_WIDTH) >= (Map.TILE_SIZE * map.spacesWide)) {
            cam.x = (Map.TILE_SIZE * map.spacesWide) - Darklike.RES_WIDTH - 1;
        }
        cam.y = ((int)player.pos.y + Player.HEIGHT / 2) - Darklike.RES_HEIGHT / 2;
        if (cam.y < 0) cam.y = 0;
        if ((cam.y + Darklike.RES_HEIGHT) >= (Map.TILE_SIZE * map.spacesHigh)) {
            cam.y = (Map.TILE_SIZE * map.spacesHigh) - Darklike.RES_HEIGHT - 1;
        }

        if (deathSpiralTimer > 500) {
            player.draw(fb, (int)player.pos.x - cam.x,
                    (int)player.pos.y - cam.y);
        } else {
            deathBlip.draw(fb, (int)player.pos.x - cam.x,
                    (int)player.pos.y - cam.y);
            if (deathSoundPlayed == false) {
                deathSound.play();
                deathSoundPlayed = true;
            }
        }
    }
}
