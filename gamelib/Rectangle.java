package clachipso.gamelib;

public class Rectangle
{
    public int x;
    public int y;
    public int width;
    public int height;

    public boolean containsPoint(int px, int py)
    {
        if (px >= x && px < (x + width) && py >= y && py < (y + height))
        {
            return true;
        }
        return false;
    }

    public static boolean collides(Rectangle a, Rectangle b)
    {
        boolean collision = false;
        if (a.x < (b.x + b.width) && (a.x + a.width) > b.x &&
                a.y < (b.y + b.height) && (a.y + a.height) > b.y)
        {
            collision = true;
        }
        return collision;
    }
}
