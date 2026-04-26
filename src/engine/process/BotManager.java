package engine.process;
import data.model.Hero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import engine.mobile.Bot;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.awt.Graphics2D;
import org.apache.log4j.Logger;

import engine.mobile.Personnage;
import log.LoggerUtility;

public class BotManager {

    private static final Logger logger = LoggerUtility.getLogger(BotManager.class);

    private static BotManager instance;
    private List<Bot> bots      = new ArrayList<>();
    private List<Bot> enemyBots = new ArrayList<>();

    private BotManager() {}

    public static BotManager getInstance() {
        return instance;
    }

    public static BotManager create(List<Hero> allHeroes, Hero playerHero) {
        logger.info("Creating BotManager with " + allHeroes.size() + " heroes, player hero: " + playerHero.getName());
        instance = new BotManager();
        instance.init(allHeroes, playerHero);
        return instance;
    }

    public static void reset() {
        instance = null;
    }

public void init(List<Hero> allHeroes, Hero playerHero) {
        logger.info("Initializing BotManager bots");
        List<Hero> allyPool = new ArrayList<>(allHeroes);
        allyPool.removeIf(h -> h.getName().equals(playerHero.getName()));
        Collections.shuffle(allyPool);

        bots.add(new Bot(getBotWaypoints("Bot1"), 0, "Bot1", allyPool.get(0)));
        bots.add(new Bot(getBotWaypoints("Bot2"), 0, "Bot2", allyPool.get(1)));
        bots.add(new Bot(getBotWaypoints("Bot3"), 0, "Bot3", allyPool.get(2)));
        bots.add(new Bot(getBotWaypoints("Bot4"), 0, "Bot4", allyPool.get(3)));
        logger.debug("Created " + bots.size() + " ally bots");

        
        List<Hero> enemyPool = new ArrayList<>(allHeroes);
        Collections.shuffle(enemyPool);

        enemyBots.add(new Bot(getBotWaypoints("ENEMY_Bot1"), 1, "EBot1", enemyPool.get(0)));
        enemyBots.add(new Bot(getBotWaypoints("ENEMY_Bot2"), 1, "EBot2", enemyPool.get(1)));
        enemyBots.add(new Bot(getBotWaypoints("ENEMY_Bot3"), 1, "EBot3", enemyPool.get(2)));
        enemyBots.add(new Bot(getBotWaypoints("ENEMY_Bot4"), 1, "EBot4", enemyPool.get(3)));
        enemyBots.add(new Bot(getBotWaypoints("ENEMY_Bot5"), 1, "EBot5", enemyPool.get(4)));
        logger.debug("Created " + enemyBots.size() + " enemy bots");
        
        for (Bot b : bots) {
            b.addGold(GameConfiguration.STARTING_GOLD);
        }
        for (Bot b : enemyBots) {
            b.addGold(GameConfiguration.STARTING_GOLD);
        }
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
        if (botName.equals("Bot1")) {
        	wp.add(new double[]{8 * T, 52 * T});
            wp.add(new double[]{6 * T, 44 * T});
            wp.add(new double[]{6 * T, 29 * T});
            wp.add(new double[]{6 * T, 7 * T});
            wp.add(new double[]{21 * T, 6 * T});
            wp.add(new double[]{36 * T, 6 * T});
            wp.add(new double[]{52 * T, 6 * T});
        } else if (botName.equals("Bot2")) {
        	wp.add(new double[]{9 * T, 52 * T});
            wp.add(new double[]{16 * T, 44 * T});
            wp.add(new double[]{30 * T, 30 * T});
            wp.add(new double[]{44 * T, 16 * T});
            wp.add(new double[]{52 * T, 8 * T});
        } else if (botName.equals("Bot3")) {
        	wp.add(new double[]{8 * T, 54 * T});
            wp.add(new double[]{21 * T, 54 * T});
            wp.add(new double[]{36 * T, 54 * T});
            wp.add(new double[]{54 * T, 54 * T});
            wp.add(new double[]{54 * T, 44 * T});
            wp.add(new double[]{54 * T, 8 * T});
        } else if (botName.equals("Bot4")) {
        	wp.add(new double[]{17 * T, 55 * T}); 
            wp.add(new double[]{32 * T, 55 * T}); 
            wp.add(new double[]{47 * T, 55 * T}); 
            wp.add(new double[]{55 * T, 55 * T}); // Corner
            wp.add(new double[]{55 * T, 49 * T}); 
            wp.add(new double[]{55 * T, 34 * T}); 
            wp.add(new double[]{55 * T, 19 * T}); 
            wp.add(new double[]{53 * T, 7 * T});
        } else if (botName.equals("ENEMY_Bot1")) {
        	wp.add(new double[]{52 * T, 6 * T});
            wp.add(new double[]{7 * T, 55 * T});
            wp.add(new double[]{29 * T, 6 * T});
            wp.add(new double[]{6 * T, 6 * T});
            wp.add(new double[]{6 * T, 21 * T});
            wp.add(new double[]{6 * T, 44 * T});
            wp.add(new double[]{6 * T, 52 * T});
        } else if (botName.equals("ENEMY_Bot2")) {
        	wp.add(new double[]{52 * T, 9 * T});
            wp.add(new double[]{44 * T, 16 * T});
            wp.add(new double[]{30 * T, 30 * T});
            wp.add(new double[]{16 * T, 44 * T});
            wp.add(new double[]{8 * T, 52 * T});
        } else if (botName.equals("ENEMY_Bot3")) {
        	wp.add(new double[]{54 * T, 8 * T});
            wp.add(new double[]{54 * T, 21 * T});
            wp.add(new double[]{54 * T, 36 * T});
            wp.add(new double[]{54 * T, 54 * T});
            wp.add(new double[]{44 * T, 54 * T});
            wp.add(new double[]{8 * T, 54 * T});
        } else if (botName.equals("ENEMY_Bot4")) {
        	wp.add(new double[]{55 * T, 7 * T});
            wp.add(new double[]{55 * T, 21 * T});
            wp.add(new double[]{55 * T, 36 * T});
            wp.add(new double[]{55 * T, 54 * T});
            wp.add(new double[]{44 * T, 55 * T});
            wp.add(new double[]{8 * T, 55 * T});
        } else if (botName.equals("ENEMY_Bot5")) {
        	wp.add(new double[]{52 * T, 7 * T});
            wp.add(new double[]{44 * T, 7 * T});	
            wp.add(new double[]{29 * T, 7 * T});
            wp.add(new double[]{7 * T, 7 * T});
            wp.add(new double[]{7 * T, 21 * T});
            wp.add(new double[]{7 * T, 44 * T});
            wp.add(new double[]{7 * T, 52 * T});
        }
        return wp;
    }
}
