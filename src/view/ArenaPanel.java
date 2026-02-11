package view;

import engine.Arena;
import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ArenaPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private Arena arena;
    private int windowWidth;
    private int windowHeight;

    public ArenaPanel(Arena arena, int windowWidth, int windowHeight) {
        this.arena = arena;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                int miniWidth = (int)(windowWidth * 0.4);
                int miniHeight = (int)(windowHeight * 0.4);
                int miniX = windowWidth - miniWidth - 20; 
                int miniY = windowHeight - miniHeight - 20; 

                // clic dans la Minimap
                if (mx >= miniX && mx <= miniX + miniWidth && my >= miniY && my <= miniY + miniHeight) {
                    double relativeX = (double)(mx - miniX) / miniWidth;
                    double relativeY = (double)(my - miniY) / miniHeight;
                    arena.getPlayer().moveTo(relativeX, relativeY);
                } 
                // clic sur l'arène
                else {
                    double relativeX = (double) mx / windowWidth;
                    double relativeY = (double) my / windowHeight;
                    arena.getPlayer().moveTo(relativeX, relativeY);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        windowWidth = getWidth();
        windowHeight = getHeight();

        // Fond
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, windowWidth, windowHeight);

        arena.render(g2, windowWidth, windowHeight);

        int miniWidth = (int)(windowWidth * 0.4);
        int miniHeight = (int)(windowHeight * 0.4);
        int miniX = windowWidth - miniWidth - 20; 
        int miniY = windowHeight - miniHeight - 20; 

        g2.setColor(Color.BLACK);
        g2.fillRect(miniX, miniY, miniWidth, miniHeight); 
        g2.setColor(Color.WHITE);
        g2.drawRect(miniX - 2, miniY - 2, miniWidth + 4, miniHeight + 4);

        g2.translate(miniX, miniY);
        arena.render(g2, miniWidth, miniHeight);
        g2.translate(-miniX, -miniY);
    }
}