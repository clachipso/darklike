package clachipso.game;

import clachipso.gamelib.*;

import java.awt.image.BufferedImage;

public class Darklike extends Game {

    public static final String TITLE = "Darklike";
    public static final int RES_WIDTH = 240;
    public static final int RES_HEIGHT = 192;
    public static final int SCALE = 3;

    public static BitmapFont GAME_FONT;
    private static Sound music;

    public enum GameState {
        GAME_STATE_TITLE,
        GAME_STATE_PLAY,
        GAME_STATE_GAME_OVER
    };


    private GameState currState;
    private TitleState titleState;
    private PlayState playState;
    private GameOverState gameOverState;

    public Darklike() {
        super(TITLE, RES_WIDTH * SCALE, RES_HEIGHT * SCALE, RES_WIDTH,
                RES_HEIGHT);
        try {
            GAME_FONT = BitmapFont.loadFromFiles("/res/ldfont.fnt",
                    "/res/ldfont.png");
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        music = new Sound("/res/HeavyMetalChiptuneBobRossFinal.wav");
        Sprite tempSprite = new Sprite("/res/Statue.png");
        Game.setIcon(tempSprite.image);
    }

    @Override
    public void init() {
        currState = GameState.GAME_STATE_TITLE;
        titleState = new TitleState();
        playState = new PlayState();
        gameOverState = new GameOverState();

        titleState.enter();
    }

    @Override
    public void update(int deltaMs, BufferedImage fb, Mouse mouse, Keyboard kb) {
        if (!music.isPlaying()) {
            music.play();
        }

        GameState nextState = null;
        switch (currState) {
            case GAME_STATE_TITLE:
                nextState = titleState.update(deltaMs, fb, mouse, kb);
                break;
            case GAME_STATE_PLAY:
                nextState = playState.update(deltaMs, fb, mouse, kb);
                break;
            case GAME_STATE_GAME_OVER:
                nextState = gameOverState.update(deltaMs, fb, mouse, kb);
                break;
        }

        if (nextState != currState) {
            switch (nextState) {
                case GAME_STATE_TITLE:
                    titleState.enter();
                    currState = GameState.GAME_STATE_TITLE;
                    break;
                case GAME_STATE_PLAY:
                    currState = GameState.GAME_STATE_PLAY;
                    playState.enter();
                    break;
                case GAME_STATE_GAME_OVER:
                    currState = GameState.GAME_STATE_GAME_OVER;
                    gameOverState.enter(playState.player.orbCount,
                            playState.levelNum);
                    break;
            }
        }
    }

    public static void main(String[] args) {
        Darklike game = new Darklike();
        game.init();
        game.run();
    }
}
