package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import data.model.Hero;
import engine.mobile.Bot;

import java.util.ArrayList;
import java.util.List;

public class TestBot {

    private List<double[]> waypoints;
    private Hero testHero;

    @Before
    public void prepare() {
        waypoints = new ArrayList<>();
        waypoints.add(new double[]{200, 200});
        waypoints.add(new double[]{400, 200});
        waypoints.add(new double[]{400, 400});

        testHero = new Hero();
        testHero.setName("TestHero");
        testHero.setMaxHp(500);
        testHero.setAttack(50);
        testHero.setDefense(0);
        testHero.setAttackSpeed(1.0);
        testHero.setMaxMana(200);
        testHero.setSpeed(300);
        testHero.setAtkRange(200);
    }

    @Test
    public void testBotCreationAlly() {
        Bot bot = new Bot(waypoints, 0, "Bot1", testHero);
        assertEquals("Bot1", bot.getName());
    }

    @Test
    public void testBotCreationEnemy() {
        Bot bot = new Bot(waypoints, 1, "EnemyBot", testHero);
        assertEquals("EnemyBot", bot.getName());
    }

    @Test
    public void testBotHeroName() {
        Bot bot = new Bot(waypoints, 0, "Bot1", testHero);
        assertEquals("TestHero", bot.getHeroName());
    }

    @Test
    public void testBotWaypointsNonNull() {
        Bot bot = new Bot(waypoints, 0, "Bot1", testHero);
        assertTrue(bot.getRespawnTimer() >= 0);
    }

    @Test
    public void testGetHeroIdRenvoieZero() {
        Bot bot = new Bot(waypoints, 0, "Bot1", testHero);
        assertEquals(0, bot.getHeroId());
    }

    @Test
    public void testBotWaypointsNotEmpty() {
        assertFalse(waypoints.isEmpty());
    }

    @Test
    public void testBotHasWaypoints() {
        assertTrue(waypoints.size() > 0);
    }

    @Test
    public void testWaypointsCoordinates() {
        assertEquals(200.0, waypoints.get(0)[0], 0.001);
        assertEquals(200.0, waypoints.get(0)[1], 0.001);
    }
}