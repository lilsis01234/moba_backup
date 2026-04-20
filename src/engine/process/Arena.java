package engine.process;

import engine.mobile.Base;
import engine.mobile.Bot;
import engine.mobile.Entity;
import engine.mobile.EntityUtils;
import engine.mobile.Fountain;
import engine.mobile.Minion;
import engine.mobile.Personnage;
import engine.mobile.Player;
import engine.mobile.Tower;
import engine.map.TilesManager;
import game_config.GameConfiguration;

import data.model.Hero;
import data.model.GameStats;
import data.model.HeroStats;
import data.model.TeamStats;
import data.model.KDA;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import log.LoggerUtility;
import org.apache.log4j.Logger;

public class Arena {
    private static Arena instance;
    private static final Logger logger = LoggerUtility.getLogger(Arena.class);
    
    private List<Lane> lanes;
    private Player player;
    private Hero selectedHero;
    private BotManager botManager;
    private Fountain playerFountain;
    private Fountain enemyFountain;
    private Base playerBase;
    private Base enemyBase;
    private MinionSpawner minionSpawner;
    private TilesManager tilesManager;
    private GameStats gameStats;
    private long gameStartTime;

    private static final int TILE_SIZE = GameConfiguration.TILE_SIZE;

    private Arena() {}

    public static Arena getInstance() {
        return instance;
    }

    public static void init(Hero hero) {
        logger.info("Initialisation de l'arène avec le héros : " + (hero != null ? hero.getName() : "null"));
        instance = new Arena();
        instance.setup(hero);
    }

    private void setup(Hero hero) {
        this.selectedHero = hero;
        this.gameStartTime = System.currentTimeMillis();
        tilesManager = TilesManager.getInstance();

        JsonDataProvider dataProvider;
        try {
            dataProvider = JsonDataProviderFactory.getInstance();
            logger.info("héros chargé avec succès");
        } catch (IOException e) {
            logger.fatal("Erreur : Impossible de charger les données JSON des héros");
            throw new RuntimeException("Could not load heroes for bot assignment", e);
        }
        botManager = BotManager.create(dataProvider.getAllHeroes(), selectedHero);
        logger.info("BotManager créé avec " + botManager.getAllBots().size() + " bots.");

        lanes = new ArrayList<>();
        lanes.add(Lane.getInstance(Lane.Type.top));
        lanes.add(Lane.getInstance(Lane.Type.middle));
        lanes.add(Lane.getInstance(Lane.Type.bot));

        playerBase = new Base(7 * TILE_SIZE, 53 * TILE_SIZE, 0);
        enemyBase = new Base(53 * TILE_SIZE, 7 * TILE_SIZE, 1);

        player = new Player(hero);
        playerFountain = new Fountain(4 * TILE_SIZE, 56 * TILE_SIZE, 0);
        enemyFountain = new Fountain(56 * TILE_SIZE, 4 * TILE_SIZE, 1);

        minionSpawner = MinionSpawner.getInstance();
        logger.info("Installation de l'arène terminé.");
    }

    public static void reset() {
        logger.info("Réinitialisation de l'arène.");
        instance = null;
    }

