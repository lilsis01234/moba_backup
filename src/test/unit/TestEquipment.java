package test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import data.model.Equipment;
import data.model.EquipmentType;
import data.model.Hero;
import engine.mobile.Player;

public class TestEquipment {

    private Player player;
    private Equipment sword1;
    private Equipment sword2;
    private Equipment fusedSword;

    @Before
    public void prepare() {
        Hero hero = new Hero();
        hero.setName("TestHero");
        hero.setMaxHp(500);
        hero.setAttack(50);
        hero.setDefense(0);
        hero.setAttackSpeed(1.0);
        hero.setMaxMana(200);
        hero.setSpeed(300);
        hero.setAtkRange(200);
        hero.setSpriteFile("");

        player = new Player(hero);
        player.addGold(9999);

        sword1     = new Equipment(1,   "Epee de Fer",
            EquipmentType.SWORD, 15, 0, 350, "Test");
        sword2     = new Equipment(2,   "Epee d Acier",
            EquipmentType.SWORD, 25, 0, 600, "Test");
        fusedSword = new Equipment(101, "Lame du Guerrier",
            EquipmentType.SWORD, 65, 0, "Test fusion", 1, 2);
    }

    @Test
    public void testAchatDeduitOr() {
        int goldAvant = player.getGold();
        player.buyEquipment(sword1);
        assertEquals(goldAvant - sword1.getPrice(), player.getGold());
    }

    @Test
    public void testAchatAppliqueBonus() {
        double atkAvant = player.getAtkDamage();
        player.buyEquipment(sword1);
        assertEquals(atkAvant + sword1.getAttackBonus(), player.getAtkDamage(), 0.01);
    }

    @Test
    public void testAchatImpossibleSansOr() {
        Hero hero = createHero();
        Player pauvre = new Player(hero);
        double atkAvant = pauvre.getAtkDamage();
        pauvre.buyEquipment(sword1);
        assertEquals(atkAvant, pauvre.getAtkDamage(), 0.01);
    }

    @Test
    public void testFusionRetireItems() {
        player.buyEquipment(sword1);
        player.buyEquipment(sword2);
        player.fuseEquipment(1, 2, fusedSword);
        assertFalse(player.hasEquipment(1));
        assertFalse(player.hasEquipment(2));
    }

    @Test
    public void testFusionAjouteNouvelItem() {
        player.buyEquipment(sword1);
        player.buyEquipment(sword2);
        player.fuseEquipment(1, 2, fusedSword);
        assertTrue(player.hasEquipment(101));
    }

    @Test
    public void testDefenseReduitDegats() {
        Hero hero = createHero();
        hero.setDefense(100);
        Player tank = new Player(hero);
        double hpAvant = tank.getHp();
        tank.takeDamage(100);
        double degatsReels = hpAvant - tank.getHp();
        assertTrue(degatsReels < 100);
    }

    @Test
    public void testSansDefenseDegatsPlein() {
        double hpAvant = player.getHp();
        player.takeDamage(100);
        double degatsReels = hpAvant - player.getHp();
        assertEquals(100.0, degatsReels, 0.01);
    }

    private Hero createHero() {
        Hero hero = new Hero();
        hero.setName("TestHero");
        hero.setMaxHp(500);
        hero.setAttack(50);
        hero.setDefense(0);
        hero.setAttackSpeed(1.0);
        hero.setMaxMana(200);
        hero.setSpeed(300);
        hero.setAtkRange(200);
        hero.setSpriteFile("");
        return hero;
    }
}