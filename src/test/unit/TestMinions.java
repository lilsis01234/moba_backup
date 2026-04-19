package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import engine.mobile.Minion;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestMinions {

    private List<double[]> waypoints;
    private Minion allyMinion;
    private Minion enemyMinion;

    @Before
    public void prepare() {
        waypoints = new ArrayList<>();
        waypoints.add(new double[]{200, 200});
        waypoints.add(new double[]{400, 200});
        waypoints.add(new double[]{400, 400});

        allyMinion  = new Minion(100, 100, 0, waypoints);
        enemyMinion = new Minion(500, 500, 1, waypoints);
    }

    @Test
    public void testPositionXInitiale() {
        assertEquals(100.0, allyMinion.getX(), 0.001);
    }

    @Test
    public void testPositionYInitiale() {
        assertEquals(100.0, allyMinion.getY(), 0.001);
    }

    @Test
    public void testAllyTeamVautZero() {
        assertEquals(0, allyMinion.getTeam());
    }

    @Test
    public void testEnemyTeamVautUn() {
        assertEquals(1, enemyMinion.getTeam());
    }

    @Test
    public void testHpInitialEgalMaxHp() {
        assertEquals(allyMinion.getMaxHp(), allyMinion.getHp(), 0.001);
    }

    @Test
    public void testMaxHpCorrespondConfig() {
        assertEquals(GameConfiguration.MINION_MAX_HP, allyMinion.getMaxHp(), 0.001);
    }

    @Test
    public void testAtkDamageCorrespondConfig() {
        assertEquals(GameConfiguration.MINION_DMG, allyMinion.getAtkDamage(), 0.001);
    }

    @Test
    public void testLootOrCorrespondConfig() {
        assertEquals(GameConfiguration.GOLD_MINION, allyMinion.getLoot());
    }

    @Test
    public void testLootXpCorrespondConfig() {
        assertEquals(GameConfiguration.XP_MINION, allyMinion.getXPLoot());
    }

    @Test
    public void testActiveParDefaut() {
        assertTrue(allyMinion.isActive());
    }

    @Test
    public void testTakeDamageReduitHp() {
        double hpAvant = allyMinion.getHp();
        allyMinion.takeDamage(50);
        assertEquals(hpAvant - 50, allyMinion.getHp(), 0.01);
    }

    @Test
    public void testMinionMeurtQuandHpZero() {
        allyMinion.takeDamage(GameConfiguration.MINION_MAX_HP);
        assertFalse(allyMinion.isActive());
    }

    @Test
    public void testMinionInactifApresDestructionTotale() {
        allyMinion.takeDamage(999999);
        assertFalse(allyMinion.isActive());
    }

    @Test
    public void testHealRestaurerHp() {
        allyMinion.takeDamage(100);
        double hpAvant = allyMinion.getHp();
        allyMinion.heal(50);
        assertEquals(hpAvant + 50, allyMinion.getHp(), 0.01);
    }

    @Test
    public void testHealNeDepassePasMaxHp() {
        allyMinion.heal(999999);
        assertEquals(allyMinion.getMaxHp(), allyMinion.getHp(), 0.001);
    }

    @Test
    public void testMinionSeDeplaceSuivantWaypoint() {
        double xAvant = allyMinion.getX();
        double yAvant = allyMinion.getY();
        // Liste fraîche pointant vers (200,200), le minion est en (100,100)
        allyMinion.update(0.1, new ArrayList<>());
        assertFalse(
            allyMinion.getX() == xAvant && allyMinion.getY() == yAvant
        );
    }

    @Test
    public void testMinionSeRapprocheDeWaypoint() {
        double[] wp = waypoints.get(0);
        double distAvant = distance(allyMinion.getX(), allyMinion.getY(), wp[0], wp[1]);
        allyMinion.update(0.1, new ArrayList<>());
        double distApres = distance(allyMinion.getX(), allyMinion.getY(), wp[0], wp[1]);
        assertTrue(distApres < distAvant);
    }

    @Test
    public void testMinionSArreteSansWaypoints() {
        Minion sansWp = new Minion(100, 100, 0, new ArrayList<>());
        double xAvant = sansWp.getX();
        double yAvant = sansWp.getY();
        sansWp.update(0.1, new ArrayList<>());
        assertEquals(xAvant, sansWp.getX(), 0.001);
        assertEquals(yAvant, sansWp.getY(), 0.001);
    }

    @Test
    public void testMinionMortNeBougePas() {
        allyMinion.takeDamage(999999);
        double xAvant = allyMinion.getX();
        double yAvant = allyMinion.getY();
        allyMinion.update(0.5, new ArrayList<>());
        assertEquals(xAvant, allyMinion.getX(), 0.001);
        assertEquals(yAvant, allyMinion.getY(), 0.001);
    }

    @Test
    public void testAttaqueEnnemiAPortee() {
        // Minion ennemi placé juste dans la portée
        Minion cible = new Minion(
            allyMinion.getX(),
            allyMinion.getY() + GameConfiguration.MINION_RANGE / 2.0,
            1, waypoints
        );
        List<Entity> ennemis = new ArrayList<>();
        ennemis.add(cible);

        double hpAvant = cible.getHp();
        // deltaTime > cooldown pour déclencher l'attaque
        allyMinion.update(GameConfiguration.MINION_ATTACK_COOLDOWN + 0.1, ennemis);

        assertTrue(cible.getHp() < hpAvant);
    }

    @Test
    public void testNAttaquePasHorsPortee() {
        Minion cible = new Minion(
            allyMinion.getX(),
            allyMinion.getY() + GameConfiguration.MINION_RANGE * 10,
            1, waypoints
        );
        List<Entity> ennemis = new ArrayList<>();
        ennemis.add(cible);

        double hpAvant = cible.getHp();
        allyMinion.update(GameConfiguration.MINION_ATTACK_COOLDOWN + 0.1, ennemis);

        assertEquals(hpAvant, cible.getHp(), 0.01);
    }

    @Test
    public void testNAttaquePasCibleMorte() {
        Minion cible = new Minion(
            allyMinion.getX(),
            allyMinion.getY() + GameConfiguration.MINION_RANGE / 2.0,
            1, waypoints
        );
        cible.takeDamage(999999);
        List<Entity> ennemis = new ArrayList<>();
        ennemis.add(cible);

        double hpApresMort = cible.getHp();
        allyMinion.update(GameConfiguration.MINION_ATTACK_COOLDOWN + 0.1, ennemis);

        assertEquals(hpApresMort, cible.getHp(), 0.01);
    }

    @Test
    public void testAttaquePrioriteCibleLaPlusProche() {
        // Deux ennemis : un proche, un loin
        Minion proche = new Minion(
            allyMinion.getX(),
            allyMinion.getY() + GameConfiguration.MINION_RANGE / 2.0,
            1, waypoints
        );
        Minion loin = new Minion(
            allyMinion.getX(),
            allyMinion.getY() + GameConfiguration.MINION_RANGE / 2.0 + 200,
            1, waypoints
        );
        List<Entity> ennemis = new ArrayList<>();
        ennemis.add(loin);
        ennemis.add(proche);

        double hpProcheAvant = proche.getHp();
        double hpLoinAvant   = loin.getHp();

        allyMinion.update(GameConfiguration.MINION_ATTACK_COOLDOWN + 0.1, ennemis);

        assertTrue(proche.getHp() < hpProcheAvant);
        assertEquals(hpLoinAvant, loin.getHp(), 0.01);
    }

    @Test
    public void testTropProcheVrai() {
        Minion voisin = new Minion(
            allyMinion.getX() + 10,
            allyMinion.getY(),
            0, waypoints
        );
        assertTrue(allyMinion.isTooCloseTo(voisin));
    }

    @Test
    public void testTropProcheFaux() {
        Minion loin = new Minion(
            allyMinion.getX() + 100,
            allyMinion.getY(),
            0, waypoints
        );
        assertFalse(allyMinion.isTooCloseTo(loin));
    }

    @Test
    public void testTropProcheSymetrique() {
        Minion voisin = new Minion(
            allyMinion.getX() + 10,
            allyMinion.getY(),
            0, waypoints
        );
        assertEquals(allyMinion.isTooCloseTo(voisin), voisin.isTooCloseTo(allyMinion));
    }

    @Test
    public void testDistancePythagore() {
        Minion a = new Minion(0,   0, 0, waypoints);
        Minion b = new Minion(300, 400, 1, waypoints);
        assertEquals(500.0, a.getDistanceTo(b), 0.001);
    }

    @Test
    public void testDistanceMemePosition() {
        Minion a = new Minion(100, 100, 0, waypoints);
        Minion b = new Minion(100, 100, 1, waypoints);
        assertEquals(0.0, a.getDistanceTo(b), 0.001);
    }

    private double distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }
}