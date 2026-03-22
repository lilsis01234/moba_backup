package gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import game_config.GameConfiguration;
import engine.map.TilesManager;
import engine.mobile.Bot;
import engine.mobile.Minion;
import engine.mobile.Player;
import engine.process.Lane;
import engine.mobile.Tower;
import engine.process.Arena;

public class MinimapRenderer {
    private final Player player;
    private final Arena arena;
    private final TilesManager tilesManager;
    private int x, y;
    private final int size;
    private BufferedImage cachedMinimap;
    private java.util.function.Consumer<Point> onClickCallback;

    public MinimapRenderer(Player player, Arena arena, TilesManager tilesManager, int x, int y, int size) {
        this.player = player;
        this.arena = arena;
        this.tilesManager = arena.getTilesManager();
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setOnClickCallback(java.util.function.Consumer<Point> callback) {
        this.onClickCallback = callback;
    }

    public boolean handleClick(int clickX, int clickY) {
        if (clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size) {
            double relativeX = (double)(clickX - x) / size;
            double relativeY = (double)(clickY - y) / size;

            double worldX = relativeX * GameConfiguration.WORLD_WIDTH;
            double worldY = relativeY * GameConfiguration.WORLD_HEIGHT;

            player.moveTo(worldX, worldY);
            return true;
        }
        return false;
    }

    public boolean handleRightClick(int clickX, int clickY) {
        return clickX >= x && clickX <= x + size && clickY >= y && clickY <= y + size;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }

    public void render(Graphics2D g2) {
        // Fond
        g2.setColor(new Color(20, 20, 30, 230));
        g2.fillRect(x - 3, y - 3, size + 6, size + 6);
        g2.setColor(new Color(30, 30, 40));
        g2.fillRect(x, y, size, size);

        // Fond de carte (cachée)
        if (cachedMinimap == null) {
            cachedMinimap = createMinimapBackground();
        }
        g2.drawImage(cachedMinimap, x, y, null);

        double scaleX = (double) size / GameConfiguration.WORLD_WIDTH;
        double scaleY = (double) size / GameConfiguration.WORLD_HEIGHT;

        // Tours
        for ( Lane lane : arena.lanes) {
            for (Tower tower : lane.getAllTowers()) {
                int px = x + (int)(tower.getX() * scaleX);
                int py = y + (int)(tower.getY() * scaleY);
                g2.setColor(tower.getTeam() == 0 ? new Color(80, 180, 255) : new Color(255, 80, 80));
                g2.fillRect(px - 3, py - 3, 6, 6);
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(1));
                g2.drawRect(px - 3, py - 3, 6, 6);
            }
        }

        arena.renderMinimapEntities(g2, x, y, size, size);

        // Joueur
        int playerX = x + (int)(player.getX() * scaleX);
        int playerY = y + (int)(player.getY() * scaleY);
        g2.setColor(new Color(100, 255, 100));
        g2.fillOval(playerX - 5, playerY - 5, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(playerX - 5, playerY - 5, 10, 10);

        // Bordure + label
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1));
        g2.drawRect(x, y, size, size);
        g2.setColor(new Color(150, 150, 170));
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString("LMB: Move", x + 5, y + size - 8);
    }

    private BufferedImage createMinimapBackground() {
        BufferedImage bg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int mapCols = GameConfiguration.TILE_COLS;
        int mapRows = GameConfiguration.TILE_ROWS;
        float tileScaleX = size / (float) mapCols;
        float tileScaleY = size / (float) mapRows;

        for (int row = 0; row < mapRows; row++) {
            for (int col = 0; col < mapCols; col++) {
                int tileId = tilesManager.mapTileNum[row][col];
                int px = (int)(col * tileScaleX);
                int py = (int)(row * tileScaleY);
                int w = Math.max(1, (int)((col + 1) * tileScaleX) - px);
                int h = Math.max(1, (int)((row + 1) * tileScaleY) - py);

                if (tileId >= 0 && tileId < tilesManager.tiles.length && tilesManager.tiles[tileId] != null) {
                    BufferedImage img = tilesManager.tiles[tileId].image;
                    if (img != null) {
                        g2.drawImage(img, px, py, w, h, null);
                    } else {
                        g2.setColor(new Color(45, 45, 55));
                        g2.fillRect(px, py, w, h);
                    }
                } else {
                    g2.setColor(new Color(45, 45, 55));
                    g2.fillRect(px, py, w, h);
                }
            }
        }

        g2.dispose();
        return bg;
    }
}