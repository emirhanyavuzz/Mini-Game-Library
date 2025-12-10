
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.IOException;

public class GameSelectionPanel extends JPanel {
    private Image backgroundImage;

    public GameSelectionPanel(MiniGamePlatform platform) {
        setLayout(new GridBagLayout());

        // Load background
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("resources/menu_bg.png");
            if (is != null) {
                backgroundImage = ImageIO.read(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("MINI OYUN PLATFORMU");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        // Shadow effect for title
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        FancyButton snakeBtn = new FancyButton("Snake Game");
        snakeBtn.addActionListener(e -> platform.showGame("Snake"));

        FancyButton flappyBtn = new FancyButton("Flappy Bird");
        flappyBtn.addActionListener(e -> platform.showGame("Flappy"));

        FancyButton chessBtn = new FancyButton("Chess");
        chessBtn.addActionListener(e -> platform.showGame("Chess"));

        FancyButton sudokuBtn = new FancyButton("Sudoku");
        sudokuBtn.addActionListener(e -> platform.showGame("Sudoku"));

        FancyButton goldBtn = new FancyButton("Gold Digger");
        goldBtn.addActionListener(e -> platform.showGame("GoldDigger"));

        // Increase button sizes
        Dimension btnSize = new Dimension(220, 50);
        snakeBtn.setPreferredSize(btnSize);
        flappyBtn.setPreferredSize(btnSize);
        chessBtn.setPreferredSize(btnSize);
        sudokuBtn.setPreferredSize(btnSize);
        goldBtn.setPreferredSize(btnSize);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(snakeBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(flappyBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(chessBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(sudokuBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(goldBtn, gbc);

        MenuNavigationHelper nav = new MenuNavigationHelper(
                snakeBtn, flappyBtn, chessBtn, sudokuBtn, goldBtn);
        nav.focusFirst();

        JLabel footer = new JLabel("© 2025 Emirhan Yavuz");
        footer.setFont(new Font("Verdana", Font.PLAIN, 14)); // Readable, clean font
        footer.setForeground(new Color(220, 220, 220)); // Whitish gray for better visibility

        // Resetting some GBC properties for the footer to ensure it centers nicely
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.NONE; // Don't stretch
        gbc.anchor = GridBagConstraints.CENTER; // Center horizontally
        gbc.insets = new Insets(40, 0, 10, 0); // More spacing above for separation
        add(footer, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback gradient if image not found
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, Color.DARK_GRAY, 0, getHeight(), Color.BLACK);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