    public void update(double deltaTime) {
        if (deltaTime > GameConfiguration.MAX_DELTA_TIME) {
            logger.warn("Pic de deltaTime détecté : " + deltaTime);
            deltaTime = GameConfiguration.MAX_DELTA_TIME;
        }

        ArrayList<Personnage> allCharacters = new ArrayList<>();
        allCharacters.add(player);
        for (Bot b : botManager.getAllBots()) {
            allCharacters.add(b);
        }

        ArrayList<Entity> enemiesOfTeam0 = getEnemiesForTeam(0);
        ArrayList<Entity> enemiesOfTeam1 = getEnemiesForTeam(1);

        player.update(deltaTime, this, allCharacters);
        player.respawn(deltaTime);

        botManager.update(deltaTime, enemiesOfTeam0, enemiesOfTeam1, allCharacters);

        playerFountain.update(deltaTime, enemiesOfTeam0, getAlliesForTeam(0));
        enemyFountain.update(deltaTime, enemiesOfTeam1, getAlliesForTeam(1));

        playerBase.update(deltaTime, enemiesOfTeam0);
        enemyBase.update(deltaTime, enemiesOfTeam1);

        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                ArrayList<Entity> targets = (t.getTeam() == 0) ? enemiesOfTeam0 : enemiesOfTeam1;
                t.update(deltaTime, targets);
            }
        }

        minionSpawner.update(deltaTime, player);
        for (Minion m : minionSpawner.getMinions()) {
            ArrayList<Entity> targets = (m.getTeam() == 0) ? enemiesOfTeam0 : enemiesOfTeam1;
            m.update(deltaTime, targets);
        }

        String result = checkGameOver();
        if (result != null) {
            logger.info("Partie terminée, résultat : " + result);
        }
    }

    public void renderMinimapEntities(Graphics2D g2, int miniX, int miniY, int miniWidth, int miniHeight) {
        double scaleX = (double) miniWidth / GameConfiguration.WORLD_WIDTH;
        double scaleY = (double) miniHeight / GameConfiguration.WORLD_HEIGHT;

        for (Minion m : minionSpawner.getMinions()) {
            if (!m.isActive()) continue;
            int px = miniX + (int) (m.getX() * scaleX);
            int py = miniY + (int) (m.getY() * scaleY);
            g2.setColor(m.getTeam() == 0 ? Color.CYAN : Color.ORANGE);
            g2.fillRect(px - 1, py - 1, 2, 2);
        }

        for (Bot b : botManager.getAllBots()) {
            if (!b.isActive()) continue;
            int px = miniX + (int) (b.getX() * scaleX);
            int py = miniY + (int) (b.getY() * scaleY);
            g2.setColor(b.getTeam() == 0 ? Color.BLUE : Color.MAGENTA);
            g2.fillOval(px - 2, py - 2, 4, 4);
        }
    }

    public void render(Graphics2D g2, int screenW, int screenH, Entity hovered) {
        double scale = Math.min((double) screenW / GameConfiguration.WORLD_WIDTH,
                                 (double) screenH / GameConfiguration.WORLD_HEIGHT);
        double offsetX = (screenW - GameConfiguration.WORLD_WIDTH * scale) / 2;
        double offsetY = (screenH - GameConfiguration.WORLD_HEIGHT * scale) / 2;

        AffineTransform original = g2.getTransform();
        g2.translate(offsetX, offsetY);
        g2.scale(scale, scale);

        tilesManager.render(g2, TILE_SIZE);

        playerFountain.render(g2, screenW, screenH);
        enemyFountain.render(g2, screenW, screenH);
        playerBase.render(g2, screenW, screenH);
        enemyBase.render(g2, screenW, screenH);

        botManager.render(g2, screenW, screenH);
        for (Minion m : minionSpawner.getMinions()) {
            m.render(g2, screenW, screenH);
        }

        player.render(g2, screenW, screenH);

        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                t.render(g2, screenW, screenH);
            }
        }

        if (hovered != null && hovered.isActive()) {
            renderHealthBar(g2, hovered);
        }

        g2.setTransform(original);

        int screenX = (int) Math.round(player.getX() * scale + offsetX);
        int screenY = (int) Math.round(player.getY() * scale + offsetY);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        String heroName = selectedHero != null ? selectedHero.getName() : "Hero";
        g2.drawString(heroName, screenX - 15, screenY - 20);
    }

    private void renderHealthBar(Graphics2D g2, Entity entity) {
        int barWidth = TILE_SIZE + 20;
        int barHeight = TILE_SIZE / 5;
        int barX = (int) entity.getX() - barWidth / 2;
        int barY = (int) entity.getY() - barHeight * 4;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(barX, barY, barWidth, barHeight);

        double hpPercent = entity.getHp() / entity.getMaxHp();
        g2.setColor(new Color(50, 200, 50));
        g2.fillRect(barX, barY, (int) (hpPercent * barWidth), barHeight);
        g2.setColor(Color.BLACK);
        g2.drawRect(barX, barY, barWidth, barHeight);
    }

    public Player getPlayer() {
        return player;
    }

    public ArrayList<Entity> getEnemiesForTeam(int team) {
        ArrayList<Entity> enemies = new ArrayList<>();

        if (team == 1 && player.isActive()) {
            enemies.add(player);
        }

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
        if (enemyBase.getTeam() != team && enemyBase.isActive()) enemies.add(enemyBase);

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
        return tilesManager.isSolidTile(newX, newY, TILE_SIZE);
    }

    public TilesManager getTilesManager() {
        return tilesManager;
    }

    public List<Lane> getLanes() {
        return lanes;
    }

    public Entity findClickedEnemy(double worldX, double worldY, double clickRadius) {
        for (Entity e : getEnemiesForTeam(0)) {
            if (!e.isActive()) continue;
            double dx = e.getX() - worldX;
            double dy = e.getY() - worldY;
            if (Math.sqrt(dx * dx + dy * dy) <= clickRadius) {
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

    public GameStats buildGameStats(String result) {
        long duration = System.currentTimeMillis() - gameStartTime;
        
        gameStats = new GameStats();
        gameStats.setGameResult(result);
        gameStats.setGameDuration(duration);
        gameStats.setPlayerTeamId(0);
        
        int playerTowersDestroyed = countTowersDestroyedByTeam(1);
        int enemyTowersDestroyed = countTowersDestroyedByTeam(0);
        gameStats.setTowersDestroyed(playerTowersDestroyed);
        gameStats.setEnemyTowersDestroyed(enemyTowersDestroyed);
        gameStats.setDragonsKilled(0);
        gameStats.setBaronKilled(0);
        gameStats.setEnemyDragonsKilled(0);
        gameStats.setEnemyBaronsKilled(0);

        TeamStats blueTeam = new TeamStats(0);
        TeamStats redTeam = new TeamStats(1);
        blueTeam.setTowersDestroyed(playerTowersDestroyed);
        redTeam.setTowersDestroyed(enemyTowersDestroyed);

        if (player != null && player.getKDA() != null) {
            KDA pk = player.getKDA();
            System.out.println("[STATS] Player KDA - K: " + pk.getKills() + ", D: " + pk.getDeaths() + ", A: " + pk.getAssists());
            HeroStats playerStats = new HeroStats(
                selectedHero.getId(),
                selectedHero.getName(),
                0
            );
            int kills = pk.getKills();
            int deaths = pk.getDeaths();
            int assists = pk.getAssists();
            for (int i = 0; i < kills; i++) playerStats.addKill();
            for (int i = 0; i < deaths; i++) playerStats.addDeath();
            for (int i = 0; i < assists; i++) playerStats.addAssist();
            playerStats.setGoldEarned(player.getGold());
            playerStats.setGoldSpent(0);
            playerStats.setCsCreeps(0);
            playerStats.setLevel(player.getLevel());
            playerStats.setTimePlayed(duration);
            playerStats.setTimeSpentDead(0);
            playerStats.setPlayer(true);
            playerStats.setLongestKillStreak(kills);
            blueTeam.addHero(playerStats);
        }

        for (Bot bot : botManager.getAllBots()) {
            KDA bk = bot.getKDA();
            if (bk != null) {
                System.out.println("[STATS] Bot " + bot.getHeroName() + " (Team " + bot.getTeam() + ") KDA - K: " + bk.getKills() + ", D: " + bk.getDeaths() + ", A: " + bk.getAssists());
            }
            HeroStats botStats = new HeroStats(
                bot.getHeroId(),
                bot.getHeroName(),
                bot.getTeam()
            );
            if (bk != null) {
                int bkKills = bk.getKills();
                int bkDeaths = bk.getDeaths();
                int bkAssists = bk.getAssists();
                for (int i = 0; i < bkKills; i++) botStats.addKill();
                for (int i = 0; i < bkDeaths; i++) botStats.addDeath();
                for (int i = 0; i < bkAssists; i++) botStats.addAssist();
                botStats.setLongestKillStreak(bkKills);
            }
            botStats.setGoldEarned(bot.getGold());
            botStats.setGoldSpent(0);
            botStats.setCsCreeps(0);
            botStats.setLevel(bot.getLevel());
            botStats.setTimePlayed(duration);
            botStats.setTimeSpentDead(0);

            if (bot.getTeam() == 0) {
                blueTeam.addHero(botStats);
            } else {
                redTeam.addHero(botStats);
            }
        }

        blueTeam.calculateTotals();
        redTeam.calculateTotals();
        gameStats.setTeamStats(0, blueTeam);
        gameStats.setTeamStats(1, redTeam);

        return gameStats;
    }

    private int countTowersDestroyedByTeam(int teamId) {
        int count = 0;
        for (Lane lane : lanes) {
            for (Tower t : lane.getAllTowers()) {
                if (t.getTeam() != teamId && !t.isActive()) {
                    count++;
                }
            }
        }
        return count;
    }

    public BotManager getBotManager() {
        return botManager;
    }
}
