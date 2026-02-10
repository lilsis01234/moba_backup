package view;

import engine.Arena;
import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Arena arena;

    public GamePanel(Arena arena) {
        this.arena = arena;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        arena.render(g2, getWidth(), getHeight());
    }
}