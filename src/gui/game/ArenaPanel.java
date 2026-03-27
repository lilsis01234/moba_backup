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

public class ArenaPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private Arena arena;
    private int windowWidth;
    private int windowHeight;
    private HUDRenderer hudRenderer;
    private Runnable pauseCallback;
    private boolean paused = false;
    private Entity hoveredEntity = null;

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

                double[] world = screenToWorld(mx, my, getWidth(), getHeight());
                double worldX = world[0];
                double worldY = world[1];

                if (worldX < 0 || worldX > GameConfiguration.WORLD_WIDTH  ||
                    worldY < 0 || worldY > GameConfiguration.WORLD_HEIGHT) return;

                if (e.getButton() == MouseEvent.BUTTON3) {
                    arena.getPlayer().setTarget(null);
                    hudRenderer.setTargetedBot(null);
                    arena.getPlayer().moveTo(worldX, worldY);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
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
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                if (hudRenderer.isMouseOverPauseButton(e.getX(), e.getY())) {
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    double[] world = screenToWorld(e.getX(), e.getY(), getWidth(), getHeight());
                    hoveredEntity = arena.findEntityAtPosition(world[0], world[1], GameConfiguration.TILE_SIZE * 0.75);
                }
            }
        });
    }

    private double[] screenToWorld(int screenX, int screenY, int w, int h) {
        double scale   = Math.min((double)w / GameConfiguration.WORLD_WIDTH,
                                  (double)h / GameConfiguration.WORLD_HEIGHT);
        double offsetX = (w - GameConfiguration.WORLD_WIDTH  * scale) / 2;
        double offsetY = (h - GameConfiguration.WORLD_HEIGHT * scale) / 2;
        double worldX  = (screenX - offsetX) / scale;
        double worldY  = (screenY - offsetY) / scale;
        return new double[]{worldX, worldY};
    }

    public void setPauseCallback(Runnable callback) {
        this.pauseCallback = callback;
    }
    
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(windowWidth, windowHeight);
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

        arena.render(g2, windowWidth, windowHeight, hoveredEntity);
        g2.setTransform(original);

        hudRenderer.setScreenSize(windowWidth, windowHeight);
        hudRenderer.render(g2);
    }
}
