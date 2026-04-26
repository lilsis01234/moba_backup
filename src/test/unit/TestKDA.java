package test.unit;

import static org.junit.Assert.assertEquals;


import org.junit.Before;
import org.junit.Test;

import data.model.KDA;

public class TestKDA {

    private KDA kda;

    @Before
    public void prepare() {
        kda = new KDA();
    }

    @Test
    public void testKillIncrementeCompteur() {
        kda.addKill();
        assertEquals(1, kda.getKills());
    }

    @Test
    public void testDeathIncrementeCompteur() {
        kda.addDeath();
        assertEquals(1, kda.getDeaths());
    }

    @Test
    public void testAssistIncrementeCompteur() {
        kda.addAssist();
        assertEquals(1, kda.getAssists());
    }

    @Test
    public void testMultipleKills() {
        kda.addKill();
        kda.addKill();
        kda.addKill();
        assertEquals(3, kda.getKills());
    }

    @Test
    public void testMultipleDeaths() {
        kda.addDeath();
        kda.addDeath();
        assertEquals(2, kda.getDeaths());
    }

    @Test
    public void testMultipleAssists() {
        kda.addAssist();
        kda.addAssist();
        kda.addAssist();
        kda.addAssist();
        assertEquals(4, kda.getAssists());
    }

    @Test
    public void testValeursInitialesZero() {
        assertEquals(0, kda.getKills());
        assertEquals(0, kda.getDeaths());
        assertEquals(0, kda.getAssists());
    }

    @Test
    public void testKDAMixte() {
        kda.addKill();
        kda.addKill();
        kda.addDeath();
        kda.addAssist();
        kda.addAssist();
        assertEquals(2, kda.getKills());
        assertEquals(1, kda.getDeaths());
        assertEquals(2, kda.getAssists());
    }
}