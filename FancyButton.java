
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class FancyButton extends JButton {
    private boolean isHovered = false;
    private Color startColor = new Color(75, 0, 130);
    private Color endColor = new Color(138, 43, 226);
    private Color hoverStartColor = new Color(100, 20, 160);
    private Color hoverEndColor = new Color(160, 60, 255);

    public FancyButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 18));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Gradient
        Color c1 = isHovered ? hoverStartColor : startColor;
        Color c2 = isHovered ? hoverEndColor : endColor;
        GradientPaint gp = new GradientPaint(0, 0, c1, w, h, c2);
        g2.setPaint(gp);

        // Rounded rect
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, 20, 20));

        // Border
        g2.setColor(new Color(255, 255, 255, 100));
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(new RoundRectangle2D.Float(0, 0, w - 1, h - 1, 20, 20));

        g2.dispose();

        super.paintComponent(g);
    }
}
