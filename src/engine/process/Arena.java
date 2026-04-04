package engine.process;
import engine.mobile.Personnage;
import engine.mobile.EntityUtils;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.IOException;
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
import engine.mobile.Personnage;
import game_config.GameConfiguration;

public class Arena {
    public List<Lane> lanes;
    private Player player;
    private Hero selectedHero;

    
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
        
        JsonDataProvider dataProvider;
        try {
            dataProvider = new JsonDataProvider();
        } catch (IOException e) {
            throw new RuntimeException("Could not load heroes for bot assignment", e);
        }
        botManager = new BotManager(dataProvider.getAllHeroes(), selectedHero);

        lanes = new ArrayList<>();
        lanes.add(new Lane(Lane.Type.top));
        lanes.add(new Lane(Lane.Type.middle));
        lanes.add(new Lane(Lane.Type.bot));
        
        playerBase = new Base(7 * T, 53 * T, 0);  
        enemyBase  = new Base(53 * T, 7 * T, 1); 

        player = new Player(hero);
        player.loadHeroGraphics(hero.getSpriteFile());

        playerFountain = new Fountain(4 * T, 56 * T, 0);
        enemyFountain  = new Fountain(56 * T, 4 * T, 1);

        minionSpawner = new MinionSpawner();
    }

    public void update(double deltaTime) {
        if (deltaTime > 0.05) deltaTime = 0.05;
        
        ArrayList<Personnage> allPersonnages = new ArrayList<>();
        allPersonnages.add(player);
        for (Bot b : botManager.getAllBots()) allPersonnages.add(b);

        ArrayList<Entity> enemiesOfTeam0 = getEnemiesForTeam(0);
        ArrayList<Entity> enemiesOfTeam1 = getEnemiesForTeam(1);

        // player
        player.update(deltaTime, this, allPersonnages);
        player.respawn(deltaTime);

        // bots
        botManager.update(deltaTime, enemiesOfTeam0, enemiesOfTeam1, allPersonnages);

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
        return EntityUtils.findClosest(source, enemies);
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


     public void render(Graphics2D g2, int screenW, int screenH, Entity hovered){
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
        // Render towers
        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                t.render(g2, screenW, screenH);
            }
        }
        
        //hp bar on hover 
        
        if (hovered != null && hovered.isActive()) {
            int px = (int) hovered.getX();
            int py = (int) hovered.getY();
            int barWidth = GameConfiguration.TILE_SIZE + 20;
            int barHeight = GameConfiguration.TILE_SIZE/5;
            int barX = px - barWidth / 2;
            int barY = py - barHeight*4 ;

            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(barX, barY, barWidth, barHeight);

            double hpPercent = hovered.getHp() / hovered.getMaxHp();
            
            Color barColor;
            barColor = new Color(50, 200, 50);                       
            g2.setColor(barColor);
            g2.fillRect(barX, barY, (int)(hpPercent * barWidth), barHeight);
            g2.setColor(Color.BLACK);
            g2.drawRect(barX, barY, barWidth, barHeight);
        }


        // Restore original transform
        g2.setTransform(original);

        // Player's center on screen (matching transformed rendering)
        int screenX = (int)Math.round(player.getX() * scale + offsetX);
        int screenY = (int)Math.round(player.getY() * scale + offsetY);


        // Player name
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String heroName = selectedHero != null ? selectedHero.getName() : "Hero";
        g2.drawString(heroName, screenX - 15, screenY-20);

        
        
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Entity> getEnemiesForTeam(int team) {
        ArrayList<Entity> enemies = new ArrayList<>();

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

    public ArrayList<Entity> getAlliesForTeam(int team) {
        ArrayList<Entity> allies = new ArrayList<>();

        if (team == 0 && player.isActive()) allies.add(player);

        for (Bot b : botManager.getAllBots()) {
            if (b.getTeam() == team && b.isActive()) allies.add(b);
        }
        for (Minion m : minionSpawner.getMinions()) {
            if (m.getTeam() == team && m.isActive()) allies.add(m);
        }
        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                if (t.getTeam() == team && t.isActive()) allies.add(t);
            }
        }
        if (playerBase.getTeam() == team && playerBase.isActive()) allies.add(playerBase);
        if (enemyBase.getTeam() == team && enemyBase.isActive()) allies.add(enemyBase);
        
        return allies;
        
    }

    public boolean isCollidingWithWall(double newX, double newY) {
        return tilesManager.isSolidTile(newX, newY, GameConfiguration.TILE_SIZE);
    }

    public TilesManager getTilesManager() {
        return tilesManager;
    }
 
    //one methode for attack the second for hp render
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
    
    public Entity findEntityAtPosition(double worldX, double worldY, double radius) {
        List<Entity> all = new ArrayList<>();
        all.addAll(getEnemiesForTeam(0));
        all.addAll(getAlliesForTeam(0));
        for (Entity e : all) {
            if (!e.isActive()) continue;
            double dx = e.getX() - worldX;
            double dy = e.getY() - worldY;
            if (Math.sqrt(dx * dx + dy * dy) <= radius) return e;
        }
        return null;
    }
    
    public String checkGameOver() {
        if (!enemyBase.isActive()) return "WIN";
        if (!playerBase.isActive()) return "LOSE";
        return null;
    }

	public BotManager getBotManager() {return botManager;}
    
}
