package clachipso.game;

import clachipso.gamelib.Keyboard;
import clachipso.gamelib.Mouse;
import clachipso.gamelib.Sound;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class GameOverState {

    private int orbCount;
    private int levelCount;
    private boolean confirmDown;

    private DarkOrb orb;



    void enter(int orbs, int level) {
        orbCount = orbs;
        levelCount = level;
        confirmDown = false;
        orb = new DarkOrb();
    }

    public Darklike.GameState update(int deltaMs, BufferedImage fb, Mouse mouse,
                                     Keyboard kb) {

        orb.update(deltaMs);

        Graphics2D g2d = (Graphics2D)fb.getGraphics();
        g2d.setColor(new Color(27, 38, 50));
        g2d.fillRect(0, 0, Darklike.RES_WIDTH, Darklike.RES_HEIGHT);


        int strSize =
                Darklike.GAME_FONT.getStringWidth("PRESS SPACE TO TRY AGAIN");
        int strX = (Darklike.RES_WIDTH - strSize) / 2;
        Darklike.GAME_FONT.drawText(fb, "PRESS SPACE TO TRY AGAIN",
                strX, Darklike.RES_HEIGHT - 23, Color.WHITE);

        strSize = Darklike.GAME_FONT.getStringWidth("GAME OVER");
        strX = (Darklike.RES_WIDTH - strSize) / 2;
        Darklike.GAME_FONT.drawText(fb, "GAME OVER", strX, 24, new Color(190, 38, 51));

        Darklike.GAME_FONT.drawText(fb, "ORBS:  " + orbCount, 80, 80, Color.WHITE);
        Darklike.GAME_FONT.drawText(fb, "LEVEL: " + levelCount, 80, 96, Color.WHITE);

        orb.draw(fb, Darklike.RES_WIDTH / 2 - 8, 120);


        if (kb.pushedKeys.contains(KeyEvent.VK_SPACE)) {
            confirmDown = true;
        } else if (confirmDown) {
            return Darklike.GameState.GAME_STATE_PLAY;
        }

        return Darklike.GameState.GAME_STATE_GAME_OVER;
    }
}
