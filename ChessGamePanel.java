
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ChessGamePanel extends JPanel implements KeyListener, MouseListener {
    private MiniGamePlatform platform;

    private final int BOARD_SIZE = 8;
    private final int TILE_SIZE = 70;
    private final int GAME_WIDTH = BOARD_SIZE * TILE_SIZE;
    private final int GAME_HEIGHT = BOARD_SIZE * TILE_SIZE;

    // Tahta: Büyük harf beyaz, küçük harf siyah: P,N,B,R,Q,K
    private char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
    private boolean whiteToMove = true;
    private Point selectedSquare = null; // (row, col)
    private List<Point> legalTargets = new ArrayList<>();
    private boolean gameOver = false;
    private String winnerText = "";
    private JButton restartBtn;
    private JButton menuBtn;

    public ChessGamePanel(MiniGamePlatform platform) {
        this.platform = platform;
        setPreferredSize(new Dimension(1200, 650));
        setBackground(new Color(20, 20, 20));
        setFocusable(true);
        setLayout(null);
        addKeyListener(this);
        addMouseListener(this);
        initOverlayButtons();
        resetPosition();
    }

    private void initOverlayButtons() {
        restartBtn = new JButton("Tekrar Oyna");
        restartBtn.setFocusable(false);
        restartBtn.setVisible(false);
        restartBtn.addActionListener(e -> {
            resetPosition();
            gameOver = false;
            winnerText = "";
            restartBtn.setVisible(false);
            menuBtn.setVisible(false);
            requestFocusInWindow();
            repaint();
        });
        menuBtn = new JButton("Ana Menü");
        menuBtn.setFocusable(false);
        menuBtn.setVisible(false);
        menuBtn.addActionListener(e -> platform.showMenu());
        add(restartBtn);
        add(menuBtn);
    }

    private void resetPosition() {
        // Temizle
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) board[r][c] = '\0';
        }
        // Siyah ana taşlar (üst sıra)
        board[0] = new char[] {'r','n','b','q','k','b','n','r'};
        // Siyah piyonlar
        for (int c = 0; c < BOARD_SIZE; c++) board[1][c] = 'p';
        // Beyaz piyonlar
        for (int c = 0; c < BOARD_SIZE; c++) board[6][c] = 'P';
        // Beyaz ana taşlar (alt sıra)
        board[7] = new char[] {'R','N','B','Q','K','B','N','R'};
        whiteToMove = true;
        selectedSquare = null;
        legalTargets.clear();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int offsetX = (getWidth() - GAME_WIDTH) / 2;
        int offsetY = (getHeight() - GAME_HEIGHT) / 2;

        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                boolean light = (row + col) % 2 == 0;
                g.setColor(light ? new Color(238, 238, 210) : new Color(118, 150, 86));
                int x = offsetX + col * TILE_SIZE;
                int y = offsetY + row * TILE_SIZE;
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                // Seçili kare vurgusu
                if (selectedSquare != null && selectedSquare.x == row && selectedSquare.y == col) {
                    g.setColor(new Color(255, 235, 59, 120));
                    g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Olası hamle vurguları
        g.setColor(new Color(33, 150, 243, 160));
        for (Point p : legalTargets) {
            int cx = offsetX + p.y * TILE_SIZE + TILE_SIZE / 2;
            int cy = offsetY + p.x * TILE_SIZE + TILE_SIZE / 2;
            g.fillOval(cx - 10, cy - 10, 20, 20);
        }

        // Taşları çiz (Unicode semboller)
        g.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 42));
        FontMetrics fm = g.getFontMetrics();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                char pc = board[r][c];
                if (pc == '\0') continue;
                String sym = pieceToUnicode(pc);
                int x = offsetX + c * TILE_SIZE + (TILE_SIZE - fm.stringWidth(sym)) / 2;
                int y = offsetY + r * TILE_SIZE + (TILE_SIZE + fm.getAscent()) / 2 - 8;
                g.setColor(Character.isUpperCase(pc) ? Color.WHITE : Color.BLACK);
                g.drawString(sym, x, y);
            }
        }

        // Alt bilgi
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Menü: ESC  |  Sıra: " + (whiteToMove ? "Beyaz" : "Siyah"), 20, 30);

        // Oyun bitti overlay
        if (gameOver) {
            String text = winnerText.isEmpty() ? "Oyun Bitti" : winnerText;
            g.setColor(new Color(0, 0, 0, 160));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            int tw = g.getFontMetrics().stringWidth(text);
            g.drawString(text, (getWidth() - tw) / 2, getHeight() / 2 - 20);
            // Butonları konumlandır
            int bw = 160, bh = 36;
            restartBtn.setBounds(getWidth() / 2 - bw - 10, getHeight() / 2 + 10, bw, bh);
            menuBtn.setBounds(getWidth() / 2 + 10, getHeight() / 2 + 10, bw, bh);
            restartBtn.setVisible(true);
            menuBtn.setVisible(true);
        }
    }

    private String pieceToUnicode(char p) {
        switch (p) {
            case 'K': return "\u2654"; // White King
            case 'Q': return "\u2655";
            case 'R': return "\u2656";
            case 'B': return "\u2657";
            case 'N': return "\u2658";
            case 'P': return "\u2659";
            case 'k': return "\u265A"; // Black King
            case 'q': return "\u265B";
            case 'r': return "\u265C";
            case 'b': return "\u265D";
            case 'n': return "\u265E";
            case 'p': return "\u265F";
        }
        return "";
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) platform.showMenu();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver) return;
        int offsetX = (getWidth() - GAME_WIDTH) / 2;
        int offsetY = (getHeight() - GAME_HEIGHT) / 2;
        int mx = e.getX();
        int my = e.getY();
        int col = (mx - offsetX) / TILE_SIZE;
        int row = (my - offsetY) / TILE_SIZE;
        if (col < 0 || col >= BOARD_SIZE || row < 0 || row >= BOARD_SIZE) return;

        if (selectedSquare == null) {
            char pc = board[row][col];
            if (pc != '\0' && isWhite(pc) == whiteToMove) {
                selectedSquare = new Point(row, col);
                legalTargets = generateLegalTargets(row, col, pc);
                repaint();
            }
        } else {
            // Aynı renkten başka taş seçimi
            char pc = board[row][col];
            if (pc != '\0' && isWhite(pc) == whiteToMove) {
                selectedSquare = new Point(row, col);
                legalTargets = generateLegalTargets(row, col, pc);
                repaint();
                return;
            }
            // Hamle dene
            for (Point t : legalTargets) {
                if (t.x == row && t.y == col) {
                    doMove(selectedSquare.x, selectedSquare.y, row, col);
                    // Basit bitiş: rakip şah yakalandıysa oyun biter
                    if (!hasKing(!whiteToMove)) {
                        gameOver = true;
                        winnerText = (whiteToMove ? "Beyaz kazandı" : "Siyah kazandı");
                    } else {
                        whiteToMove = !whiteToMove;
                    }
                    break;
                }
            }
            selectedSquare = null;
            legalTargets.clear();
            repaint();
        }
    }

    private boolean hasKing(boolean white) {
        char k = white ? 'K' : 'k';
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == k) return true;
            }
        }
        return false;
    }

    private void doMove(int sr, int sc, int tr, int tc) {
        char pc = board[sr][sc];
        // Basit terfi: piyon son sıraya ulaşırsa vezire çevir
        if ((pc == 'P' && tr == 0)) pc = 'Q';
        if ((pc == 'p' && tr == 7)) pc = 'q';
        board[tr][tc] = pc;
        board[sr][sc] = '\0';
    }

    private boolean isWhite(char pc) { return Character.isUpperCase(pc); }
    private boolean isEmpty(int r, int c) { return board[r][c] == '\0'; }
    private boolean inBoard(int r, int c) { return r >= 0 && r < BOARD_SIZE && c >= 0 && c < BOARD_SIZE; }

    private List<Point> generateLegalTargets(int r, int c, char pc) {
        List<Point> list = new ArrayList<>();
        char p = Character.toLowerCase(pc);
        int dir = isWhite(pc) ? -1 : 1; // beyaz yukarı, siyah aşağı
        switch (p) {
            case 'p':
                // ileri 1
                int nr = r + dir;
                if (inBoard(nr, c) && isEmpty(nr, c)) list.add(new Point(nr, c));
                // ilk yerinden ileri 2
                int startRow = isWhite(pc) ? 6 : 1;
                int nr2 = r + 2 * dir;
                if (r == startRow && inBoard(nr2, c) && isEmpty(nr, c) && isEmpty(nr2, c)) list.add(new Point(nr2, c));
                // çapraz yeme
                for (int dc : new int[]{-1, 1}) {
                    int cr = r + dir, cc = c + dc;
                    if (inBoard(cr, cc) && !isEmpty(cr, cc) && isWhite(board[cr][cc]) != isWhite(pc)) list.add(new Point(cr, cc));
                }
                break;
            case 'n':
                int[][] ks = {{-2,-1},{-2,1},{-1,-2},{-1,2},{1,-2},{1,2},{2,-1},{2,1}};
                for (int[] d : ks) {
                    int rr = r + d[0], cc = c + d[1];
                    if (!inBoard(rr, cc)) continue;
                    if (isEmpty(rr, cc) || isWhite(board[rr][cc]) != isWhite(pc)) list.add(new Point(rr, cc));
                }
                break;
            case 'b':
                slide(list, r, c, pc, -1,-1); slide(list, r, c, pc, -1,1); slide(list, r, c, pc, 1,-1); slide(list, r, c, pc, 1,1);
                break;
            case 'r':
                slide(list, r, c, pc, -1,0); slide(list, r, c, pc, 1,0); slide(list, r, c, pc, 0,-1); slide(list, r, c, pc, 0,1);
                break;
            case 'q':
                slide(list, r, c, pc, -1,-1); slide(list, r, c, pc, -1,1); slide(list, r, c, pc, 1,-1); slide(list, r, c, pc, 1,1);
                slide(list, r, c, pc, -1,0); slide(list, r, c, pc, 1,0); slide(list, r, c, pc, 0,-1); slide(list, r, c, pc, 0,1);
                break;
            case 'k':
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc2 = -1; dc2 <= 1; dc2++) {
                        if (dr == 0 && dc2 == 0) continue;
                        int rr = r + dr, cc = c + dc2;
                        if (!inBoard(rr, cc)) continue;
                        if (isEmpty(rr, cc) || isWhite(board[rr][cc]) != isWhite(pc)) list.add(new Point(rr, cc));
                    }
                }
                break;
        }
        return list;
    }

    private void slide(List<Point> list, int r, int c, char pc, int dr, int dc) {
        int rr = r + dr, cc = c + dc;
        while (inBoard(rr, cc)) {
            if (isEmpty(rr, cc)) {
                list.add(new Point(rr, cc));
            } else {
                if (isWhite(board[rr][cc]) != isWhite(pc)) list.add(new Point(rr, cc));
                break;
            }
            rr += dr; cc += dc;
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}


