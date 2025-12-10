
import javax.swing.*;

import main.java.SudokuLoader.SudokuEntry;
import java.awt.*;
import java.awt.event.*;

public class SudokuGamePanel extends JPanel implements KeyListener, MouseListener {
    private MiniGamePlatform platform;

    private final int GRID_SIZE = 9;
    private final int CELL_SIZE = 50;
    private final int GRID_PIXELS = GRID_SIZE * CELL_SIZE;

    private int[][] grid = new int[GRID_SIZE][GRID_SIZE];
    private boolean[][] fixed = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean[][] invalid = new boolean[GRID_SIZE][GRID_SIZE];
    private boolean[][] countedInvalid = new boolean[GRID_SIZE][GRID_SIZE];
    private int selectedRow = -1;
    private int selectedCol = -1;
    private String difficulty = "easy";
    private int errorCount = 0;
    private int maxErrors = 3;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private long startTimeMs = 0L;
    private long endTimeMs = 0L;
    private javax.swing.Timer uiTimer;

    private Color colorFixedNumber = new Color(20, 20, 20);
    private Color colorUserNumber = new Color(25, 118, 210);
    private Color colorSelectedCell = new Color(255, 235, 59, 120);

    public enum Theme {
        DEFAULT,
        DARK,
        HIGH_CONTRAST,
        SOLARIZED
    }

