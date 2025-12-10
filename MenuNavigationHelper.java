
import javax.swing.*;
import java.awt.event.ActionEvent;

public class MenuNavigationHelper {
    private final JButton[] buttons;
    private int currentIndex = 0;

    public MenuNavigationHelper(JButton... buttons) {
        this.buttons = buttons;
        setupKeyBindings();
    }

    private void setupKeyBindings() {
        if (buttons.length == 0) return;

        JComponent comp = (JComponent) buttons[0].getParent(); // Panel referansı

        InputMap im = comp.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = comp.getActionMap();

        // Aşağı ok
        im.put(KeyStroke.getKeyStroke("DOWN"), "moveDown");
        am.put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                move(1);
            }
        });

        // Yukarı ok
        im.put(KeyStroke.getKeyStroke("UP"), "moveUp");
        am.put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                move(-1);
            }
        });

        // Enter
        im.put(KeyStroke.getKeyStroke("ENTER"), "press");
        am.put("press", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttons[currentIndex].doClick();
            }
        });

        // Tab tuşu
        im.put(KeyStroke.getKeyStroke("TAB"), "next");
        am.put("next", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                move(1);
            }
        });
    }

    private void move(int delta) {
        if (buttons.length == 0) return;
        currentIndex = (currentIndex + delta + buttons.length) % buttons.length;
        buttons[currentIndex].requestFocusInWindow();
    }

    public void focusFirst() {
        if (buttons.length > 0) {
            currentIndex = 0;
            buttons[0].requestFocusInWindow();
        }
    }
}
