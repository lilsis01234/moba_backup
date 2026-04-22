package engine.process;
import data.model.Hero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import engine.mobile.Bot;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.awt.Graphics2D;
import engine.mobile.Personnage;

public class BotManager {

    private static BotManager instance;
    private List<Bot> bots      = new ArrayList<>();
    private List<Bot> enemyBots = new ArrayList<>();

    private BotManager() {}

    public static BotManager getInstance() {
        return instance;
    }

    public static BotManager create(List<Hero> allHeroes, Hero playerHero) {
        instance = new BotManager();
        instance.init(allHeroes, playerHero);
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    public void init(List<Hero> allHeroes, Hero playerHero) {
        int T = GameConfiguration.TILE_SIZE;

        // get all the heros exept the player's cus allies cant have the same hero
        List<Hero> allyPool = new ArrayList<>(allHeroes);
        //compare names
	     allyPool.removeIf(h -> h.getName().equals(playerHero.getName()));		     
	     Collections.shuffle(allyPool);
        
        System.out.println("allyPool size: " + allyPool.size());
        System.out.println("allHeroes size: " + allHeroes.size());
        System.out.println("Création Bot1 avec hero: " + allyPool.get(0).getName());
        bots.add(new Bot(getBotWaypoints("Bot1"), 0, "Bot1", allyPool.get(0)));

        bots.add(new Bot( getBotWaypoints("Bot1"), 0, "Bot1", allyPool.get(0)));
        bots.add(new Bot (getBotWaypoints("Bot2"), 0, "Bot2", allyPool.get(1)));
        bots.add(new Bot( getBotWaypoints("Bot3"), 0, "Bot3", allyPool.get(2)));
        bots.add(new Bot(getBotWaypoints("Bot4"), 0, "Bot4", allyPool.get(3)));

        
        List<Hero> enemyPool = new ArrayList<>(allHeroes);
        Collections.shuffle(enemyPool);

        enemyBots.add(new Bot( getBotWaypoints("ENEMY_Bot1"), 1, "EBot1", enemyPool.get(0)));
        enemyBots.add(new Bot( getBotWaypoints("ENEMY_Bot2"), 1, "EBot2", enemyPool.get(1)));
        enemyBots.add(new Bot( getBotWaypoints("ENEMY_Bot3"), 1, "EBot3", enemyPool.get(2)));
        enemyBots.add(new Bot(getBotWaypoints("ENEMY_Bot4"), 1, "EBot4", enemyPool.get(3)));
        enemyBots.add(new Bot(getBotWaypoints("ENEMY_Bot5"), 1, "EBot5", enemyPool.get(4)));
    }

    public void update(double deltaTime, List<Entity> enemiesTeam0, ArrayList<Entity> enemiesTeam1, ArrayList<Personnage> allPersonnages) {
        ArrayList<Bot> allBots = getAllBots();
        for (Bot b : bots)      { b.update(deltaTime, enemiesTeam0, allBots, allPersonnages); }
        for (Bot b : enemyBots) { b.update(deltaTime, enemiesTeam1, allBots, allPersonnages); }
    }

    public void render(Graphics2D g2, int width, int height) {
        for (Bot b : bots)      { b.render(g2, width, height); }
        for (Bot b : enemyBots) { b.render(g2, width, height); }
    }

    public ArrayList<Bot> getAllBots() {
        ArrayList<Bot> all = new ArrayList<>(bots);
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
