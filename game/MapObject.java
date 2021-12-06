package clachipso.game;

import clachipso.gamelib.Point2D;

import java.awt.image.BufferedImage;

public interface MapObject {
    Point2D getPos();
    void draw(BufferedImage fb, int x, int y);
}
