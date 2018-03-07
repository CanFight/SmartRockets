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

public class SmartRockets implements Runnable, MouseListener, MouseMotionListener, KeyListener{

    public static int UPDATES_PER_SEC = 60;
    private static final int UPDATES_PER_SEC_BASE = 60;
    public static double GAME_PIXEL_FIX = 1.0;
    public static double GAME_SPEED_FIX = 1.0;
    public static int FRAMES_PER_SEC = 60;
    private int framesC, updatesC;
    private double nsPerFrame, nsPerUpdate;
    private boolean isRunning;

    public static Random rng;
    private static int[][] map;
    private double drawOffsetX = 50, drawOffsetY = 50, mouseX, mouseY;
    private static int tileSize = 10;
    private GameFrame frame;
    private boolean camMove = false, drawTool = false, eraseTool = false;

    private RocketLauncher rL;
    
    public SmartRockets() {
        rng = new Random();
        map = new int[100][100];
        frame = new GameFrame();
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        frame.addKeyListener(this);
        rL = new RocketLauncher();
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

        int frames = 0;
        int updates = 0;

        long frameCounter = System.currentTimeMillis();

        while (isRunning) {

            long currentTime = System.nanoTime();
            long passedTime = currentTime - lastTime;
            lastTime = currentTime;
            unprocessedTime += passedTime;
            unprocessedTimeFPS += passedTime;

            if (unprocessedTime >= nsPerUpdate) {
                unprocessedTime = 0;
                updates++;
                update();
            }

            if (unprocessedTimeFPS >= nsPerFrame) {
                unprocessedTimeFPS = 0;
                render();
                frames++;
            }

            if (System.currentTimeMillis() - frameCounter >= 1000) {
                framesC = frames;
                updatesC = updates;
                frames = 0;
                updates = 0;
                frameCounter += 1000;
            }

        }
        dispose();
    }

    private void update() {
        rL.update();
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
        rL.draw((int) drawOffsetX,(int) drawOffsetY, g);
        g.setColor(Color.blue);
        g.setFont(new Font("LucidaSans", Font.PLAIN, 15));
        g.drawString("FPS: " + framesC, 25, 50);
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
        if(drawTool){
            changeTile(e.getX(), e.getY(), 1);
        }
        if(eraseTool){
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
        switch(e.getKeyCode()){
            case KeyEvent.VK_R:
                    rL.moveLauncher((int)(mouseX - drawOffsetX), (int)(mouseY - drawOffsetY));
                break;
            case KeyEvent.VK_T:
                    rL.moveTarget((int)(mouseX - drawOffsetX), (int)(mouseY - drawOffsetY));
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
