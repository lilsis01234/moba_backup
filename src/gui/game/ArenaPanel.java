package gui.game;

import engine.process.Arena;
import gui.HUDRenderer;
import game_config.GameConfiguration;
import engine.mobile.Entity;
import javax.swing.JPanel;

import data.model.Hero;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * @author ZEGHBIB Sonia RAHARIMANANA Tianantenaina
 */

public class ArenaPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Arena arena;
    private int windowWidth;
    private int windowHeight;
    private HUDRenderer hudRenderer;
    private Runnable pauseCallback;

    public ArenaPanel(Arena arena, int windowWidth, int windowHeight, Hero hero) {
        this.arena = arena;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setMinimumSize(new Dimension(800, 600));
        
        this.hudRenderer = new HUDRenderer(
            arena.getPlayer(), 
            arena, 
            arena.getTilesManager(),
            hero
        );

        addMouseListener(new MouseAdapter() {
           @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();

                if (hudRenderer.handleMinimapClick(mx, my)) return;
                if (hudRenderer.handlePauseButtonClick(mx, my)) {
                    if (pauseCallback != null) pauseCallback.run();
                    return;
                }

                int w = getWidth();
                int h = getHeight();
                double scale   = Math.min((double)w / GameConfiguration.WORLD_WIDTH,
                                        (double)h / GameConfiguration.WORLD_HEIGHT);
                double offsetX = (w - GameConfiguration.WORLD_WIDTH  * scale) / 2;
                double offsetY = (h - GameConfiguration.WORLD_HEIGHT * scale) / 2;
                double worldX  = (mx - offsetX) / scale;
                double worldY  = (my - offsetY) / scale;

                if (worldX < 0 || worldX > GameConfiguration.WORLD_WIDTH  ||
                    worldY < 0 || worldY > GameConfiguration.WORLD_HEIGHT) return;

                if (e.getButton() == MouseEvent.BUTTON3) {
                    // aka u right-click
                    arena.getPlayer().setTarget(null); // cancel attack then move (did that cus i had a bug)
                    hudRenderer.setTargetedBot(null);
                    arena.getPlayer().moveTo(worldX, worldY);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    // aka left-click
                    double clickRadius = GameConfiguration.AttackMargin;
                    Entity clicked = arena.findClickedEnemy(worldX, worldY, clickRadius);
                    if (clicked != null) {
                        arena.getPlayer().setTarget(clicked);
                        if (clicked instanceof engine.mobile.Bot) {
                            hudRenderer.setTargetedBot((engine.mobile.Bot) clicked);
                        } else {
                            hudRenderer.setTargetedBot(null);
                        }
                    }
                }
        }});
    }

    public void setPauseCallback(Runnable callback) {
        this.pauseCallback = callback;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(windowWidth, windowHeight);
    }

    private void handleMouseClick(int mx, int my) {
        int miniWidth  = (int)(windowWidth  * 0.4);
        int miniHeight = (int)(windowHeight * 0.4);
        int miniX = windowWidth  - miniWidth  - 15;
        int miniY = windowHeight - miniHeight - 15;

        if (isClickInMinimap(mx, my, miniX, miniY, miniWidth, miniHeight)) {
            double relativeX = (double)(mx - miniX) / miniWidth;
            double relativeY = (double)(my - miniY) / miniHeight;
            arena.getPlayer().moveTo(relativeX, relativeY);
        } else {
            double relativeX = (double) mx / windowWidth;
            double relativeY = (double) my / windowHeight;
            arena.getPlayer().moveTo(relativeX, relativeY);
        }
    }

    private boolean isClickInMinimap(int mx, int my, int miniX, int miniY, int miniWidth, int miniHeight) {
        return mx >= miniX && mx <= miniX + miniWidth && my >= miniY && my <= miniY + miniHeight;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        windowWidth  = getWidth();
        windowHeight = getHeight();

        AffineTransform original = g2.getTransform();

        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, windowWidth, windowHeight);

        GameConfiguration.WINDOW_WIDTH  = windowWidth;
        GameConfiguration.WINDOW_HEIGHT = windowHeight;

        arena.render(g2, windowWidth, windowHeight);
        g2.setTransform(original);

        hudRenderer.setScreenSize(windowWidth, windowHeight);
        hudRenderer.render(g2);
    }
}
