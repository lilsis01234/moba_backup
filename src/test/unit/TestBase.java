package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import engine.mobile.Base;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;

public class TestBase {

    private Base allyBase;
    private Base enemyBase;

    @Before
    public void prepare() {
        allyBase  = new Base(100, 100, 0);
        enemyBase = new Base(500, 500, 1);
    }

    @Test
    public void testPositionXInitiale() {
        assertEquals(100.0, allyBase.getX(), 0.001);
    }

    @Test
    public void testPositionYInitiale() {
        assertEquals(100.0, allyBase.getY(), 0.001);
    }

    @Test
    public void testAllyTeamVautZero() {
        assertEquals(0, allyBase.getTeam());
    }

    @Test
    public void testEnemyTeamVautUn() {
        assertEquals(1, enemyBase.getTeam());
    }

    @Test
    public void testHpInitialEgalMaxHp() {
        assertEquals(allyBase.getMaxHp(), allyBase.getHp(), 0.001);
    }

    @Test
    public void testMaxHpCorrespondConfig() {
        assertEquals(GameConfiguration.BASE_MAX_HP, allyBase.getMaxHp(), 0.001);
    }

    @Test
    public void testActiveParDefaut() {
        assertTrue(allyBase.isActive());
    }

    @Test
    public void testTakeDamageReduitHp() {
        double hpAvant = allyBase.getHp();
        allyBase.takeDamage(50);
        assertEquals(hpAvant - 50, allyBase.getHp(), 0.01);
    }

    @Test
    public void testBaseMeurtQuandHpZero() {
        allyBase.takeDamage(GameConfiguration.BASE_MAX_HP);
        assertFalse(allyBase.isActive());
    }

    @Test
    public void testHealRestaurerHp() {
        allyBase.takeDamage(100);
        double hpAvant = allyBase.getHp();
        allyBase.heal(50);
        assertEquals(hpAvant + 50, allyBase.getHp(), 0.01);
    }

    @Test
    public void testHealNeDepassePasMaxHp() {
        allyBase.heal(999999);
        assertEquals(allyBase.getMaxHp(), allyBase.getHp(), 0.001);
    }

    @Test
    public void testBaseInactifNeBougePas() {
        allyBase.takeDamage(999999);
        double xAvant = allyBase.getX();
        double yAvant = allyBase.getY();
        allyBase.update(0.5, new ArrayList<>());
        assertEquals(xAvant, allyBase.getX(), 0.001);
        assertEquals(yAvant, allyBase.getY(), 0.001);
    }

    @Test
    public void testBaseAttaqueEnnemiAPortee() {
        List<Entity> enemies = new ArrayList<>();
        enemies.add(enemyBase);

        double hpAvant = enemyBase.getHp();
        allyBase.update(2.1, enemies);

        assertTrue(enemyBase.getHp() < hpAvant);
    }

    @Test
    public void testBaseNAttaquePasAllie() {
        List<Entity> allies = new ArrayList<>();
        allies.add(allyBase);

        double hpAvant = enemyBase.getHp();
        enemyBase.update(2.1, allies);

        assertEquals(hpAvant, enemyBase.getHp(), 0.01);
    }
}