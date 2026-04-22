package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import data.model.HeroStats;

public class TestHeroStats {

    private HeroStats stats;

    @Before
    public void prepare() {
        stats = new HeroStats(1, "TestHero", 0);
    }

    @Test
    public void testHeroIdCorrect() {
        assertEquals(1, stats.getHeroId());
    }

    @Test
    public void testHeroNameCorrect() {
        assertEquals("TestHero", stats.getHeroName());
    }

    @Test
    public void testTeamIdCorrect() {
        assertEquals(0, stats.getTeamId());
    }

    @Test
    public void testKillAjouteKDA() {
        stats.addKill();
        assertEquals(1, stats.getKills());
    }

    @Test
    public void testDeathAjouteKDA() {
        stats.addDeath();
        assertEquals(1, stats.getDeaths());
    }

    @Test
    public void testAssistAjouteKDA() {
        stats.addAssist();
        assertEquals(1, stats.getAssists());
    }

    @Test
    public void testKDASansMorts() {
        stats.addKill();
        stats.addAssist();
        stats.addAssist();
        assertEquals(3.0, stats.getKDA(), 0.001);
    }

    @Test
    public void testKDAAvecMorts() {
        stats.addKill();
        stats.addKill();
        stats.addAssist();
        stats.addDeath();
        assertEquals(1.5, stats.getKDA(), 0.001);
    }

    @Test
    public void testSetHeroId() {
        stats.setHeroId(5);
        assertEquals(5, stats.getHeroId());
    }

    @Test
    public void testSetHeroName() {
        stats.setHeroName("NewName");
        assertEquals("NewName", stats.getHeroName());
    }

    @Test
    public void testSetTeamId() {
        stats.setTeamId(1);
        assertEquals(1, stats.getTeamId());
    }

    @Test
    public void testSetLevel() {
        stats.setLevel(5);
        assertEquals(5, stats.getLevel());
    }

    @Test
    public void testAddDamageToHeroes() {
        stats.addDamageToHeroes(500);
        assertEquals(500, stats.getDamageDealtToHeroes());
    }

    @Test
    public void testAddDamageToBuildings() {
        stats.addDamageToBuildings(1000);
        assertEquals(1000, stats.getDamageDealtToBuildings());
    }

    @Test
    public void testAddDamageTaken() {
        stats.addDamageTaken(250);
        assertEquals(250, stats.getDamageTaken());
    }

    @Test
    public void testAddHealingDone() {
        stats.addHealingDone(100);
        assertEquals(100, stats.getHealingDone());
    }

    @Test
    public void testAddGoldEarned() {
        stats.addGoldEarned(1000);
        assertEquals(1000, stats.getGoldEarned());
    }

    @Test
    public void testSetGoldSpent() {
        stats.setGoldSpent(500);
        assertEquals(500, stats.getGoldSpent());
    }

    @Test
    public void testSetCsCreeps() {
        stats.setCsCreeps(50);
        assertEquals(50, stats.getCsCreeps());
    }

    @Test
    public void testSetTimePlayed() {
        stats.setTimePlayed(60000);
        assertEquals(60000, stats.getTimePlayed());
    }

    @Test
    public void testSetTimeSpentDead() {
        stats.setTimeSpentDead(10000);
        assertEquals(10000, stats.getTimeSpentDead());
    }

    @Test
    public void testSetLongestKillStreak() {
        stats.setLongestKillStreak(10);
        assertEquals(10, stats.getLongestKillStreak());
    }

    @Test
    public void testSetLongestDeathStreak() {
        stats.setLongestDeathStreak(5);
        assertEquals(5, stats.getLongestDeathStreak());
    }

    @Test
    public void testSetMultiKills() {
        stats.setMultiKills(3);
        assertEquals(3, stats.getMultiKills());
    }

    @Test
    public void testSetPlayer() {
        stats.setPlayer(true);
        assertTrue(stats.isPlayer());
        stats.setPlayer(false);
        assertFalse(stats.isPlayer());
    }
}