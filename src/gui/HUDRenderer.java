package gui;

import engine.mobile.Player;
import engine.process.Arena;
import engine.map.TilesManager;
import engine.map.Tile;
import config.GameConfiguration;

import java.awt.*;

public class HUDRenderer {
    private final Player player;
    private final Arena arena;
    private final TilesManager tilesManager;
    private int screenWidth;
    private int screenHeight;
    private boolean paused = false;
    private long matchStartTime;
    private int blueKills = 0;
    private int redKills = 0;
    private int playerGold = 0;

    public HUDRenderer(Player player, Arena arena, TilesManager tilesManager) {
        this.player = player;
        this.arena = arena;
        this.tilesManager = tilesManager;
        this.matchStartTime = System.currentTimeMillis();
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setKills(int blue, int red) {
        this.blueKills = blue;
        this.redKills = red;
    }

    public void setGold(int gold) {
        this.playerGold = gold;
    }

    public void render(Graphics2D g2) {
        int margin = 10;
        
        renderMinimap(g2, screenWidth - 180 - margin, margin, 180, 180);
        
        renderScoreboard(g2, margin, margin);
        
        renderGoldDisplay(g2, screenWidth - 100, 200);
        
        renderCharacterPanel(g2, margin, screenHeight - 160);
        
        renderAbilityBar(g2, screenWidth - 140 - margin, screenHeight - 55);
        
        renderItemBar(g2, screenWidth - 140 - margin, screenHeight - 110);
        
        renderPauseButton(g2, margin, 100);
        
        if (!player.isActive()) {
            renderRespawnOverlay(g2);
        }
    }

    private void renderMinimap(Graphics2D g2, int x, int y, int size, int sizeY) {
        g2.setColor(new Color(20, 20, 30, 230));
        g2.fillRect(x - 3, y - 3, size + 6, size + 6);
        
        g2.setColor(new Color(40, 40, 50));
        g2.fillRect(x, y, size, size);
        
        double scaleX = (double) size / GameConfiguration.WORLD_WIDTH;
        double scaleY = (double) size / GameConfiguration.WORLD_HEIGHT;
        
        int mapRows = GameConfiguration.TILE_ROWS;
        int mapCols = GameConfiguration.TILE_COLS;
        
        for (int row = 0; row < mapRows; row++) {
            for (int col = 0; col < mapCols; col++) {
                int tileId = tilesManager.mapTileNum[row][col];
                if (tileId >= 0 && tileId < tilesManager.tiles.length && tilesManager.tiles[tileId] != null) {
                    g2.setColor(tilesManager.tiles[tileId].getColor());
                    int px = x + (int)(col * GameConfiguration.TILE_SIZE * scaleX);
                    int py = y + (int)(row * GameConfiguration.TILE_SIZE * scaleY);
                    int w = Math.max(1, (int)((col + 1) * GameConfiguration.TILE_SIZE * scaleX) - px);
                    int h = Math.max(1, (int)((row + 1) * GameConfiguration.TILE_SIZE * scaleY) - py);
                    g2.fillRect(px, py, w, h);
                }
            }
        }
        
        for (var lane : arena.lanes) {
            for (var tower : lane.getAllTowers()) {
                int px = x + (int)(tower.getX() * scaleX);
                int py = y + (int)(tower.getY() * scaleY);
                g2.setColor(tower.getTeam() == 0 ? Color.BLUE : Color.RED);
                g2.fillRect(px - 2, py - 2, 4, 4);
            }
        }
        
        int playerX = x + (int)(player.getX() * scaleX);
        int playerY = y + (int)(player.getY() * scaleY);
        g2.setColor(Color.YELLOW);
        g2.fillOval(playerX - 4, playerY - 4, 8, 8);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 9));
        g2.drawString("LMB: Move", x + 5, y + size - 5);
        
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, size, size);
    }

    private void renderScoreboard(Graphics2D g2, int x, int y) {
        int width = 120;
        int height = 60;
        
        g2.setColor(new Color(25, 25, 35, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        long elapsed = System.currentTimeMillis() - matchStartTime;
        int minutes = (int)(elapsed / 60000);
        int seconds = (int)((elapsed % 60000) / 1000);
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(timerText, x + (width - fm.stringWidth(timerText)) / 2, y + 22);
        
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(new Color(100, 150, 255));
        g2.drawString("Blue: " + blueKills, x + 5, y + 40);
        
        g2.setColor(new Color(255, 100, 100));
        g2.drawString("Red: " + redKills, x + 5, y + 55);
    }

    private void renderGoldDisplay(Graphics2D g2, int x, int y) {
        int width = 90;
        int height = 25;
        
        g2.setColor(new Color(30, 30, 40, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("" + playerGold, x + 10, y + 18);
        
        g2.setColor(new Color(200, 200, 150));
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("GOLD", x + 60, y + 18);
    }

    private void renderCharacterPanel(Graphics2D g2, int x, int y) {
        int width = 200;
        int height = 140;
        
        g2.setColor(new Color(25, 25, 35, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        g2.setColor(new Color(50, 50, 70));
        g2.fillRect(x + 10, y + 10, 40, 40);
        g2.setColor(Color.GRAY);
        g2.drawRect(x + 10, y + 10, 40, 40);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Hero", x + 58, y + 24);
        
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(new Color(180, 180, 200));
        g2.drawString("Lv.1", x + 58, y + 38);
        
        int barX = x + 10;
        int barY = y + 60;
        int barWidth = width - 20;
        int barHeight = 14;
        
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        double hpPct = player.getHp() / player.getMaxHp();
        g2.setColor(new Color(50, 200, 50));
        g2.fillRect(barX, barY, (int)(hpPct * barWidth), barHeight);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("HP", barX + 2, barY + 10);
        String hpText = (int)player.getHp() + "/" + (int)player.getMaxHp();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hpText, barX + barWidth - fm.stringWidth(hpText) - 2, barY + 10);
        
        barY += 18;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        double manaPct = player.getMana() / player.getMaxMana();
        g2.setColor(new Color(50, 100, 200));
        g2.fillRect(barX, barY, (int)(manaPct * barWidth), barHeight);
        g2.setColor(Color.WHITE);
        g2.drawString("MANA", barX + 2, barY + 10);
        String manaText = (int)player.getMana() + "/" + (int)player.getMaxMana();
        g2.drawString(manaText, barX + barWidth - fm.stringWidth(manaText) - 2, barY + 10);
        
        int statsY = y + 110;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("ATK:20", x + 10, statsY);
        g2.drawString("DEF:0", x + 70, statsY);
        g2.drawString("SPD:" + (int)player.getSpeed(), x + 130, statsY);
    }

    private void renderAbilityBar(Graphics2D g2, int x, int y) {
        int width = 130;
        int height = 45;
        
        g2.setColor(new Color(25, 25, 35, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        int slotSize = 32;
        int gap = 4;
        int startX = x + 10;
        
        for (int i = 0; i < 3; i++) {
            int slotX = startX + i * (slotSize + gap);
            int slotY = y + 6;
            
            g2.setColor(new Color(40, 40, 60));
            g2.fillRect(slotX, slotY, slotSize, slotSize);
            g2.setColor(new Color(70, 70, 90));
            g2.drawRect(slotX, slotY, slotSize, slotSize);
            
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            g2.drawString((i + 1) + "", slotX + 4, slotY + 16);
        }
    }

    private void renderItemBar(Graphics2D g2, int x, int y) {
        int width = 130;
        int height = 45;
        
        g2.setColor(new Color(25, 25, 35, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        g2.setColor(new Color(180, 180, 200));
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("ITEMS", x + 5, y + 12);
        
        int slotSize = 26;
        int gap = 3;
        int startX = x + 5;
        
        for (int i = 0; i < 4; i++) {
            int slotX = startX + i * (slotSize + gap);
            int slotY = y + 15;
            
            g2.setColor(new Color(40, 40, 60));
            g2.fillRect(slotX, slotY, slotSize, slotSize);
            g2.setColor(new Color(70, 70, 90));
            g2.drawRect(slotX, slotY, slotSize, slotSize);
            
            g2.setColor(new Color(150, 150, 170));
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.drawString((i + 1) + "", slotX + 3, slotY + 10);
        }
    }

    private void renderPauseButton(Graphics2D g2, int x, int y) {
        int size = 30;
        
        g2.setColor(new Color(40, 40, 55, 220));
        g2.fillRect(x, y, size, size);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, size, size);
        
        g2.setColor(Color.WHITE);
        int barWidth = 4;
        int barHeight = 12;
        int spacing = 4;
        g2.fillRect(x + (size - barWidth * 2 - spacing) / 2, y + (size - barHeight) / 2, barWidth, barHeight);
        g2.fillRect(x + (size - barWidth * 2 - spacing) / 2 + barWidth + spacing, y + (size - barHeight) / 2, barWidth, barHeight);
    }

    private void renderRespawnOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        String text = "RESPAWNING";
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, (screenWidth - textWidth) / 2, screenHeight / 2);
    }
    
    public boolean handleMinimapClick(int clickX, int clickY) {
        int minimapSize = 180;
        int margin = 10;
        int minimapX = screenWidth - minimapSize - margin;
        int minimapY = margin;
        
        if (clickX >= minimapX && clickX <= minimapX + minimapSize &&
            clickY >= minimapY && clickY <= minimapY + minimapSize) {
            double scaleX = (double) minimapSize / GameConfiguration.WORLD_WIDTH;
            double scaleY = (double) minimapSize / GameConfiguration.WORLD_HEIGHT;
            double worldX = (clickX - minimapX) / scaleX;
            double worldY = (clickY - minimapY) / scaleY;
            player.moveTo(worldX, worldY);
            return true;
        }
        return false;
    }
    
    public boolean handlePauseButtonClick(int clickX, int clickY) {
        int pauseMargin = 10;
        int pauseY = 100;
        int pauseSize = 30;
        
        if (clickX >= pauseMargin && clickX <= pauseMargin + pauseSize &&
            clickY >= pauseY && clickY <= pauseY + pauseSize) {
            return true;
        }
        return false;
    }
}
