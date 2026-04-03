package engine.process;
import data.model.Hero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import engine.mobile.Bot;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.awt.Graphics2D;


public class BotManager {

    private List<Bot> bots      = new ArrayList<>();
    private List<Bot> enemyBots = new ArrayList<>();
    int T = GameConfiguration.TILE_SIZE;

    public BotManager(List<Hero> allHeroes, Hero playerHero) {
        int T = GameConfiguration.TILE_SIZE;

        // get all the heros exept the player's cus allies cant have the same hero
        List<Hero> allyPool = new ArrayList<>(allHeroes);
        allyPool.remove(playerHero); 
        Collections.shuffle(allyPool);
        
        
        bots.add(new Bot(8*T, 52*T, getBotWaypoints("Bot1"), 0, "Bot1", allyPool.get(0)));
        bots.add(new Bot(9*T, 52*T, getBotWaypoints("Bot2"), 0, "Bot2", allyPool.get(1)));
        bots.add(new Bot(8*T, 54*T, getBotWaypoints("Bot3"), 0, "Bot3", allyPool.get(2)));
        bots.add(new Bot(7*T, 55*T, getBotWaypoints("Bot4"), 0, "Bot4", allyPool.get(3)));

        
        List<Hero> enemyPool = new ArrayList<>(allHeroes);
        Collections.shuffle(enemyPool);

        enemyBots.add(new Bot(52*T, 6*T, getBotWaypoints("ENEMY_Bot1"), 1, "EBot1", enemyPool.get(0)));
        enemyBots.add(new Bot(52*T, 9*T, getBotWaypoints("ENEMY_Bot2"), 1, "EBot2", enemyPool.get(1)));
        enemyBots.add(new Bot(54*T, 8*T, getBotWaypoints("ENEMY_Bot3"), 1, "EBot3", enemyPool.get(2)));
        enemyBots.add(new Bot(55*T, 7*T, getBotWaypoints("ENEMY_Bot4"), 1, "EBot4", enemyPool.get(3)));
        enemyBots.add(new Bot(52*T, 7*T, getBotWaypoints("ENEMY_Bot5"), 1, "EBot5", enemyPool.get(4)));
    }

    public void update(double deltaTime, List<Entity> allEnemiesForTeam0, List<Entity> allEnemiesForTeam1) {
        List<Bot> allBots = getAllBots();
        for (Bot b : bots)      { b.update(deltaTime, allEnemiesForTeam0, allBots); }
        for (Bot b : enemyBots) { b.update(deltaTime, allEnemiesForTeam1, allBots); }
    }

    public void render(Graphics2D g2, int width, int height) {
        for (Bot b : bots)      { b.render(g2, width, height); }
        for (Bot b : enemyBots) { b.render(g2, width, height); }
    }

    public List<Bot> getAllBots() {
        List<Bot> all = new ArrayList<>(bots);
        all.addAll(enemyBots);
        return all;
    }

    private List<double[]> getBotWaypoints(String botName) {
        int T = GameConfiguration.TILE_SIZE;
        List<double[]> wp = new ArrayList<>();
        switch (botName) {
        case "Bot1":
        	wp.add(new double[]{8 * T, 52 * T});
            wp.add(new double[]{6 * T, 44 * T});
            wp.add(new double[]{6 * T, 29 * T});
            wp.add(new double[]{6 * T, 7 * T});
            wp.add(new double[]{21 * T, 6 * T});
            wp.add(new double[]{36 * T, 6 * T});
            wp.add(new double[]{52 * T, 6 * T});
            break;
        case "Bot2":
        	wp.add(new double[]{9 * T, 52 * T});
            wp.add(new double[]{16 * T, 44 * T});
            wp.add(new double[]{30 * T, 30 * T});
            wp.add(new double[]{44 * T, 16 * T});
            wp.add(new double[]{52 * T, 8 * T});
            break;
        case "Bot3":
        	wp.add(new double[]{8 * T, 54 * T});
            wp.add(new double[]{21 * T, 54 * T});
            wp.add(new double[]{36 * T, 54 * T});
            wp.add(new double[]{54 * T, 54 * T});
            wp.add(new double[]{54 * T, 44 * T});
            wp.add(new double[]{54 * T, 8 * T});
            break;
        case "Bot4":
        	wp.add(new double[]{16 * T, 44 * T});
            wp.add(new double[]{21 * T, 55 * T});
            wp.add(new double[]{36 * T, 55 * T});
            wp.add(new double[]{54 * T, 55 * T});
            wp.add(new double[]{54 * T, 44 * T});
            wp.add(new double[]{54 * T, 8 * T});
            break;
        case "ENEMY_Bot1":
        	wp.add(new double[]{52 * T, 6 * T});
            wp.add(new double[]{7 * T, 55 * T});
            wp.add(new double[]{29 * T, 6 * T});
            wp.add(new double[]{6 * T, 6 * T});
            wp.add(new double[]{6 * T, 21 * T});
            wp.add(new double[]{6 * T, 44 * T});
            wp.add(new double[]{6 * T, 52 * T});
            break;
        case "ENEMY_Bot2":
        	wp.add(new double[]{52 * T, 9 * T});
            wp.add(new double[]{44 * T, 16 * T});
            wp.add(new double[]{30 * T, 30 * T});
            wp.add(new double[]{16 * T, 44 * T});
            wp.add(new double[]{8 * T, 52 * T});
            break;
        case "ENEMY_Bot3":
        	wp.add(new double[]{54 * T, 8 * T});
            wp.add(new double[]{54 * T, 21 * T});
            wp.add(new double[]{54 * T, 36 * T});
            wp.add(new double[]{54 * T, 54 * T});
            wp.add(new double[]{44 * T, 54 * T});
            wp.add(new double[]{8 * T, 54 * T});
            break;
        case "ENEMY_Bot4":
        	wp.add(new double[]{55 * T, 7 * T});
            wp.add(new double[]{55 * T, 21 * T});
            wp.add(new double[]{55 * T, 36 * T});
            wp.add(new double[]{55 * T, 54 * T});
            wp.add(new double[]{44 * T, 55 * T});
            wp.add(new double[]{8 * T, 55 * T});
            break;
        case "ENEMY_Bot5":
        	wp.add(new double[]{52 * T, 7 * T});
            wp.add(new double[]{44 * T, 7 * T});	
            wp.add(new double[]{29 * T, 7 * T});
            wp.add(new double[]{7 * T, 7 * T});
            wp.add(new double[]{7 * T, 21 * T});
            wp.add(new double[]{7 * T, 44 * T});
            wp.add(new double[]{7 * T, 52 * T});
            break;
        }
        return wp;
    }
}
