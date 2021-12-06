
package clachipso.gamelib;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.*;

public abstract class Game {
    public static final long MIN_FRAME_TIME_NS = 15666666;
    public static final long FRAME_TIME_NS = 16666667;
    public static final int FIXED_FPS = 60;
    public static final Color CLEAR_COLOR = new Color(80, 0, 80);

    private static JFrame frame;
    private JPanel panel;
    private Canvas canvas;
    private BufferStrategy strategy;
    private boolean running;
    private int screenWidth;
    private int screenHeight;

    private int frameCounter;
    private int fps;
    private long lastFrameNanos;

    private Mouse mouse;
    private Keyboard keyboard;
    private BufferedImage frameBuffer;

    public Game(String title, int windowWidth, int windowHeight, int frameWidth,
            int frameHeight)
    {
        screenWidth = windowWidth;
        screenHeight = windowHeight;
        frameCounter = 0;
        fps = 0;
        lastFrameNanos = System.nanoTime();

        frame = new JFrame(title);
        panel = (JPanel)frame.getContentPane();
        panel.setSize(new Dimension(screenWidth, screenHeight));
        canvas = new Canvas();
        canvas.setBounds(0, 0, screenWidth, screenHeight);
        panel.add(canvas);
        canvas.setIgnoreRepaint(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        strategy = canvas.getBufferStrategy();

        mouse = new Mouse();
        canvas.addMouseListener(mouse);
        canvas.addMouseMotionListener(mouse);

        keyboard = new Keyboard();
        KeyboardFocusManager kbfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        kbfm.addKeyEventDispatcher(keyboard);

        GraphicsConfiguration gc = canvas.getGraphicsConfiguration();
        frameBuffer = gc.createCompatibleImage(frameWidth, frameHeight);

        running = true;
    }

    public abstract void init();
    public abstract void update(int deltaMs, BufferedImage fb, Mouse mouse,
                                Keyboard kb);

    public void run()
    {
        long lastFrameTime = System.nanoTime();
        while (running)
        {
            frame.requestFocus();;
            long currFrameTime = System.nanoTime();
            int deltaMs = (int)((currFrameTime - lastFrameTime) / 1000000);
            lastFrameTime = currFrameTime;


            Graphics2D g2d = (Graphics2D)frameBuffer.getGraphics();
            g2d.setColor(CLEAR_COLOR);
            g2d.fillRect(0, 0, frameBuffer.getWidth(), frameBuffer.getHeight());
            update(deltaMs, frameBuffer, mouse, keyboard);
            g2d = (Graphics2D)strategy.getDrawGraphics();
            g2d.drawImage(frameBuffer, 0, 0, screenWidth, screenHeight, 0, 0,
                    frameBuffer.getWidth(), frameBuffer.getHeight(), null);
            g2d.dispose();
            strategy.show();

            frameCounter++;
            if (System.nanoTime() - lastFrameNanos >= 1000000000)
            {
                fps = frameCounter;
                frameCounter = 0;
                lastFrameNanos = System.nanoTime();
                System.out.println(fps);
            }
            while ((System.nanoTime() - currFrameTime) < MIN_FRAME_TIME_NS)
            {
                try
                {
                    Thread.sleep(0, 999999);
                }
                catch (InterruptedException e)
                {
                }
            }
            while ((System.nanoTime() - currFrameTime) < FRAME_TIME_NS)
            {
                Thread.yield();
            }
        }
    }

    public static void setIcon(BufferedImage image) {
        frame.setIconImage(image);
    }
}
