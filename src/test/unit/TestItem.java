package test.unit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import data.model.Item;

public class TestItem {

    private Item swordItem;
    private Item armorItem;
    private Item bootItem;

    @Before
    public void prepare() {
        swordItem = new Item("Epee de Fer", 15, 0, 0.0, 0, 0);
        armorItem = new Item("Armure de Fer", 0, 25, 0.0, 100, 0);
        bootItem = new Item("Bottes de Vitesse", 0, 0, 0.5, 0, 50);
    }

    @Test
    public void testNomCorrect() {
        assertEquals("Epee de Fer", swordItem.getName());
    }

    @Test
    public void testAttackBonus() {
        assertEquals(15, swordItem.getAttackBonus());
    }

    @Test
    public void testDefenseBonus() {
        assertEquals(25, armorItem.getDefenseBonus());
    }

    @Test
    public void testSpeedBonus() {
        assertEquals(0.5, bootItem.getSpeedBonus(), 0.001);
    }

    @Test
    public void testHpBonus() {
        assertEquals(100, armorItem.getHpBonus());
    }

    @Test
    public void testManaBonus() {
        assertEquals(50, bootItem.getManaBonus());
    }

    @Test
    public void testSansBonusRenvoieZero() {
        assertEquals(0, swordItem.getDefenseBonus());
        assertEquals(0, swordItem.getSpeedBonus(), 0.001);
        assertEquals(0, swordItem.getHpBonus());
        assertEquals(0, swordItem.getManaBonus());
    }

    @Test
    public void testItemAucunBonus() {
        Item emptyItem = new Item("Empty", 0, 0, 0.0, 0, 0);
        assertEquals(0, emptyItem.getAttackBonus());
        assertEquals(0, emptyItem.getDefenseBonus());
        assertEquals(0.0, emptyItem.getSpeedBonus(), 0.001);
        assertEquals(0, emptyItem.getHpBonus());
        assertEquals(0, emptyItem.getManaBonus());
    }
}