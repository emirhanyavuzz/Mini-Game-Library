
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SnakeGamePanel extends JPanel implements ActionListener, KeyListener {
    private MiniGamePlatform platform;
    private Timer timer;
    private final int UNIT_SIZE = 25;
    private final int GAME_WIDTH = 1200;
    private final int GAME_HEIGHT = 650;
    private final int GAME_UNITS = (GAME_WIDTH * GAME_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private int[] x = new int[GAME_UNITS];
    private int[] y = new int[GAME_UNITS];
    private int bodyParts = 6;
    private int applesEaten;
    private int appleX, appleY;
    private char direction = 'R';
    private boolean running = false;
    private boolean paused = false;
    private boolean wallMode = true;
    private Random random = new Random();

    public SnakeGamePanel(MiniGamePlatform platform) {
        this.platform = platform;
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
    }

    public void start(int delay, boolean wallMode) {
        this.wallMode = wallMode;
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;
        paused = false;
        newApple();
        timer = new Timer(delay, this);
        timer.start();
    }

    private void newApple() {
        boolean valid;
        do {
            valid = true;
            appleX = random.nextInt(GAME_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(GAME_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            for (int i = 0; i < bodyParts; i++) {
                if (x[i] == appleX && y[i] == appleY) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (running) {
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.red);
                } else {
                    g.setColor(Color.green);
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + applesEaten, 10, 20);
        } else {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 - 20);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Skor: " + applesEaten, GAME_WIDTH / 2 - 40, GAME_HEIGHT / 2 + 20);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Menüye dönmek için ESC'ye bas", GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 + 60);
        }
    }

    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U': y[0] -= UNIT_SIZE; break;
            case 'D': y[0] += UNIT_SIZE; break;
            case 'L': x[0] -= UNIT_SIZE; break;
            case 'R': x[0] += UNIT_SIZE; break;
        }
    }

    private void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) running = false;
        }
        if (wallMode) {
            if (x[0] < 0 || x[0] >= GAME_WIDTH || y[0] < 0 || y[0] >= GAME_HEIGHT) running = false;
        } else {
            if (x[0] < 0) x[0] = GAME_WIDTH - UNIT_SIZE;
            if (x[0] >= GAME_WIDTH) x[0] = 0;
            if (y[0] < 0) y[0] = GAME_HEIGHT - UNIT_SIZE;
            if (y[0] >= GAME_HEIGHT) y[0] = 0;
        }
        if (!running) timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT: if (direction != 'R') direction = 'L'; break;
            case KeyEvent.VK_RIGHT: if (direction != 'L') direction = 'R'; break;
            case KeyEvent.VK_UP: if (direction != 'D') direction = 'U'; break;
            case KeyEvent.VK_DOWN: if (direction != 'U') direction = 'D'; break;
            case KeyEvent.VK_SPACE: paused = !paused; break;
            case KeyEvent.VK_ESCAPE: platform.showMenu(); break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
