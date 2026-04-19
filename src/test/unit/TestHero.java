package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import data.model.Hero;
import data.model.Spell;
import data.model.Spell.Type;
import data.model.Item;

public class TestHero {

    private Hero hero;

    @Before
    public void prepare() {
        hero = new Hero();
        hero.setId(1);
        hero.setName("Arthur");
        hero.setHistory("Un chevalier légendaire");
        hero.setCategoryId(2);
        hero.setMaxHp(500);
        hero.setAttack(50);
        hero.setDefense(20);
        hero.setAttackSpeed(1.5);
        hero.setMaxMana(200);
        hero.setSpeed(300);
        hero.setAtkRange(150);
        hero.setSpriteFile("arthur.png");
    }

    @Test
    public void testId() {
        assertEquals(1, hero.getId());
    }

    @Test
    public void testNom() {
        assertEquals("Arthur", hero.getName());
    }

    @Test
    public void testHistory() {
        assertEquals("Un chevalier légendaire", hero.getHistory());
    }

    @Test
    public void testCategoryId() {
        assertEquals(2, hero.getCategoryId());
    }

    @Test
    public void testMaxHp() {
        assertEquals(500, hero.getMaxHp());
    }

    @Test
    public void testAttaque() {
        assertEquals(50, hero.getAttack());
    }

    @Test
    public void testDefense() {
        assertEquals(20, hero.getDefense());
    }

    @Test
    public void testVitesseAttaque() {
        assertEquals(1.5, hero.getAttackSpeed(), 0.001);
    }

    @Test
    public void testManaMax() {
        assertEquals(200, hero.getMaxMana());
    }

    @Test
    public void testVitesse() {
        assertEquals(300.0, hero.getSpeed(), 0.001);
    }

    @Test
    public void testPorteeAttaque() {
        assertEquals(150.0, hero.getAtkRange(), 0.001);
    }

    @Test
    public void testSpriteFile() {
        assertEquals("arthur.png", hero.getSpriteFile());
    }

    @Test
    public void testConstructeurComplet() {
        Hero h = new Hero(42, "Merlin", "merlin.png", "Un vieux sage", 3,
                          400, 30, 10, 1.0, 300);
        assertEquals(42,       h.getId());
        assertEquals("Merlin", h.getName());
        assertEquals("merlin.png", h.getSpriteFile());
        assertEquals("Un vieux sage", h.getHistory());
        assertEquals(3,        h.getCategoryId());
        assertEquals(400,      h.getMaxHp());
        assertEquals(30,       h.getAttack());
        assertEquals(10,       h.getDefense());
        assertEquals(1.0,      h.getAttackSpeed(), 0.001);
        assertEquals(300,      h.getMaxMana());
    }

    @Test
    public void testConstructeurCompletSpellsVides() {
        Hero h = new Hero(1, "Merlin", "merlin.png", "Sage", 1,
                          400, 30, 10, 1.0, 300);
        assertNotNull(h.getSpells());
        assertTrue(h.getSpells().isEmpty());
    }

    @Test
    public void testConstructeurCompletItemsVides() {
        Hero h = new Hero(1, "Merlin", "merlin.png", "Sage", 1,
                          400, 30, 10, 1.0, 300);
        assertNotNull(h.getItems());
        assertTrue(h.getItems().isEmpty());
    }

    @Test
    public void testSpellsVidesParDefaut() {
        assertNotNull(hero.getSpells());
        assertTrue(hero.getSpells().isEmpty());
    }

    @Test
    public void testItemsVidesParDefaut() {
        assertNotNull(hero.getItems());
        assertTrue(hero.getItems().isEmpty());
    }

    @Test
    public void testAjoutUnSort() {
        Spell sort = makeSpell(1, "Boule de feu", Type.DAMAGE);
        hero.addSpell(sort);
        assertEquals(1, hero.getSpells().size());
    }

    @Test
    public void testAjoutPlusieursSpells() {
        hero.addSpell(makeSpell(1, "Boule de feu", Type.DAMAGE));
        hero.addSpell(makeSpell(2, "Éclair",       Type.DAMAGE));
        hero.addSpell(makeSpell(3, "Soin",         Type.SUPPORT));
        assertEquals(3, hero.getSpells().size());
    }

    @Test
    public void testSortBienAjoute() {
        Spell sort = makeSpell(1, "Boule de feu", Type.DAMAGE);
        hero.addSpell(sort);
        assertEquals(sort, hero.getSpells().get(0));
    }

    @Test
    public void testSetSpellsRemplaceListe() {
        hero.addSpell(makeSpell(1, "Ancien sort", Type.DAMAGE));

        java.util.List<Spell> nouvelle = new java.util.ArrayList<>();
        nouvelle.add(makeSpell(2, "Nouveau sort", Type.SUPPORT));
        hero.setSpells(nouvelle);

        assertEquals(1, hero.getSpells().size());
        assertEquals("Nouveau sort", hero.getSpells().get(0).getName());
    }

    @Test
    public void testAjoutSortNIncrementePasAutreListe() {
        hero.addSpell(makeSpell(1, "Boule de feu", Type.DAMAGE));
        assertTrue(hero.getItems().isEmpty());
    }

    //Modification des stats

    @Test
    public void testModificationAttaque() {
        hero.setAttack(100);
        assertEquals(100, hero.getAttack());
    }

    @Test
    public void testModificationDefense() {
        hero.setDefense(50);
        assertEquals(50, hero.getDefense());
    }

    @Test
    public void testModificationMaxHp() {
        hero.setMaxHp(1000);
        assertEquals(1000, hero.getMaxHp());
    }

    @Test
    public void testModificationVitesse() {
        hero.setSpeed(500);
        assertEquals(500.0, hero.getSpeed(), 0.001);
    }

    @Test
    public void testModificationMana() {
        hero.setMaxMana(400);
        assertEquals(400, hero.getMaxMana());
    }

    @Test
    public void testModificationPortee() {
        hero.setAtkRange(300);
        assertEquals(300.0, hero.getAtkRange(), 0.001);
    }

    private Spell makeSpell(int id, String name, Type type) {
        Spell s = new Spell();
        s.setId(id);
        s.setName(name);
        s.setType(type);
        s.setDamage(40);
        s.setManaCost(20);
        s.setCooldown(1.0);
        return s;
    }
}