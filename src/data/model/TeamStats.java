package data.model;

import java.util.ArrayList;
import java.util.List;

public class TeamStats {
    private int teamId;
    private List<HeroStats> heroes;
    private int totalKills;
    private int totalDeaths;
    private int totalAssists;
    private int towersDestroyed;
    private int goldEarned;
    private int totalDamageDealt;

    public TeamStats() {
        this.heroes = new ArrayList<>();
    }

    public TeamStats(int teamId) {
        this.teamId = teamId;
        this.heroes = new ArrayList<>();
    }

    public void addHero(HeroStats hero) {
        heroes.add(hero);
    }

    public List<HeroStats> getHeroes() {
        return heroes;
    }

    public void calculateTotals() {
        totalKills = 0;
        totalDeaths = 0;
        totalAssists = 0;
        totalDamageDealt = 0;
        goldEarned = 0;

        for (HeroStats hero : heroes) {
            totalKills += hero.getKills();
            totalDeaths += hero.getDeaths();
            totalAssists += hero.getAssists();
            totalDamageDealt += hero.getDamageDealtToHeroes() + hero.getDamageDealtToBuildings();
            goldEarned += hero.getGoldEarned();
        }
    }

    public double getTeamKDA() {
        if (totalDeaths == 0) return totalKills + totalAssists;
        return (double)(totalKills + totalAssists) / totalDeaths;
    }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public int getTotalKills() { return totalKills; }
    public int getTotalDeaths() { return totalDeaths; }
    public int getTotalAssists() { return totalAssists; }

    public int getTowersDestroyed() { return towersDestroyed; }
    public void setTowersDestroyed(int towersDestroyed) { this.towersDestroyed = towersDestroyed; }

    public int getGoldEarned() { return goldEarned; }
    public void setGoldEarned(int goldEarned) { this.goldEarned = goldEarned; }

    public int getTotalDamageDealt() { return totalDamageDealt; }
    public void setTotalDamageDealt(int totalDamageDealt) { this.totalDamageDealt = totalDamageDealt; }
}