package engine.process;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import data.model.Hero;
import engine.mobile.Base;
import engine.mobile.Bot;
import engine.mobile.Entity;
import engine.mobile.Fountain;
import engine.mobile.Minion;
import engine.mobile.Player;
import engine.mobile.Tower;
import engine.map.TilesManager;
import game_config.GameConfiguration;

public class Arena {
    public List<Lane> lanes;
    private Player player;
    private Hero selectedHero;

    // PLEASE CHANGE THE RENDER ORDER SO TOWER IS ON TOP LATER
    private BotManager botManager;
    private Fountain playerFountain;
    private Fountain enemyFountain;
    private Base playerBase;
    private Base enemyBase;
    private MinionSpawner minionSpawner;
    private TilesManager tilesManager;
    int T = GameConfiguration.TILE_SIZE;

    public Arena(Hero hero) {
        this.selectedHero = hero;
        tilesManager = new TilesManager("/game_config/map/map.txt");

        lanes = new ArrayList<>();
        lanes.add(new Lane(Lane.Type.top));
        lanes.add(new Lane(Lane.Type.middle));
        lanes.add(new Lane(Lane.Type.bot));
        
        playerBase = new Base(8 * T, 53 * T, 0);  
        enemyBase  = new Base(53 * T, 7 * T, 1); 

         player = new Player(
            GameConfiguration.PLAYER_START_X,
            GameConfiguration.PLAYER_START_Y,
            hero.getMaxHp(),
            hero.getMaxMana(),
            hero.getSpeed(),
            hero.getAtkRange()
        );

        botManager = new BotManager();

        playerFountain = new Fountain(4 * T, 56 * T, 0);
        enemyFountain  = new Fountain(56 * T, 4 * T, 1);

        minionSpawner = new MinionSpawner();
    }

