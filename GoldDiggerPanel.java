
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GoldDiggerPanel extends JPanel implements ActionListener, KeyListener {
    private MiniGamePlatform platform;
    private Timer timer;

    private final int GAME_WIDTH = 1200;
    private final int GAME_HEIGHT = 650;

    // Kanca parametreleri
    private double hookAngle = 0; // radyan
    private double hookAngleDir = 1; // 1 veya -1
    private final double hookAngleMin = Math.toRadians(10);
    private final double hookAngleMax = Math.toRadians(160);
    private final double hookSwingSpeed = Math.toRadians(1.2);
    private boolean shooting = false;
    private boolean pullingBack = false;
    private int ropeLength = 80;
    private int ropeSpeed = 10;
    private Point hookPos = new Point();

    private int originX = GAME_WIDTH / 2;
    private int originY = 60;

    // Ögeler
    private static class Item {
        Rectangle rect;
        int value;
        Color color;
        String type;
        String size;
        int pullPenalty;

        Item(Rectangle r, int v, Color c, String type, String size, int pullPenalty) {
            rect = r;
            value = v;
            color = c;
            this.type = type;
            this.size = size;
            this.pullPenalty = pullPenalty;
        }
    }

    private List<Item> items = new ArrayList<>();
    private Item caughtItem = null;
    private int score = 0;

    private Random rnd = new Random();

    public GoldDiggerPanel(MiniGamePlatform platform) {
        this.platform = platform;
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(new Color(15, 18, 24));
        setFocusable(true);
        addKeyListener(this);
        initLevel();
    }

    public void start() {
        score = 0;
        resetHook();
        if (timer != null)
            timer.stop();
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    private List<GoldConfigLoader.ItemDef> itemDefs = new ArrayList<>();

    private void initLevel() {
        items.clear();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("resources/goldDiggerItems.xml")) {
            if (is == null)
                throw new IllegalArgumentException("Resource not found: resources/goldDiggerItems.xml");
            itemDefs = GoldConfigLoader.load(is);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // Konfige göre rastgele 15 nesne yerleştir
        int count = 15;
        for (int i = 0; i < count; i++) {
            GoldConfigLoader.ItemDef def = itemDefs.get(rnd.nextInt(itemDefs.size()));
            int x = 40 + rnd.nextInt(GAME_WIDTH - 80 - def.w);
            int y = GAME_HEIGHT / 2 + rnd.nextInt(GAME_HEIGHT / 2 - 80);
            items.add(new Item(new Rectangle(x, y, def.w, def.h), def.value, def.color, def.type, def.size,
                    def.pullPenalty));
        }
    }

    private void resetHook() {
        hookAngle = Math.toRadians(0);
        hookAngleDir = 1;
        shooting = false;
        pullingBack = false;
        ropeLength = 80;
        caughtItem = null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private void updateGame() {
        if (!shooting) {
            // salınım
            hookAngle += hookSwingSpeed * hookAngleDir;
            if (hookAngle > hookAngleMax) {
                hookAngle = hookAngleMax;
                hookAngleDir = -1;
            }
            if (hookAngle < hookAngleMin) {
                hookAngle = hookAngleMin;
                hookAngleDir = 1;
            }
        } else {
            // ateş ve geri çekme
            if (!pullingBack) {
                ropeLength += ropeSpeed;
                // Kenarlara veya dibe çarparsa geri çek
                int hx = originX + (int) (Math.cos(hookAngle) * ropeLength);
                int hy = originY + (int) (Math.sin(hookAngle) * ropeLength);
                if (hx < 0 || hx >= GAME_WIDTH || hy < 0 || hy >= GAME_HEIGHT)
                    pullingBack = true;
                // Eşya yakalama
                if (caughtItem == null) {
                    Rectangle hookRect = new Rectangle(hx - 6, hy - 6, 12, 12);
                    for (Item it : items) {
                        if (it.rect.intersects(hookRect)) {
                            caughtItem = it;
                            pullingBack = true;
                            break;
                        }
                    }
                }
            } else {
                // geri çekme: yakalanan varsa daha yavaş çek
                int penalty = (caughtItem != null ? caughtItem.pullPenalty : 0);
                int pullSpeed = (caughtItem == null ? ropeSpeed : Math.max(2, ropeSpeed - penalty));
                ropeLength -= pullSpeed;
                if (ropeLength <= 80) {
                    ropeLength = 80;
                    shooting = false;
                    if (caughtItem != null) {
                        score += caughtItem.value;
                        items.remove(caughtItem);
                        caughtItem = null;
                    }
                }
            }
        }
        // Kanca pozisyonunu güncelle
        hookPos.x = originX + (int) (Math.cos(hookAngle) * ropeLength);
        hookPos.y = originY + (int) (Math.sin(hookAngle) * ropeLength);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gökyüzü ve yer
        g.setColor(new Color(25, 28, 35));
        g.fillRect(0, 0, GAME_WIDTH, originY);
        g.setColor(new Color(50, 40, 30));
        g.fillRect(0, originY, GAME_WIDTH, GAME_HEIGHT - originY);

        // Yüzey vinci
        g.setColor(new Color(200, 200, 200));
        g.fillRect(originX - 30, originY - 40, 60, 40);

        // Halat
        g.setColor(Color.WHITE);
        g.drawLine(originX, originY, hookPos.x, hookPos.y);
        // Kanca
        g.setColor(new Color(255, 87, 34));
        g.fillOval(hookPos.x - 6, hookPos.y - 6, 12, 12);

        // Yakalanan eşya halata bağlı çizim
        if (caughtItem != null) {
            g.setColor(caughtItem.color);
            Rectangle r = caughtItem.rect;
            int cx = hookPos.x - r.width / 2;
            int cy = hookPos.y - r.height / 2;
            g.fillRoundRect(cx, cy, r.width, r.height, 6, 6);
        }

        // Öğeleri çiz
        for (Item it : items) {
            if (it == caughtItem)
                continue;
            g.setColor(it.color);
            g.fillRoundRect(it.rect.x, it.rect.y, it.rect.width, it.rect.height, 6, 6);
        }

        // UI
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Skor: " + score + "   |  Kancayı Sal: SPACE  |  Menü: ESC", 16, 28);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                if (!shooting) {
                    shooting = true;
                    pullingBack = false;
                }
                break;
            case KeyEvent.VK_ESCAPE:
                platform.showMenu();
                break;
            case KeyEvent.VK_R:
                initLevel();
                resetHook();
                score = 0;
                repaint();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
