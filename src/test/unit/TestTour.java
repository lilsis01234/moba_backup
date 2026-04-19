package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import engine.mobile.Tower;
import engine.mobile.Entity;
import game_config.GameConfiguration;

import java.util.ArrayList;

public class TestTour {

    private Tower allyTower;
    private Tower enemyTower;

    @Before
    public void prepare() {
        allyTower  = new Tower(100, 200, 0);
        enemyTower = new Tower(500, 300, 1);
    }

    @Test
    public void testPositionXAlly() {
        assertEquals(100.0, allyTower.getX(), 0.001);
    }

    @Test
    public void testPositionYAlly() {
        assertEquals(200.0, allyTower.getY(), 0.001);
    }

    @Test
    public void testPositionXEnemy() {
        assertEquals(500.0, enemyTower.getX(), 0.001);
    }

    @Test
    public void testPositionYEnemy() {
        assertEquals(300.0, enemyTower.getY(), 0.001);
    }

    @Test
    public void testAllyIsAlly() {
        assertTrue(allyTower.isAlly());
    }

    @Test
    public void testAllyIsNotEnemy() {
        assertFalse(allyTower.isEnemy());
    }

    @Test
    public void testEnemyIsEnemy() {
        assertTrue(enemyTower.isEnemy());
    }

    @Test
    public void testEnemyIsNotAlly() {
        assertFalse(enemyTower.isAlly());
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
    public void testAtkDamageCorrespondConfig() {
        assertEquals(GameConfiguration.TOWER_DAMAGE, allyTower.getAtkDamage(), 0.001);
    }

    @Test
    public void testActiveParDefaut() {
        assertTrue(allyTower.isActive());
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
    public void testTakeDamageReduitHp() {
        double hpAvant = allyTower.getHp();
        allyTower.takeDamage(100);
        assertTrue(allyTower.getHp() < hpAvant);
    }

    @Test
    public void testTakeDamageMontantCorrect() {
        double hpAvant = allyTower.getHp();
        allyTower.takeDamage(100);
        assertEquals(hpAvant - 100, allyTower.getHp(), 0.01);
    }

    @Test
    public void testTourMeurtQuandHpZero() {
        allyTower.takeDamage(GameConfiguration.TOWER_MAX_HP);
        assertFalse(allyTower.isActive());
    }

    @Test
    public void testHpNeTombePasEnNegatif() {
        allyTower.takeDamage(999999);
        assertTrue(allyTower.getHp() >= 0);
    }

    @Test
    public void testTourInactiveApresDestructionTotale() {
        allyTower.takeDamage(999999);
        assertFalse(allyTower.isActive());
    }

    @Test
    public void testHealRestaurerHp() {
        allyTower.takeDamage(200);
        double hpAvant = allyTower.getHp();
        allyTower.heal(100);
        assertEquals(hpAvant + 100, allyTower.getHp(), 0.01);
    }

    @Test
    public void testHealNeDepassePasMaxHp() {
        allyTower.heal(999999);
        assertEquals(allyTower.getMaxHp(), allyTower.getHp(), 0.001);
    }

    @Test
    public void testHealSurTourPleineSansEffet() {
        double hpAvant = allyTower.getHp();
        allyTower.heal(100);
        assertEquals(hpAvant, allyTower.getHp(), 0.001);
    }

    @Test
    public void testDistanceEntreDeuxTours() {
        Tower a = new Tower(0,   0, 0);
        Tower b = new Tower(300, 400, 1);
        assertEquals(500.0, a.getDistanceTo(b), 0.001);
    }

    @Test
    public void testDistanceMemePosition() {
        Tower a = new Tower(100, 200, 0);
        Tower b = new Tower(100, 200, 1);
        assertEquals(0.0, a.getDistanceTo(b), 0.001);
    }

    @Test
    public void testDistanceSymetrique() {
        Tower a = new Tower(0,   0, 0);
        Tower b = new Tower(300, 400, 1);
        assertEquals(a.getDistanceTo(b), b.getDistanceTo(a), 0.001);
    }

    @Test
    public void testUpdateSansEnnemisNeCrashePas() {
        ArrayList<Entity> vide = new ArrayList<>();
        allyTower.update(0.016, vide);
        assertTrue(allyTower.isActive());
    }

    @Test
    public void testUpdateTourMorteNeFaitRien() {
        allyTower.takeDamage(999999);
        ArrayList<Entity> ennemis = new ArrayList<>();
        allyTower.update(0.016, ennemis);
        assertFalse(allyTower.isActive());
    }

    @Test
    public void testUpdateAttaqueEnnemiAPortee() {
        // Ennemi placé à la moitié de la portée de la tour
        Tower cible = new Tower(
            allyTower.getX(),
            allyTower.getY() + GameConfiguration.TOWER_RANGE / 2.0,
            1
        );
        ArrayList<Entity> ennemis = new ArrayList<>();
        ennemis.add(cible);

        double hpAvant = cible.getHp();
        // deltaTime > cooldown (1.5s) pour déclencher l'attaque
        allyTower.update(2.0, ennemis);

        assertTrue(cible.getHp() < hpAvant);
    }

    @Test
    public void testUpdateNAttaquePasHorsPortee() {
        Tower cible = new Tower(
            allyTower.getX(),
            allyTower.getY() + GameConfiguration.TOWER_RANGE * 10,
            1
        );
        ArrayList<Entity> ennemis = new ArrayList<>();
        ennemis.add(cible);

        double hpAvant = cible.getHp();
        allyTower.update(2.0, ennemis);

        assertEquals(hpAvant, cible.getHp(), 0.01);
    }

    @Test
    public void testUpdateNAttaquePasSaMemeEquipe() {
        // Tour alliée ne doit pas attaquer une autre tour alliée
        Tower allie2 = new Tower(
            allyTower.getX(),
            allyTower.getY() + GameConfiguration.TOWER_RANGE / 2.0,
            0  // même équipe
        );
        ArrayList<Entity> cibles = new ArrayList<>();
        cibles.add(allie2);

        double hpAvant = allie2.getHp();
        allyTower.update(2.0, cibles);

        assertEquals(hpAvant, allie2.getHp(), 0.01);
    }

    @Test
    public void testUpdateNAttaquePasCibleMorte() {
        Tower cible = new Tower(
            allyTower.getX(),
            allyTower.getY() + GameConfiguration.TOWER_RANGE / 2.0,
            1
        );
        cible.takeDamage(999999); // cible déjà morte
        ArrayList<Entity> ennemis = new ArrayList<>();
        ennemis.add(cible);

        double hpApresMort = cible.getHp();
        allyTower.update(2.0, ennemis);

        assertEquals(hpApresMort, cible.getHp(), 0.01);
    }
}