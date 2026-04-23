package data.model;

public class HeroStats {
    private int heroId;
    private String heroName;
    private int teamId;
    private int level;
    private KDA kda;
    private int damageDealtToHeroes;
    private int damageDealtToBuildings;
    private int damageTaken;
    private int healingDone;
    private int goldEarned;
    private int goldSpent;
    private int csCreeps;
    private long timePlayed;
    private long timeSpentDead;
    private int longestKillStreak;
    private int longestDeathStreak;
    private int multiKills;
    private boolean isPlayer;

    public HeroStats() {
        this.kda = new KDA();
    }

    public HeroStats(int heroId, String heroName, int teamId) {
        this.heroId = heroId;
        this.heroName = heroName;
        this.teamId = teamId;
        this.kda = new KDA();
    }

    public void addKill() { kda.addKill(); }
    public void addDeath() { kda.addDeath(); }
    public void addAssist() { kda.addAssist(); }

    public int getKills() { return kda.getKills(); }
    public int getDeaths() { return kda.getDeaths(); }
    public int getAssists() { return kda.getAssists(); }

    public double getKDA() {
        int deaths = kda.getDeaths();
        if (deaths == 0) return kda.getKills() + kda.getAssists();
        return (double)(kda.getKills() + kda.getAssists()) / deaths;
    }

    public int getMVPScore() {
        return kda.getKills() * 100 + kda.getAssists() * 50 + (damageDealtToHeroes + damageDealtToBuildings) / 10;
    }

    public boolean isMVP() {
        return isPlayer;
    }

    public int getHeroId() { return heroId; }
    public void setHeroId(int heroId) { this.heroId = heroId; }

    public String getHeroName() { return heroName; }
    public void setHeroName(String heroName) { this.heroName = heroName; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getDamageDealtToHeroes() { return damageDealtToHeroes; }
    public void addDamageToHeroes(int dmg) { this.damageDealtToHeroes += dmg; }

    public int getDamageDealtToBuildings() { return damageDealtToBuildings; }
    public void addDamageToBuildings(int dmg) { this.damageDealtToBuildings += dmg; }

    public int getDamageTaken() { return damageTaken; }
    public void addDamageTaken(int dmg) { this.damageTaken += dmg; }

    public int getHealingDone() { return healingDone; }
    public void addHealingDone(int heal) { this.healingDone += heal; }

    public int getGoldEarned() { return goldEarned; }
    public void setGoldEarned(int goldEarned) { this.goldEarned = goldEarned; }
    public void addGoldEarned(int gold) { this.goldEarned += gold; }

    public int getGoldSpent() { return goldSpent; }
    public void setGoldSpent(int goldSpent) { this.goldSpent = goldSpent; }

    public int getCsCreeps() { return csCreeps; }
    public void setCsCreeps(int csCreeps) { this.csCreeps = csCreeps; }

    public long getTimePlayed() { return timePlayed; }
    public void setTimePlayed(long timePlayed) { this.timePlayed = timePlayed; }

    public long getTimeSpentDead() { return timeSpentDead; }
    public void setTimeSpentDead(long timeSpentDead) { this.timeSpentDead = timeSpentDead; }

    public int getLongestKillStreak() { return longestKillStreak; }
    public void setLongestKillStreak(int streak) { this.longestKillStreak = streak; }

    public int getLongestDeathStreak() { return longestDeathStreak; }
    public void setLongestDeathStreak(int streak) { this.longestDeathStreak = streak; }

    public int getMultiKills() { return multiKills; }
    public void setMultiKills(int multiKills) { this.multiKills = multiKills; }

    public boolean isPlayer() { return isPlayer; }
    public void setPlayer(boolean isPlayer) { this.isPlayer = isPlayer; }

    public int getGoldPerMinute() {
        if (timePlayed == 0) return 0;
        return (int)((goldEarned * 60_000.0) / timePlayed);
    }

    public String getNetWorth() {
        return util.FormatUtils.formatNumber(goldEarned - goldSpent);
    }

    public String formatNumber(int num) {
        return util.FormatUtils.formatNumber(num);
    }
}