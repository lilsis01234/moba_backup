
package gui.game;

import engine.process.Arena;
import gui.HUDRenderer;
import game_config.GameConfiguration;
import engine.mobile.Bot;
import engine.mobile.Entity;
import javax.swing.JPanel;

import data.model.Hero;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import gui.ShopPanel;
import engine.process.EquipmentLoader;

public class ArenaPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private ShopPanel shopPanel;
    private Arena arena;
    private int windowWidth;
    private int windowHeight;
    private HUDRenderer hudRenderer;
    private Runnable pauseCallback;
    private Entity hoveredEntity = null;

    public ArenaPanel(Arena arena, int windowWidth, int windowHeight, Hero hero) {
        this.arena = arena;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        setMinimumSize(new Dimension(800, 600));
        
        HUDRenderer.create(
            arena.getPlayer(), 
            arena, 
            arena.getTilesManager(),
            hero
        );
        this.hudRenderer = HUDRenderer.getInstance();
        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
                    shopPanel.toggle();
                    repaint();
                }
            }
        });
        
        shopPanel = ShopPanel.create(arena.getPlayer());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mx = e.getX();
                int my = e.getY();
                if (shopPanel.handleClick(e.getX(), e.getY())) {
                    repaint();
                    return;
                }

                if (hudRenderer.handleRecallClick(mx, my)) return;

                int spellSlot = hudRenderer.getSpellSlotAt(mx, my);
                if (spellSlot >= 0) {
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        arena.getPlayer().upgradeSpell(spellSlot);
                    } else if (e.getButton() == MouseEvent.BUTTON1) {
                        Entity target = arena.getPlayer().getTargetEnemy();
                        arena.getPlayer().castSpell(spellSlot, target);
                    }
                    repaint();
                    return;
                }

                if (hudRenderer.handlePauseButtonClick(mx, my)) {
                    if (pauseCallback != null) pauseCallback.run();
                    return;
                }

                double[] world = screenToWorld(mx, my, getWidth(), getHeight());
                double worldX = world[0];
                double worldY = world[1];

                if (worldX < 0 || worldX > GameConfiguration.WORLD_WIDTH ||
                    worldY < 0 || worldY > GameConfiguration.WORLD_HEIGHT) return;

                if (e.getButton() == MouseEvent.BUTTON3) {
                    arena.getPlayer().setTarget(null);
                    hudRenderer.setTargetedBot(null);
                    arena.getPlayer().moveTo(worldX, worldY);
                } else if (e.getButton() == MouseEvent.BUTTON1) {
                    arena.getPlayer().showAttackRadius();
                    double clickRadius = GameConfiguration.ATTACK_MARGIN;
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
            }
        });

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

        this.setFocusable(true);
        this.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_4 || e.getKeyCode() == java.awt.event.KeyEvent.VK_NUMPAD4) {
                    arena.getPlayer().startRecall();
                }

                boolean ctrl = (e.getModifiersEx() & java.awt.event.InputEvent.CTRL_DOWN_MASK) != 0;
                int spellIndex = -1;
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_1) spellIndex = 0;
                else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_2) spellIndex = 1;
                else if (e.getKeyCode() == java.awt.event.KeyEvent.VK_3) spellIndex = 2;

                if (spellIndex >= 0) {
                    if (ctrl) {
                        arena.getPlayer().upgradeSpell(spellIndex);
                    } else {
                        engine.mobile.Entity target = arena.getPlayer().getTargetEnemy();
                        arena.getPlayer().castSpell(spellIndex, target);
                    }
                    repaint();
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
        hudRenderer.setGold(arena.getPlayer().getGold());

        int blue = 0, red = 0;
        blue += arena.getPlayer().getKDA().getKills();
        for (Bot b : arena.getBotManager().getAllBots()) {
            if (b.getTeam() == 0) blue += b.getKDA().getKills();
            else red += b.getKDA().getKills();
        }
        hudRenderer.setKills(blue, red);

        hudRenderer.render(g2);
        shopPanel.render(g2, windowWidth, windowHeight);
    }

    public void toggleShop() {
        shopPanel.toggle();
        repaint();
    }
}
