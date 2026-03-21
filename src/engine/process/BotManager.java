package engine.process;

import engine.mobile.Bot;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class BotManager {

    private List<Bot> bots      = new ArrayList<>();
    private List<Bot> enemyBots = new ArrayList<>();
    int T = GameConfiguration.TILE_SIZE;

    public BotManager() {
        int W = GameConfiguration.WORLD_WIDTH;
        int H = GameConfiguration.WORLD_HEIGHT;

        int T = GameConfiguration.TILE_SIZE;
        bots.add(new Bot(12 * T, 48 * T, getBotWaypoints("Bot1"), 0, "Bot1"));
        bots.add(new Bot(13 * T, 48 * T, getBotWaypoints("Bot2"), 0, "Bot2"));
        bots.add(new Bot(12 * T, 50 * T, getBotWaypoints("Bot3"), 0, "Bot3"));
        bots.add(new Bot(11 * T, 51 * T, getBotWaypoints("Bot4"), 0, "Bot4"));

        enemyBots.add(new Bot(48 * T, 10 * T, getBotWaypoints("ENEMY_Bot1"), 1, "ENEMY_Bot1"));
        enemyBots.add(new Bot(48 * T, 13 * T, getBotWaypoints("ENEMY_Bot2"), 1, "ENEMY_Bot2"));
        enemyBots.add(new Bot(50 * T, 12 * T, getBotWaypoints("ENEMY_Bot3"), 1, "ENEMY_Bot3"));
        enemyBots.add(new Bot(51 * T, 11 * T, getBotWaypoints("ENEMY_Bot4"), 1, "ENEMY_Bot4"));
        enemyBots.add(new Bot(48 * T, 11 * T, getBotWaypoints("ENEMY_Bot5"), 1, "ENEMY_Bot5"));
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
                wp.add(new double[]{10 * T, 40 * T});
                wp.add(new double[]{10 * T, 25 * T});
                wp.add(new double[]{10 * T, 11 * T});
                wp.add(new double[]{25 * T, 10 * T});
                wp.add(new double[]{40 * T, 10 * T});
                wp.add(new double[]{48 * T, 10 * T});
                break;
            case "Bot2":
                wp.add(new double[]{20 * T, 40 * T});
                wp.add(new double[]{30 * T, 30 * T});
                wp.add(new double[]{40 * T, 20 * T});
                wp.add(new double[]{48 * T, 12 * T});
                break;
            case "Bot3":
                wp.add(new double[]{25 * T, 50 * T});
                wp.add(new double[]{40 * T, 50 * T});
                wp.add(new double[]{50 * T, 50 * T});
                wp.add(new double[]{50 * T, 40 * T});
                wp.add(new double[]{50 * T, 12 * T});
                break;
            case "Bot4":
                wp.add(new double[]{25 * T, 51 * T});
                wp.add(new double[]{40 * T, 51 * T});
                wp.add(new double[]{50 * T, 51 * T});
                wp.add(new double[]{50 * T, 40 * T});
                wp.add(new double[]{50 * T, 12 * T});
                break;
            case "ENEMY_Bot1":
                wp.add(new double[]{40 * T, 10 * T});
                wp.add(new double[]{25 * T, 10 * T});
                wp.add(new double[]{10 * T, 10 * T});
                wp.add(new double[]{10 * T, 25 * T});
                wp.add(new double[]{10 * T, 40 * T});
                wp.add(new double[]{10 * T, 48 * T});
                break;
            case "ENEMY_Bot2":
                wp.add(new double[]{40 * T, 20 * T});
                wp.add(new double[]{30 * T, 30 * T});
                wp.add(new double[]{20 * T, 40 * T});
                wp.add(new double[]{12 * T, 48 * T});
                break;
            case "ENEMY_Bot3":
                wp.add(new double[]{50 * T, 25 * T});
                wp.add(new double[]{50 * T, 40 * T});
                wp.add(new double[]{50 * T, 50 * T});
                wp.add(new double[]{40 * T, 50 * T});
                wp.add(new double[]{12 * T, 50 * T});
                break;
            case "ENEMY_Bot4":
                wp.add(new double[]{51 * T, 25 * T});
                wp.add(new double[]{51 * T, 40 * T});
                wp.add(new double[]{51 * T, 50 * T});
                wp.add(new double[]{40 * T, 51 * T});
                wp.add(new double[]{12 * T, 51 * T});
                break;
            case "ENEMY_Bot5":
                wp.add(new double[]{40 * T, 11 * T});
                wp.add(new double[]{25 * T, 11 * T});
                wp.add(new double[]{11 * T, 11 * T});
                wp.add(new double[]{11 * T, 25 * T});
                wp.add(new double[]{11 * T, 40 * T});
                wp.add(new double[]{11 * T, 48 * T});
                break;
        }
        return wp;
    }
}