    public SudokuGamePanel(MiniGamePlatform platform) {
        this.platform = platform;
        setPreferredSize(new Dimension(1200, 650));
        setBackground(new Color(30, 30, 30));
        setFocusable(true);
        setLayout(null);
        addKeyListener(this);
        addMouseListener(this);
        initThemeButton();
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { positionThemeButton(); }
            @Override public void componentShown(ComponentEvent e) { positionThemeButton(); }
        });
    }

    private JButton themeButton;
    private Theme currentTheme = Theme.DEFAULT;

    private void initThemeButton() {
        themeButton = new JButton("🎨");
        themeButton.setMargin(new Insets(2, 6, 2, 6));
        themeButton.setFocusable(false);
        themeButton.setToolTipText("Temayı değiştir");
        themeButton.addActionListener(e -> {
            Theme[] values = Theme.values();
            int idx = currentTheme.ordinal();
            currentTheme = values[(idx + 1) % values.length];
            applyTheme(currentTheme);
        });
        add(themeButton);
        positionThemeButton();
    }

    private void positionThemeButton() {
        if (themeButton == null) return;
        int w = 40, h = 28;
        int x = getWidth() - w - 16;
        int y = 12;
        themeButton.setBounds(x, y, w, h);
    }

    // Tema renklerini dışarıdan set edebilmek için yardımcı metod
    public void setThemeColors(Color fixedNumber, Color userNumber, Color selectedCell) {
        if (fixedNumber != null) this.colorFixedNumber = fixedNumber;
        if (userNumber != null) this.colorUserNumber = userNumber;
        if (selectedCell != null) this.colorSelectedCell = selectedCell;
        repaint();
    }

    // Ön tanımlı temaları kolayca uygulamak için yardımcı metod
    public void applyTheme(Theme theme) {
        if (theme == null) return;
        switch (theme) {
            case DEFAULT:
                setBackground(new Color(30, 30, 30));
                setThemeColors(new Color(20, 20, 20), new Color(25, 118, 210), new Color(255, 235, 59, 120));
                break;
            case DARK:
                setBackground(new Color(18, 18, 18));
                setThemeColors(new Color(230, 230, 230), new Color(0, 176, 255), new Color(255, 193, 7, 120));
                break;
            case HIGH_CONTRAST:
                setBackground(Color.BLACK);
                setThemeColors(Color.WHITE, new Color(255, 255, 0), new Color(0, 255, 0, 120));
                break;
            case SOLARIZED:
                setBackground(new Color(0x07, 0x36, 0x42)); // base03
                setThemeColors(new Color(0xEE, 0xE8, 0xD5), new Color(0x2A, 0xA1, 0x98), new Color(0xFD, 0xF6, 0xE3, 120));
                break;
        }
        repaint();
    }

    public void loadFromXml(String resourcePath, String difficulty) {
        this.difficulty = difficulty;
        try {
            SudokuLoader.SudokuEntry pick = SudokuLoader.pickRandomByDifficulty(resourcePath, difficulty);
            if (pick != null) {
                for (int r = 0; r < GRID_SIZE; r++) {
                    for (int c = 0; c < GRID_SIZE; c++) {
                        grid[r][c] = pick.grid[r][c];
                        fixed[r][c] = pick.grid[r][c] != 0;
                        invalid[r][c] = false;
                        countedInvalid[r][c] = false;
                    }
                }
                errorCount = 0;
                gameOver = false;
                gameWon = false;
                startTimeMs = System.currentTimeMillis();
                endTimeMs = 0L;
                if (uiTimer != null) uiTimer.stop();
                uiTimer = new javax.swing.Timer(1000, e -> repaint());
                uiTimer.start();
                recomputeInvalids();
                selectedRow = -1;
                selectedCol = -1;
                repaint();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void recomputeInvalids() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                invalid[r][c] = false;
            }
        }
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int val = grid[r][c];
                if (val > 0) {
                    if (!isUniqueAt(r, c, val)) {
                        if (!fixed[r][c]) invalid[r][c] = true;
                    }
                }
            }
        }
        // Artık geçersiz olmayan hücreler için sayım bayrağını sıfırla
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (!invalid[r][c]) countedInvalid[r][c] = false;
            }
        }
    }

    private boolean isUniqueAt(int row, int col, int val) {
        // row
        for (int cc = 0; cc < GRID_SIZE; cc++) {
            if (cc == col) continue;
            if (grid[row][cc] == val) return false;
        }
        // col
        for (int rr = 0; rr < GRID_SIZE; rr++) {
            if (rr == row) continue;
            if (grid[rr][col] == val) return false;
        }
        // 3x3 block
        int br = (row / 3) * 3;
        int bc = (col / 3) * 3;
        for (int rr = br; rr < br + 3; rr++) {
            for (int cc = bc; cc < bc + 3; cc++) {
                if (rr == row && cc == col) continue;
                if (grid[rr][cc] == val) return false;
            }
        }
        return true;
    }

    private boolean isBoardCompleteAndValid() {
        // Tüm hücreler dolu mu?
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (grid[r][c] == 0) return false;
            }
        }
        // Geçersizlik var mı?
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int val = grid[r][c];
                if (val == 0) return false;
                if (!isUniqueAt(r, c, val)) return false;
            }
        }
        return true;
    }

    private int gridOffsetX() {
        return (getWidth() - GRID_PIXELS) / 2;
    }

    private int gridOffsetY() {
        return (getHeight() - GRID_PIXELS) / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offsetX = gridOffsetX();
        int offsetY = gridOffsetY();

        // Hücre zemini
        g.setColor(new Color(245, 245, 245));
        g.fillRect(offsetX, offsetY, GRID_PIXELS, GRID_PIXELS);

        // Seçili hücre arkaplanı
        if (selectedRow >= 0 && selectedCol >= 0) {
            g.setColor(colorSelectedCell);
            g.fillRect(offsetX + selectedCol * CELL_SIZE, offsetY + selectedRow * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Ince çizgiler
        g.setColor(new Color(180, 180, 180));
        for (int i = 0; i <= GRID_SIZE; i++) {
            int x = offsetX + i * CELL_SIZE;
            int y = offsetY + i * CELL_SIZE;
            g.drawLine(offsetX, y, offsetX + GRID_PIXELS, y);
            g.drawLine(x, offsetY, x, offsetY + GRID_PIXELS);
        }

        // 3x3 kalın blok çizgileri
        g2.setStroke(new BasicStroke(3f));
        g.setColor(new Color(50, 50, 50));
        for (int i = 0; i <= GRID_SIZE; i += 3) {
            int x = offsetX + i * CELL_SIZE;
            int y = offsetY + i * CELL_SIZE;
            g.drawLine(offsetX, y, offsetX + GRID_PIXELS, y);
            g.drawLine(x, offsetY, x, offsetY + GRID_PIXELS);
        }

        // Sayıları çiz
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int val = grid[r][c];
                if (val > 0) {
                    String s = Integer.toString(val);
                    int textW = fm.stringWidth(s);
                    int textH = fm.getAscent();
                    int cx = offsetX + c * CELL_SIZE + (CELL_SIZE - textW) / 2;
                    int cy = offsetY + r * CELL_SIZE + (CELL_SIZE + textH) / 2 - 4;
                    // Renk ayrımı ve hata gösterimi
                    if (!fixed[r][c] && invalid[r][c]) {
                        g.setColor(Color.RED);
                    } else if (fixed[r][c]) {
                        g.setColor(colorFixedNumber);
                    } else {
                        g.setColor(colorUserNumber);
                    }
                    g.drawString(s, cx, cy);
                }
            }
        }

        // Başlık ve talimat
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("SUDOKU", offsetX, Math.max(28, offsetY - 16));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        long now = System.currentTimeMillis();
        long elapsed = (endTimeMs > 0 ? (endTimeMs - startTimeMs) : (now - startTimeMs));
        if (startTimeMs == 0) elapsed = 0;
        long secs = Math.max(0, elapsed / 1000);
        long mm = secs / 60;
        long ss = secs % 60;
        String timeStr = String.format("%02d:%02d", mm, ss);
        g.drawString("Ana Menü: ESC  |  Sil: Del/Backspace  |  Zorluk: " + difficulty + "  |  Hata: " + errorCount + "/" + maxErrors + "  |  Süre: " + timeStr, 20, 30);

        // Oyun bitti overlay'i (kaybetme)
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String over = "Oyun Bitti";
            int ow = g.getFontMetrics().stringWidth(over);
            g.drawString(over, (getWidth() - ow) / 2, getHeight() / 2 - 10);
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            String info = "Hata sınırı aşıldı. Menüye dönmek için ESC";
            int iw = g.getFontMetrics().stringWidth(info);
            g.setColor(Color.WHITE);
            g.drawString(info, (getWidth() - iw) / 2, getHeight() / 2 + 24);
        }
        // Oyun kazanıldı overlay'i
        if (gameWon) {
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(76, 175, 80));
            g.setFont(new Font("Arial", Font.BOLD, 40));
            String win = "Tebrikler! Oyunu Bitirdiniz";
            int ww = g.getFontMetrics().stringWidth(win);
            g.drawString(win, (getWidth() - ww) / 2, getHeight() / 2 - 10);
            g.setFont(new Font("Arial", Font.PLAIN, 22));
            String info = "Süre: " + timeStr + "  |  Hata sayınız: " + errorCount + " | Menüye dönmek için ESC";
            int iw = g.getFontMetrics().stringWidth(info);
            g.setColor(Color.WHITE);
            g.drawString(info, (getWidth() - iw) / 2, getHeight() / 2 + 24);
        }
    }

    private void setCellValue(int row, int col, int value) {
        if (row < 0 || col < 0 || row >= GRID_SIZE || col >= GRID_SIZE) return;
        if (gameOver || gameWon) return;
        if (fixed[row][col]) return; // başlangıç değeri değiştirilemez
        int previousValue = grid[row][col];
        grid[row][col] = value;
        // Değer değişmediyse tekrar hesap ve sayım yapma
        if (previousValue == value) {
            repaint();
            return;
        }
        recomputeInvalids();
        // Yalnızca bu hücre yeni hatalı duruma geçerse ve daha önce bu hata sayılmadıysa sayacı arttır
        if (value != 0 && invalid[row][col] && !countedInvalid[row][col]) {
            errorCount++;
            countedInvalid[row][col] = true;
            if (errorCount >= maxErrors) {
                gameOver = true;
                endTimeMs = System.currentTimeMillis();
                if (uiTimer != null) uiTimer.stop();
            }
        }
        // Kazanma kontrolü: tüm hücreler dolu ve geçerli ve hata ≤ 2
        if (!gameOver && errorCount <= 2 && isBoardCompleteAndValid()) {
            gameWon = true;
            endTimeMs = System.currentTimeMillis();
            if (uiTimer != null) uiTimer.stop();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            platform.showMenu();
            return;
        }
        if (gameOver || gameWon) return;
        if (selectedRow >= 0 && selectedCol >= 0) {
            int code = e.getKeyCode();
            if (code >= KeyEvent.VK_1 && code <= KeyEvent.VK_9) {
                int val = code - KeyEvent.VK_0;
                setCellValue(selectedRow, selectedCol, val);
            } else if (code >= KeyEvent.VK_NUMPAD1 && code <= KeyEvent.VK_NUMPAD9) {
                int val = code - KeyEvent.VK_NUMPAD0;
                setCellValue(selectedRow, selectedCol, val);
            } else if (code == KeyEvent.VK_BACK_SPACE || code == KeyEvent.VK_DELETE || code == KeyEvent.VK_0 || code == KeyEvent.VK_NUMPAD0) {
                setCellValue(selectedRow, selectedCol, 0);
            } else if (code == KeyEvent.VK_LEFT && selectedCol > 0) {
                selectedCol--; repaint();
            } else if (code == KeyEvent.VK_RIGHT && selectedCol < GRID_SIZE - 1) {
                selectedCol++; repaint();
            } else if (code == KeyEvent.VK_UP && selectedRow > 0) {
                selectedRow--; repaint();
            } else if (code == KeyEvent.VK_DOWN && selectedRow < GRID_SIZE - 1) {
                selectedRow++; repaint();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        int offsetX = gridOffsetX();
        int offsetY = gridOffsetY();
        int mx = e.getX();
        int my = e.getY();
        if (mx >= offsetX && mx < offsetX + GRID_PIXELS && my >= offsetY && my < offsetY + GRID_PIXELS) {
            int c = (mx - offsetX) / CELL_SIZE;
            int r = (my - offsetY) / CELL_SIZE;
            selectedRow = r;
            selectedCol = c;
            requestFocusInWindow();
            repaint();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}


