
package clachipso.gamelib;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener 
{
    public int x;
    public int y;
    public boolean leftDown;
    public boolean rightDown;

    @Override
    public void mouseDragged(MouseEvent event)
    {
        x = event.getX();
        y = event.getY();
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        x = event.getX();
        y = event.getY();
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        // Not used. Included to satisfy interface.
    }

    @Override
    public void mouseEntered(MouseEvent event)
    {
        // Not used. Included to satisfy interface.
    }

    @Override
    public void mouseExited(MouseEvent event)
    {
        // Not used. Included to satisfy interface.
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.getButton() == MouseEvent.BUTTON1)
        {
            leftDown = true;
        }
        else if (event.getButton() == MouseEvent.BUTTON3 ||
                event.getButton() == MouseEvent.BUTTON2)
        {
            rightDown = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        if (event.getButton() == MouseEvent.BUTTON1)
        {
            leftDown = false;
        }
        else if (event.getButton() == MouseEvent.BUTTON3 ||
                event.getButton() == MouseEvent.BUTTON2)
        {
            rightDown = false;
        }
    }
}
