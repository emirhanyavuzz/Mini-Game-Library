
import javax.swing.*;

import java.awt.*;

public class SnakeDifficultyPanel extends JPanel {
    private Image backgroundImage;

    public SnakeDifficultyPanel(MiniGamePlatform platform) {
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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Snake - Zorluk ve Mod Seçimi");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        // Shadow/Outline effect via border or just relying on contrast
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Zorluk
        JLabel difficultyLabel = new JLabel("Zorluk Seviyesi");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        difficultyLabel.setForeground(Color.WHITE);

        JRadioButton easyBtn = new JRadioButton("Kolay (Yavaş)");
        styleRadioButton(easyBtn);
        JRadioButton mediumBtn = new JRadioButton("Orta (Normal)");
        styleRadioButton(mediumBtn);
        JRadioButton hardBtn = new JRadioButton("Zor (Hızlı)");
        styleRadioButton(hardBtn);

        ButtonGroup diffGroup = new ButtonGroup();
        diffGroup.add(easyBtn);
        diffGroup.add(mediumBtn);
        diffGroup.add(hardBtn);
        easyBtn.setSelected(true);

        // Mod
        JLabel modeLabel = new JLabel("Oyun Modu");
        modeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        modeLabel.setForeground(Color.WHITE);

        JRadioButton wallModeBtn = new JRadioButton("Duvarlar Açık");
        styleRadioButton(wallModeBtn);
        JRadioButton noWallModeBtn = new JRadioButton("Duvarlar Kapalı (Sınırsız)");
        styleRadioButton(noWallModeBtn);

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(wallModeBtn);
        modeGroup.add(noWallModeBtn);
        wallModeBtn.setSelected(true);

        FancyButton startBtn = new FancyButton("Oyunu Başlat");
        startBtn.setPreferredSize(new Dimension(200, 45));

        FancyButton backBtn = new FancyButton("Geri Dön");
        backBtn.setPreferredSize(new Dimension(200, 45));

        // Layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(title, gbc);

        // Styling groupings
        JPanel controlsPanel = new JPanel(new GridBagLayout());
        controlsPanel.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        controlsPanel.add(difficultyLabel, c);
        c.gridy = 1;
        controlsPanel.add(easyBtn, c);
        c.gridy = 2;
        controlsPanel.add(mediumBtn, c);
        c.gridy = 3;
        controlsPanel.add(hardBtn, c);

        c.gridy = 4;
        c.insets = new Insets(20, 5, 5, 5); // spacing
        controlsPanel.add(modeLabel, c);
        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = 5;
        controlsPanel.add(wallModeBtn, c);
        c.gridy = 6;
        controlsPanel.add(noWallModeBtn, c);

        gbc.gridy = 1;
        add(controlsPanel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
        add(startBtn, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(backBtn, gbc);

        startBtn.addActionListener(e -> {
            int delay;
            if (easyBtn.isSelected())
                delay = 200;
            else if (mediumBtn.isSelected())
                delay = 140;
            else
                delay = 80;
            boolean wallMode = wallModeBtn.isSelected();
            platform.startSnake(delay, wallMode);
        });

        backBtn.addActionListener(e -> platform.showMenu());

        // Helper not strictly needed if buttons are few, but nice to have focus
        MenuNavigationHelper nav = new MenuNavigationHelper(
                startBtn, backBtn);
        nav.focusFirst();
    }

    private void styleRadioButton(JRadioButton rb) {
        rb.setOpaque(false);
        rb.setForeground(Color.WHITE);
        rb.setFont(new Font("Arial", Font.PLAIN, 16));
        rb.setFocusPainted(false);
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