    public void update(double deltaTime) {
        if (deltaTime > 0.05) deltaTime = 0.05;

        List<Entity> enemiesOfTeam0 = getEnemiesForTeam(0);
        List<Entity> enemiesOfTeam1 = getEnemiesForTeam(1);

        // player
        player.update(deltaTime, this);
        if (!player.isActive()) player.respawn();

        // bots
        botManager.update(deltaTime, enemiesOfTeam0, enemiesOfTeam1);

        // fountains
        playerFountain.update(deltaTime, enemiesOfTeam0, getAlliesForTeam(0));
        enemyFountain.update(deltaTime,  enemiesOfTeam1, getAlliesForTeam(1));

        //base
        playerBase.update(deltaTime, enemiesOfTeam0);
        enemyBase.update(deltaTime,  enemiesOfTeam1);
        // towers attack closest enemy
        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                if (!t.isActive()) continue;
                List<Entity> targets = t.getTeam() == 0 ? enemiesOfTeam0 : enemiesOfTeam1;
                Entity closest = findClosestEnemy(t, targets);
                if (closest != null) t.attack(closest, deltaTime);
            }
        }

        // minions
        minionSpawner.update(deltaTime, player);
        for (Minion m : minionSpawner.getMinions()) {
            List<Entity> targets;

            if (m.getTeam() == 0) {
                targets = enemiesOfTeam0;
            } else {
                targets = enemiesOfTeam1;
            }
            m.update(deltaTime, targets);
        }
    }

    private Entity findClosestEnemy(Entity source, List<Entity> enemies) {
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : enemies) {
            if (!e.isActive()) continue;
            double d = source.getDistanceTo(e);
            if (d < closestDist) { closestDist = d; closest = e; }
        }
        return closest;
    }

     public void renderMinimapEntities(Graphics2D g2, int miniX, int miniY, int miniWidth, int miniHeight) {
        double scaleX = (double) miniWidth  / GameConfiguration.WORLD_WIDTH;
        double scaleY = (double) miniHeight / GameConfiguration.WORLD_HEIGHT;

        for (Minion m : minionSpawner.getMinions()) {
            if (!m.isActive()) continue;
            int px = miniX + (int)(m.getX() * scaleX);
            int py = miniY + (int)(m.getY() * scaleY);
            g2.setColor(m.getTeam() == 0 ? Color.CYAN : Color.ORANGE);
            g2.fillRect(px - 1, py - 1, 2, 2);
        }

        for (Bot b : botManager.getAllBots()) {
            if (!b.isActive()) continue;
            int px = miniX + (int)(b.getX() * scaleX);
            int py = miniY + (int)(b.getY() * scaleY);
            g2.setColor(b.getTeam() == 0 ? Color.BLUE : Color.MAGENTA);
            g2.fillOval(px - 2, py - 2, 4, 4);
        }
    }


    public void render(Graphics2D g2, int screenW, int screenH) {
        // Calculate scale to fit whole world on screen
        double scale = Math.min((double)screenW / GameConfiguration.WORLD_WIDTH,
                               (double)screenH / GameConfiguration.WORLD_HEIGHT);
        double offsetX = (screenW - GameConfiguration.WORLD_WIDTH * scale) / 2;
        double offsetY = (screenH - GameConfiguration.WORLD_HEIGHT * scale) / 2;

        // Save original transform
        AffineTransform original = g2.getTransform();

        // Apply scaling and centering (correct order: translate then scale)
        // This gives: screen = offset + scale * world
        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);

        // Render tiles (world coordinates)
        tilesManager.render(g2, GameConfiguration.TILE_SIZE);

        // Render towers
        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                t.render(g2, screenW, screenH);
            }
        }

        // Render fountains
        playerFountain.render(g2, screenW, screenH);
        enemyFountain.render(g2, screenW, screenH);
        
        //base - à verif
        playerBase.render(g2, screenW, screenH);
    	enemyBase.render(g2, screenW, screenH);

        // Render bots and minions
        botManager.render(g2, screenW, screenH);
        for (Minion m : minionSpawner.getMinions()) {
            m.render(g2, screenW, screenH);
        }

        // Render player
        player.render(g2, screenW, screenH);

        // Restore original transform
        g2.setTransform(original);

        // Player's center on screen (matching transformed rendering)
        int screenX = (int)Math.round(player.getX() * scale + offsetX);
        int screenY = (int)Math.round(player.getY() * scale + offsetY);

        // Bar dimensions
        int barWidth = 80;
        int barHeight = 8;
        int barSpacing = 3;
        int barX = screenX - barWidth / 2;
        // Position bars above player sprite
        int spriteHalfSize = (int)(GameConfiguration.TILE_SIZE * scale / 2);
        int barY = screenY - spriteHalfSize - 30;

        // Background for readability
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(barX - 4, barY - 4, barWidth + 8, barHeight * 2 + barSpacing + 8);

        // Mana bar (top)
        g2.setColor(Color.GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        double manaPct = (double)player.getMana() / player.getMaxMana();
        g2.setColor(Color.CYAN);
        g2.fillRect(barX, barY, (int)(manaPct * barWidth), barHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);

        // HP bar (below mana)
        int hpY = barY - barHeight - barSpacing;
        g2.setColor(Color.GRAY);
        g2.fillRect(barX, hpY, barWidth, barHeight);
        double hpPct = (double)player.getHp() / player.getMaxHp();
        g2.setColor(Color.GREEN);
        g2.fillRect(barX, hpY, (int)(hpPct * barWidth), barHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, hpY, barWidth, barHeight);

        // Player name
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String heroName = selectedHero != null ? selectedHero.getName() : "Hero";
        g2.drawString(heroName, screenX - 15, hpY - 5);

        // Draw health bars for bots in screen space
        int botBarWidth = 60;
        int botBarHeight = 6;
        int botBarSpacing = 2;
        for (Bot b : botManager.getAllBots()) {
            if (!b.isActive()) continue;
            int bx = (int)Math.round(b.getX() * scale + offsetX);
            int by = (int)Math.round(b.getY() * scale + offsetY);
            int spriteHalf = (int)(GameConfiguration.TILE_SIZE * scale / 2);
            int botBarX = bx - botBarWidth / 2;
            int botBarY = by - spriteHalf - 25;

            // Background (expand if mana present)
            boolean hasMana = b.getMaxMana() > 0;
            int bgHeight = botBarHeight + (hasMana ? botBarHeight + botBarSpacing : 0) + 4;
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(botBarX - 2, botBarY - (hasMana ? botBarHeight + botBarSpacing : 0) - 2, botBarWidth + 4, bgHeight);

            if (hasMana) {
                // Mana bar (top)
                int manaY = botBarY;
                g2.setColor(Color.GRAY);
                g2.fillRect(botBarX, manaY, botBarWidth, botBarHeight);
                double botManaPct = (double)b.getMana() / b.getMaxMana();
                g2.setColor(Color.CYAN);
                g2.fillRect(botBarX, manaY, (int)(botManaPct * botBarWidth), botBarHeight);
                g2.setColor(Color.BLACK);
                g2.drawRect(botBarX, manaY, botBarWidth, botBarHeight);
                botBarY += botBarHeight + botBarSpacing; // move down for health bar
            }

            // Health bar
            g2.setColor(Color.GRAY);
            g2.fillRect(botBarX, botBarY, botBarWidth, botBarHeight);
            double botHpPct = (double)b.getHp() / b.getMaxHp();
            g2.setColor(Color.GREEN);
            g2.fillRect(botBarX, botBarY, (int)(botHpPct * botBarWidth), botBarHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(botBarX, botBarY, botBarWidth, botBarHeight);
        }

        // Minion health bars (health only)
        int minionBarWidth = 40;
        int minionBarHeight = 4;
        for (Minion m : minionSpawner.getMinions()) {
            if (!m.isActive()) continue;
            int mx = (int)Math.round(m.getX() * scale + offsetX);
            int my = (int)Math.round(m.getY() * scale + offsetY);
            int spriteHalf = (int)(GameConfiguration.TILE_SIZE * scale / 2);
            int minionBarX = mx - minionBarWidth / 2;
            int minionBarY = my - spriteHalf - 10;

            // Background
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(minionBarX - 2, minionBarY - 2, minionBarWidth + 4, minionBarHeight + 4);

            // Health fill
            g2.setColor(Color.GRAY);
            g2.fillRect(minionBarX, minionBarY, minionBarWidth, minionBarHeight);
            double minionHpPct = (double)m.getHp() / m.getMaxHp();
            g2.setColor(Color.GREEN);
            g2.fillRect(minionBarX, minionBarY, (int)(minionHpPct * minionBarWidth), minionBarHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(minionBarX, minionBarY, minionBarWidth, minionBarHeight);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<Entity> getEnemiesForTeam(int team) {
        List<Entity> enemies = new ArrayList<>();

        // player is team 0, so enemy of team 1
        if (team == 1 && player.isActive()) enemies.add(player);

        for (Bot b : botManager.getAllBots()) {
            if (b.getTeam() != team && b.isActive()) enemies.add(b);
        }
        for (Minion m : minionSpawner.getMinions()) {
            if (m.getTeam() != team && m.isActive()) enemies.add(m);
        }
        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                if (t.getTeam() != team && t.isActive()) enemies.add(t);
            }
        }
        
        if (playerBase.getTeam() != team && playerBase.isActive()) enemies.add(playerBase);
        if (enemyBase.getTeam()  != team && enemyBase.isActive())  enemies.add(enemyBase);
        
        
        return enemies;
    }

    public List<Entity> getAlliesForTeam(int team) {
        List<Entity> allies = new ArrayList<>();

        if (team == 0 && player.isActive()) allies.add(player);

        for (Bot b : botManager.getAllBots()) {
            if (b.getTeam() == team && b.isActive()) allies.add(b);
        }
        for (Minion m : minionSpawner.getMinions()) {
            if (m.getTeam() == team && m.isActive()) allies.add(m);
        }
        return allies;
    }

    public boolean isCollidingWithWall(double newX, double newY) {
        return tilesManager.isSolidTile(newX, newY, GameConfiguration.TILE_SIZE);
    }

    public TilesManager getTilesManager() {
        return tilesManager;
    }
 
    public Entity findClickedEnemy(double worldX, double worldY, double clickRadius) {
        for (Entity e : getEnemiesForTeam(0)) {
            if (!e.isActive()) continue;    
            double dx = e.getX() - worldX; 
            double dy = e.getY() - worldY;
            if (Math.sqrt(dx * dx + dy * dy) <= clickRadius) { //we allow a margin or error or else its unplayable
                return e;
            }
        }
        return null;
    }
}
