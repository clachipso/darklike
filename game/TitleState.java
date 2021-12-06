package clachipso.game;

import clachipso.gamelib.BitmapFont;
import clachipso.gamelib.Keyboard;
import clachipso.gamelib.Mouse;
import clachipso.gamelib.Sprite;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class TitleState {

    private Sprite titleBG;
    private DarkOrb orb;

    private boolean spaceDown;

    private static final String inst1Line0 = "PRIESTES,";
    private static final String inst1Line1 = "CLEANSE THIS PLACE.";
    private static final String inst1Line2 = "COLLECT THE DARK ORBS,";
    private static final String inst1Line3 = "BUT THEY WEAKEN THY LIGHT.";
    private static final String inst1Line4 = "BEWARE THE STATUES.";
    private static final String inst1Line5 = "WASD = MOVE";
    private static final String inst1Line6 = "SPACE = FLASH SPELL";

    private enum SubState {
        SUB_TITLE,
        SUB_INST1,
        SUB_INST2
    }
    private SubState currState;

    public void enter() {
        if (titleBG == null) {
            titleBG = new Sprite("/res/TitleBG.png");
        }
        currState = SubState.SUB_TITLE;
        orb = new DarkOrb();
    }


    public Darklike.GameState update(int deltaMs, BufferedImage fb, Mouse mouse,
                                     Keyboard kb) {
        orb.update(deltaMs);
        int strWidth;
        int strX;
        BitmapFont font = Darklike.GAME_FONT;
        Graphics2D g2d = (Graphics2D)fb.getGraphics();
        g2d.setColor(new Color(27, 38, 50));
        g2d.fillRect(0, 0, Darklike.RES_WIDTH, Darklike.RES_HEIGHT);
        switch (currState) {
            case SUB_TITLE:
                titleBG.draw(fb, 0, 0);
                orb.draw(fb, Darklike.RES_WIDTH / 2 - 8, 120);
                strWidth = font.getStringWidth("PRESS SPACE");
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, "PRESS SPACE", strX, Darklike.RES_HEIGHT - 15,
                        Color.WHITE);
                break;
            case SUB_INST1:
                strWidth = font.getStringWidth(inst1Line0);
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, inst1Line0, strX, 16, Color.WHITE);

                strWidth = font.getStringWidth(inst1Line1);
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, inst1Line1, strX, 28, Color.WHITE);

                strWidth = font.getStringWidth(inst1Line2);
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, inst1Line2, strX, 40, Color.WHITE);

                strWidth = font.getStringWidth(inst1Line3);
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, inst1Line3, strX, 52, Color.WHITE);

                strWidth = font.getStringWidth(inst1Line4);
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, inst1Line4, strX, 64, Color.WHITE);

                strWidth = font.getStringWidth(inst1Line5);
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, inst1Line5, strX, 96, Color.WHITE);

                strWidth = font.getStringWidth(inst1Line6);
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                font.drawText(fb, inst1Line6, strX, 108, Color.WHITE);


                strWidth = Darklike.GAME_FONT.getStringWidth("PRESS SPACE");
                strX = (Darklike.RES_WIDTH - strWidth) / 2;
                Darklike.GAME_FONT.drawText(fb, "PRESS SPACE", strX,
                        Darklike.RES_HEIGHT - 15, Color.WHITE);
                break;
            case SUB_INST2:
                break;
        }

        boolean advance = false;
        if (kb.pushedKeys.contains(new Integer(KeyEvent.VK_SPACE))) {
            spaceDown = true;
        } else {
            if (spaceDown) {
                spaceDown = false;
                advance = true;
            }
            spaceDown = false;
        }

        if (advance) {
            switch (currState) {
                case SUB_TITLE:
                    currState = SubState.SUB_INST1;
                    break;
                case SUB_INST1:
                    return Darklike.GameState.GAME_STATE_PLAY;
                case SUB_INST2:
                    return Darklike.GameState.GAME_STATE_PLAY;
                    //break;
            }
        }

        return Darklike.GameState.GAME_STATE_TITLE;
    }
}
