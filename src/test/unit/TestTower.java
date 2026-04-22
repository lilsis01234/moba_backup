package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import engine.mobile.Tower;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;

public class TestTower {

    private Tower allyTower;
    private Tower enemyTower;

    @Before
    public void prepare() {
        allyTower  = new Tower(100, 100, 0);
        enemyTower = new Tower(500, 500, 1);
    }

    @Test
    public void testPositionXInitiale() {
        assertEquals(100.0, allyTower.getX(), 0.001);
    }

    @Test
    public void testPositionYInitiale() {
        assertEquals(100.0, allyTower.getY(), 0.001);
    }

    @Test
    public void testAllyTeamVautZero() {
        assertEquals(0, allyTower.getTeam());
    }

    @Test
    public void testEnemyTeamVautUn() {
        assertEquals(1, enemyTower.getTeam());
    }

    @Test
    public void testHpInitialEgalMaxHp() {
        assertEquals(allyTower.getMaxHp(), allyTower.getHp(), 0.001);
    }

    @Test
    public void testMaxHpCorrespondConfig() {
        assertEquals(GameConfiguration.TOWER_MAX_HP, allyTower.getMaxHp(), 0.001);
    }

    @Test
    public void testLootOrCorrespondConfig() {
        assertEquals(GameConfiguration.GOLD_TOWER, allyTower.getLoot());
    }

    @Test
    public void testLootXpCorrespondConfig() {
        assertEquals(GameConfiguration.XP_TOWER, allyTower.getXPLoot());
    }

    @Test
    public void testActiveParDefaut() {
        assertTrue(allyTower.isActive());
    }

    @Test
    public void testIsAllyVrai() {
        assertTrue(allyTower.isAlly());
    }

    @Test
    public void testIsAllyFaux() {
        assertFalse(enemyTower.isAlly());
    }

    @Test
    public void testIsEnemyVrai() {
        assertTrue(enemyTower.isEnemy());
    }

    @Test
    public void testIsEnemyFaux() {
        assertFalse(allyTower.isEnemy());
    }

    @Test
    public void testTakeDamageReduitHp() {
        double hpAvant = allyTower.getHp();
        allyTower.takeDamage(50);
        assertEquals(hpAvant - 50, allyTower.getHp(), 0.01);
    }

    @Test
    public void testTowerMeurtQuandHpZero() {
        allyTower.takeDamage(GameConfiguration.TOWER_MAX_HP);
        assertFalse(allyTower.isActive());
    }

    @Test
    public void testHealRestaurerHp() {
        allyTower.takeDamage(100);
        double hpAvant = allyTower.getHp();
        allyTower.heal(50);
        assertEquals(hpAvant + 50, allyTower.getHp(), 0.01);
    }

    @Test
    public void testHealNeDepassePasMaxHp() {
        allyTower.heal(999999);
        assertEquals(allyTower.getMaxHp(), allyTower.getHp(), 0.001);
    }

    @Test
    public void testTowerInactifNeBougePas() {
        allyTower.takeDamage(999999);
        double xAvant = allyTower.getX();
        double yAvant = allyTower.getY();
        allyTower.update(0.5, new ArrayList<>());
        assertEquals(xAvant, allyTower.getX(), 0.001);
        assertEquals(yAvant, allyTower.getY(), 0.001);
    }

    @Test
    public void testAttaqueEnnemiAPortee() {
        Entity cible = enemyTower;
        List<Entity> ennemis = new ArrayList<>();
        ennemis.add(cible);

        double hpAvant = cible.getHp();
        allyTower.update(1.6, (ArrayList<Entity>) ennemis);

        assertTrue(cible.getHp() < hpAvant);
    }

    @Test
    public void testNAttaquePasAllie() {
        List<Entity> allies = new ArrayList<>();
        allies.add(allyTower);

        double hpAvant = enemyTower.getHp();
        enemyTower.update(1.6, (ArrayList<Entity>) allies);

        assertEquals(hpAvant, enemyTower.getHp(), 0.01);
    }
}