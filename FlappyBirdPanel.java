
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FlappyBirdPanel extends JPanel implements ActionListener, KeyListener, MouseListener {
    private MiniGamePlatform platform;
    private Timer timer;

    private final int GAME_WIDTH = 1200;
    private final int GAME_HEIGHT = 650;

    private final int BIRD_SIZE = 30;
    private int birdX = 200;
    private int birdY;
    private double birdVelocity;
    private final double GRAVITY = 0.7;
    private final double FLAP_STRENGTH = -10.5;

    private static class Pipe {
        int x;
        int gapY;
        int width;
        int gapHeight;
        boolean scored;

        Pipe(int x, int gapY, int width, int gapHeight) {
            this.x = x;
            this.gapY = gapY;
            this.width = width;
            this.gapHeight = gapHeight;
            this.scored = false;
        }
    }

    private List<Pipe> pipes = new ArrayList<>();
    private int pipeSpawnIntervalMs = 1100;
    private long lastPipeSpawnTime;
    private int pipeSpeed = 5;
    private int basePipeSpeed = 5;
    private int frameDelayMs = 16;

    private final int baseGapHeight = 170;
    private final int minGapHeight = 110;

    private int score = 0;
    private boolean running = false;
    private boolean paused = false;

    private Random random = new Random();

    public FlappyBirdPanel(MiniGamePlatform platform) {
        this.platform = platform;
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(new Color(25, 25, 25));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
    }

    public void start(int delayMs, int pipeSpeed) {
        this.frameDelayMs = delayMs;
        this.pipeSpeed = pipeSpeed;
        this.basePipeSpeed = pipeSpeed;
        this.pipeSpawnIntervalMs = Math.max(900, 1800 - pipeSpeed * 60);

        score = 0;
        running = true;
        paused = false;
        pipes.clear();

        birdY = GAME_HEIGHT / 2;
        birdVelocity = 0;
        // Oyuna hızlı ama düzenli aralıkla başlangıç: ilk boruyu beklenen aralığa göre
        // konumla
        int expectedSpacingPixels = pipeSpeed * Math.max(1, pipeSpawnIntervalMs / Math.max(1, frameDelayMs));
        int firstPipeX = Math.max(birdX + 280, GAME_WIDTH - expectedSpacingPixels);
        spawnPipeAt(firstPipeX);
        lastPipeSpawnTime = System.currentTimeMillis();

        if (timer != null)
            timer.stop();
        timer = new Timer(delayMs, this);
        timer.start();
    }

    private void spawnPipe() {
        int gapHeight = Math.max(minGapHeight, baseGapHeight - score * 4);
        int minGapY = 80;
        int maxGapY = GAME_HEIGHT - gapHeight - 80;
        int gapY = minGapY + random.nextInt(Math.max(1, maxGapY - minGapY));
        int width = 70;
        pipes.add(new Pipe(GAME_WIDTH, gapY, width, gapHeight));
    }

    private void spawnPipeAt(int x) {
        int gapHeight = Math.max(minGapHeight, baseGapHeight - score * 4);
        int minGapY = 80;
        int maxGapY = GAME_HEIGHT - gapHeight - 80;
        int gapY = minGapY + random.nextInt(Math.max(1, maxGapY - minGapY));
        int width = 70;
        pipes.add(new Pipe(x, gapY, width, gapHeight));
    }

    private void updateGame() {
        // Dinamik zorluk: hız ve spawn aralığı skorla değişir
        pipeSpeed = Math.min(12, basePipeSpeed + score / 5); // her 5 skorda +1 hız, 12 ile sınırlı
        pipeSpawnIntervalMs = Math.max(800, 1800 - pipeSpeed * 60 - score * 10); // skor arttıkça biraz daha sık boru

        birdVelocity += GRAVITY;
        birdY += (int) Math.round(birdVelocity);

        if (System.currentTimeMillis() - lastPipeSpawnTime > pipeSpawnIntervalMs) {
            spawnPipe();
            lastPipeSpawnTime = System.currentTimeMillis();
        }

        for (int i = 0; i < pipes.size(); i++) {
            Pipe p = pipes.get(i);
            p.x -= pipeSpeed;
        }

        pipes.removeIf(p -> p.x + p.width < 0);

        checkCollisionsAndScore();
    }

    private void checkCollisionsAndScore() {
        if (birdY < 0 || birdY + BIRD_SIZE > GAME_HEIGHT) {
            gameOver();
            return;
        }

        Rectangle birdRect = new Rectangle(birdX, birdY, BIRD_SIZE, BIRD_SIZE);
        for (Pipe p : pipes) {
            Rectangle top = new Rectangle(p.x, 0, p.width, p.gapY);
            Rectangle bottom = new Rectangle(p.x, p.gapY + p.gapHeight, p.width, GAME_HEIGHT - (p.gapY + p.gapHeight));

            if (birdRect.intersects(top) || birdRect.intersects(bottom)) {
                gameOver();
                return;
            }

            if (!p.scored && p.x + p.width < birdX) {
                p.scored = true;
                score++;
            }
        }
    }

    private void gameOver() {
        running = false;
        if (timer != null)
            timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            updateGame();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Arkaplan
        g.setColor(new Color(30, 35, 45));
        g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

        // Borular
        g.setColor(new Color(80, 200, 120));
        for (Pipe p : pipes) {
            g.fillRect(p.x, 0, p.width, p.gapY);
            g.fillRect(p.x, p.gapY + p.gapHeight, p.width, GAME_HEIGHT - (p.gapY + p.gapHeight));
        }

        // Kuş
        g.setColor(new Color(255, 215, 0));
        g.fillOval(birdX, birdY, BIRD_SIZE, BIRD_SIZE);
        // Göz
        g.setColor(Color.white);
        g.fillOval(birdX + BIRD_SIZE - 12, birdY + 6, 8, 8);
        g.setColor(Color.black);
        g.fillOval(birdX + BIRD_SIZE - 10, birdY + 8, 4, 4);

        // Skor
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 16, 28);

        if (!running) {
            g.setColor(Color.red);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 - 20);

            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Skor: " + score, GAME_WIDTH / 2 - 40, GAME_HEIGHT / 2 + 20);

            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Menüye dönmek için ESC'ye bas", GAME_WIDTH / 2 - 150, GAME_HEIGHT / 2 + 60);
        } else if (paused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("PAUSED", GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2);
        }
    }

    private void flap() {
        birdVelocity = FLAP_STRENGTH;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (running) {
                    paused = !paused;
                    repaint();
                }
                break;
            case KeyEvent.VK_UP:
                if (running && !paused) {
                    flap();
                }
                break;
            case KeyEvent.VK_ESCAPE:
                // Menüye dön
                if (timer != null)
                    timer.stop();
                platform.showMenu();
                break;
            // ESC veya istenirse ENTER ile çıkış (önceki kodda Enter vardı, ESC artık
            // standart istendi)
            case KeyEvent.VK_ENTER:
                if (!running)
                    platform.showMenu();
                break;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Sol tık ile zıplama
        if (running && !paused && SwingUtilities.isLeftMouseButton(e)) {
            flap();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
