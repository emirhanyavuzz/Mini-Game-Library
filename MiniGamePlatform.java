
import javax.swing.*;

import java.awt.*;

public class MiniGamePlatform extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private GameSelectionPanel gameSelectionPanel;
    private SnakeGamePanel snakeGamePanel;
    private SnakeDifficultyPanel snakeDifficultyPanel;
    private FlappyBirdPanel flappyBirdPanel;
    private ChessGamePanel chessGamePanel;
    private SudokuGamePanel sudokuGamePanel;
    private SudokuDifficultyPanel sudokuDifficultyPanel;
    private GoldDiggerPanel goldDiggerPanel;
    private String sudokuGameXmlPath = "resources/sudoku_puzzles.xml";

    public MiniGamePlatform() {
        setTitle("Mini Oyun Platformu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(1200, 650);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        gameSelectionPanel = new GameSelectionPanel(this);
        snakeGamePanel = new SnakeGamePanel(this);
        snakeDifficultyPanel = new SnakeDifficultyPanel(this);
        flappyBirdPanel = new FlappyBirdPanel(this);
        chessGamePanel = new ChessGamePanel(this);
        sudokuGamePanel = new SudokuGamePanel(this);
        sudokuDifficultyPanel = new SudokuDifficultyPanel(this);
        goldDiggerPanel = new GoldDiggerPanel(this);

        mainPanel.add(gameSelectionPanel, "Selection");
        mainPanel.add(snakeGamePanel, "Snake");
        mainPanel.add(snakeDifficultyPanel, "SnakeDifficulty");
        mainPanel.add(flappyBirdPanel, "Flappy");
        mainPanel.add(chessGamePanel, "Chess");
        mainPanel.add(sudokuGamePanel, "Sudoku");
        mainPanel.add(sudokuDifficultyPanel, "SudokuDifficulty");
        mainPanel.add(goldDiggerPanel, "GoldDigger");

        add(mainPanel);
        setVisible(true);
    }

    public void showGame(String gameName) {
        if (gameName.equals("Snake"))
            cardLayout.show(mainPanel, "SnakeDifficulty");
        else if (gameName.equals("Flappy")) {
            cardLayout.show(mainPanel, "Flappy");
            flappyBirdPanel.start(16, 6); // ~60 FPS, orta hız
            flappyBirdPanel.requestFocusInWindow();
        } else if (gameName.equals("Chess")) {
            cardLayout.show(mainPanel, "Chess");
            chessGamePanel.requestFocusInWindow();
        } else if (gameName.equals("Sudoku")) {
            cardLayout.show(mainPanel, "SudokuDifficulty");
        } else if (gameName.equals("GoldDigger")) {
            cardLayout.show(mainPanel, "GoldDigger");
            goldDiggerPanel.start();
            goldDiggerPanel.requestFocusInWindow();
        }
    }

    // Snake zorluk ekranından çağrılır
    public void startSnake(int delay, boolean wallMode) {
        cardLayout.show(mainPanel, "Snake");
        snakeGamePanel.start(delay, wallMode);
        snakeGamePanel.requestFocusInWindow();
    }

    // Sudoku zorluk seçiminden çağrılır
    public void startSudoku(String difficulty) {
        // Rastgele seçim ve yükleme SudokuGamePanel/SudokuLoader içinde yapılır
        sudokuGamePanel.loadFromXml(sudokuGameXmlPath, difficulty);
        cardLayout.show(mainPanel, "Sudoku");
        sudokuGamePanel.requestFocusInWindow();
    }

    public void showMenu() {
        cardLayout.show(mainPanel, "Selection");
    }

    public static void main(String[] args) {
        new MiniGamePlatform();
    }
}