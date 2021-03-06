package smartrockets;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.imageio.ImageIO;

public class SmartRockets implements Runnable, MouseListener, MouseMotionListener, KeyListener {

    public static int UPDATES_PER_SEC = 60;
    public static int FRAMES_PER_SEC = 60;
    private double nsPerFrame, nsPerUpdate;
    private boolean isRunning;

    public static Random rng;
    private static int[][] map;
    private double drawOffsetX = 50, drawOffsetY = 50, mouseX, mouseY;
    private static int tileSize = 10;
    private GameFrame frame;
    private boolean camMove = false, drawTool = false, eraseTool = false, fastForward = false, megaEvolve = false;

    private RocketLauncher rocketLauncher;

    public SmartRockets() {
        rng = new Random();
        map = new int[100][100];
        frame = new GameFrame();
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        frame.addKeyListener(this);
        rocketLauncher = new RocketLauncher();
        isRunning = true;
        new Thread(this).start();
    }

    @Override
    public void run() {

        nsPerUpdate = 1000000000.0 / UPDATES_PER_SEC;
        nsPerFrame = 1000000000.0 / FRAMES_PER_SEC;

        long lastTime = System.nanoTime();
        double unprocessedTime = 0;
        double unprocessedTimeFPS = 0;

        while (isRunning) {

            long currentTime = System.nanoTime();
            long passedTime = currentTime - lastTime;
            lastTime = currentTime;
            unprocessedTime += passedTime;
            unprocessedTimeFPS += passedTime;

            if (unprocessedTime >= nsPerUpdate) {
                unprocessedTime = 0;
                update();
            }

            if (unprocessedTimeFPS >= nsPerFrame) {
                unprocessedTimeFPS = 0;
                render();
            }

        }
        dispose();
    }

    private void update() {
        rocketLauncher.update();
        if (fastForward) {
            fastForward = false;
            for (int i = 0; i < 300; i++) {
                update();
            }
        }
        if (megaEvolve) {
            megaEvolve = false;
            for (int i = 0; i < 100000; i++) {
                update();
            }
        }
    }

    public void dispose() {
        System.exit(0);
    }

    private void render() {

        BufferStrategy bs = frame.getBufferStrategy();
        if (bs == null) {
            frame.createBufferStrategy(2);
            return;
        }
        Graphics g = bs.getDrawGraphics();

        g.setColor(Color.darkGray);
        g.fillRect(0, 0, frame.getWidth(), frame.getHeight());

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map.length; y++) {
                if (map[x][y] == 0) {
                    g.setColor(Color.gray);
                } else {
                    g.setColor(Color.black);
                }
                g.fillRect((int) drawOffsetX + x * tileSize, (int) drawOffsetY + y * tileSize, tileSize, tileSize);
            }
        }
        rocketLauncher.draw((int) drawOffsetX, (int) drawOffsetY, g);
        g.setColor(Color.green);
        g.setFont(new Font("LucidaSans", Font.BOLD, 15));
        g.drawString("Commands:", 25, 75);
        g.drawString("T - Move Target To Mouse", 25, 100);
        g.drawString("R - Move Launcher To Mouse", 25, 125);
        g.drawString("E - Fast Forward", 25, 150);
        g.drawString("W - Fast Evolve?", 25, 175);
        g.drawString("Left Mouse Button - Draw Walls", 25, 200);
        g.drawString("Right Mouse Button - Erase Walls", 25, 225);
        g.dispose();
        bs.show();
    }

    public static boolean isPointPathable(int x, int y) {
        try {
            return map[x / tileSize][y / tileSize] == 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void changeTile(int x, int y, int type) {
        try {
            map[(x - (int) drawOffsetX) / tileSize][(y - (int) drawOffsetY) / tileSize] = type;
        } catch (Exception e) {
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                drawTool = true;
                break;
            case MouseEvent.BUTTON2:
                camMove = true;
                break;
            case MouseEvent.BUTTON3:
                eraseTool = true;
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                drawTool = false;
                break;
            case MouseEvent.BUTTON2:
                camMove = false;
                break;
            case MouseEvent.BUTTON3:
                eraseTool = false;
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (camMove) {
            drawOffsetX += e.getX() - mouseX;
            drawOffsetY += e.getY() - mouseY;
        }
        if (drawTool) {
            changeTile(e.getX(), e.getY(), 1);
        }
        if (eraseTool) {
            changeTile(e.getX(), e.getY(), 0);
        }
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_R:
                rocketLauncher.moveLauncher((int) (mouseX - drawOffsetX), (int) (mouseY - drawOffsetY));
                break;
            case KeyEvent.VK_T:
                rocketLauncher.moveTarget((int) (mouseX - drawOffsetX), (int) (mouseY - drawOffsetY));
                break;
            case KeyEvent.VK_E:
                fastForward = true;
                break;
            case KeyEvent.VK_W:
                megaEvolve = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(smartrockets.SmartRockets.class.getResource("Resources/Textures/" + path));
        } catch (Exception e) {
            System.out.println("Failed loading: " + path);
            return null;
        }
    }

}
