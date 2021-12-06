package clachipso.gamelib;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;

public class Keyboard implements KeyEventDispatcher {
    public LinkedList<Integer> pushedKeys;
    private int key;

    public Keyboard() {
        pushedKeys = new LinkedList();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED) {
            Integer keyCode = new Integer(e.getKeyCode());
            if (pushedKeys.contains(keyCode)) {
                return false;
            }
            pushedKeys.push(keyCode);
            System.out.println("key down " + e.getKeyCode());
        } else if (e.getID() == KeyEvent.KEY_RELEASED) {
            int code = e.getKeyCode();
            Iterator<Integer> it = pushedKeys.iterator();
            while (it.hasNext()) {
                Integer key = it.next();
                if (key.intValue() == code) {
                    System.out.println("key up " + key.intValue());
                    it.remove();
                    break;
                }
            }
        }
        return false;
    }

}
