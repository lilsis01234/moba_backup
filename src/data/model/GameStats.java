package data.model;

import java.util.HashMap;
import java.util.Map;

public class GameStats {
    private String gameResult;
    private long gameDuration;
    private int playerTeamId;
    private Map<Integer, HeroStats> heroStatsMap;
    private Map<Integer, TeamStats> teamStatsMap;
    private int towersDestroyed;
    private int towersLost;
    private int dragonsKilled;
    private int baronsKilled;
    private int enemyTowersDestroyed;
    private int enemyDragonsKilled;
    private int enemyBaronsKilled;

    public GameStats() {
        this.heroStatsMap = new HashMap<>();
        this.teamStatsMap = new HashMap<>();
    }

    public void addHeroStats(int heroId, HeroStats stats) {
        heroStatsMap.put(heroId, stats);
    }

    public HeroStats getHeroStats(int heroId) {
        return heroStatsMap.get(heroId);
    }

    public Map<Integer, HeroStats> getAllHeroStats() {
        return heroStatsMap;
    }

    public void setTeamStats(int teamId, TeamStats stats) {
        teamStatsMap.put(teamId, stats);
    }

    public TeamStats getTeamStats(int teamId) {
        return teamStatsMap.get(teamId);
    }

    public Map<Integer, TeamStats> getTeamStatsMap() {
        return teamStatsMap;
    }

    public String getGameResult() { return gameResult; }
    public void setGameResult(String gameResult) { this.gameResult = gameResult; }

    public long getGameDuration() { return gameDuration; }
    public void setGameDuration(long gameDuration) { this.gameDuration = gameDuration; }

    public int getPlayerTeamId() { return playerTeamId; }
    public void setPlayerTeamId(int playerTeamId) { this.playerTeamId = playerTeamId; }

    public int getTowersDestroyed() { return towersDestroyed; }
    public void setTowersDestroyed(int towersDestroyed) { this.towersDestroyed = towersDestroyed; }

    public int getTowersLost() { return towersLost; }
    public void setTowersLost(int towersLost) { this.towersLost = towersLost; }

    public int getDragonsKilled() { return dragonsKilled; }
    public void setDragonsKilled(int dragonsKilled) { this.dragonsKilled = dragonsKilled; }

    public int getBaronsKilled() { return baronsKilled; }
    public void setBaronKilled(int baronsKilled) { this.baronsKilled = baronsKilled; }

    public int getEnemyTowersDestroyed() { return enemyTowersDestroyed; }
    public void setEnemyTowersDestroyed(int enemyTowersDestroyed) { this.enemyTowersDestroyed = enemyTowersDestroyed; }

    public int getEnemyDragonsKilled() { return enemyDragonsKilled; }
    public void setEnemyDragonsKilled(int enemyDragonsKilled) { this.enemyDragonsKilled = enemyDragonsKilled; }

    public int getEnemyBaronsKilled() { return enemyBaronsKilled; }
    public void setEnemyBaronsKilled(int enemyBaronsKilled) { this.enemyBaronsKilled = enemyBaronsKilled; }

    public int getTotalKills() {
        int total = 0;
        for (TeamStats t : teamStatsMap.values()) {
            total += t.getTotalKills();
        }
        return total;
    }

    public int getTotalDeaths() {
        int total = 0;
        for (TeamStats t : teamStatsMap.values()) {
            total += t.getTotalDeaths();
        }
        return total;
    }

    public int getTotalAssists() {
        int total = 0;
        for (TeamStats t : teamStatsMap.values()) {
            total += t.getTotalAssists();
        }
        return total;
    }
}