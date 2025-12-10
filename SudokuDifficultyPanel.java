
import javax.swing.*;

import java.awt.*;

public class SudokuDifficultyPanel extends JPanel {
    private Image backgroundImage;

    public SudokuDifficultyPanel(MiniGamePlatform platform) {
        setLayout(new GridBagLayout());

        // Load background
        try {
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("resources/menu_bg.png");
            if (is != null) {
                backgroundImage = javax.imageio.ImageIO.read(is);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Sudoku - Zorluk Seçimi");
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        Dimension btnSize = new Dimension(220, 50);

        FancyButton easyBtn = new FancyButton("Kolay");
        easyBtn.setPreferredSize(btnSize);
        easyBtn.addActionListener(e -> platform.startSudoku("easy"));

        FancyButton mediumBtn = new FancyButton("Orta");
        mediumBtn.setPreferredSize(btnSize);
        mediumBtn.addActionListener(e -> platform.startSudoku("medium"));

        FancyButton hardBtn = new FancyButton("Zor");
        hardBtn.setPreferredSize(btnSize);
        hardBtn.addActionListener(e -> platform.startSudoku("hard"));

        FancyButton backBtn = new FancyButton("Ana Menü");
        backBtn.setPreferredSize(btnSize);
        backBtn.addActionListener(e -> platform.showMenu());

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(title, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(easyBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(mediumBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(hardBtn, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(40, 15, 15, 15);
        add(backBtn, gbc);

        // === Navigation Helper ===
        MenuNavigationHelper nav = new MenuNavigationHelper(
                easyBtn, mediumBtn, hardBtn, backBtn);
        nav.focusFirst();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            Graphics2D g2 = (Graphics2D) g;
            GradientPaint gp = new GradientPaint(0, 0, new Color(20, 20, 60), 0, getHeight(), Color.BLACK);
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
