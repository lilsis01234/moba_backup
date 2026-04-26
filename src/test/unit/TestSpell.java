package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import data.model.Spell;
import data.model.Spell.Type;

public class TestSpell {

    private Spell spell;

    @Before
    public void prepare() {
        spell = new Spell();
        spell.setId(1);
        spell.setHeroId(10);
        spell.setName("Boule de feu");
        spell.setDescription("Lance une boule de feu sur l'ennemi");
        spell.setDamage(80);
        spell.setBuff(0);
        spell.setCooldown(1.5);
        spell.setManaCost(30);
        spell.setType(Type.DAMAGE);
    }

    @Test
    public void testId() {
        assertEquals(1, spell.getId());
    }

    @Test
    public void testHeroId() {
        assertEquals(10, spell.getHeroId());
    }

    @Test
    public void testNom() {
        assertEquals("Boule de feu", spell.getName());
    }

    @Test
    public void testDescription() {
        assertEquals("Lance une boule de feu sur l'ennemi", spell.getDescription());
    }

    @Test
    public void testDamage() {
        assertEquals(80, spell.getDamage());
    }

    @Test
    public void testBuff() {
        assertEquals(0, spell.getBuff());
    }

    @Test
    public void testCooldown() {
        assertEquals(1.5, spell.getCooldown(), 0.001);
    }

    @Test
    public void testManaCost() {
        assertEquals(30, spell.getManaCost());
    }

    @Test
    public void testType() {
        assertEquals(Type.DAMAGE, spell.getType());
    }

    @Test
    public void testNiveauInitialZero() {
        Spell nouveau = new Spell();
        assertEquals(0, nouveau.getSpellLevel());
    }

    @Test
    public void testVerrouParDefaut() {
        Spell nouveau = new Spell();
        assertFalse(nouveau.isUnlocked());
    }

    @Test
    public void testPremierUpgradeDeverrouille() {
        spell.upgrade();
        assertTrue(spell.isUnlocked());
    }

    @Test
    public void testUpgradeIncrementeNiveau() {
        spell.upgrade();
        assertEquals(1, spell.getSpellLevel());
    }

    @Test
    public void testUpgradePlusieurs() {
        spell.upgrade();
        spell.upgrade();
        spell.upgrade();
        assertEquals(3, spell.getSpellLevel());
    }

    @Test
    public void testUpgradePlafondMaxLevel() {
        for (int i = 0; i < 10; i++) spell.upgrade();
        assertEquals(Spell.MAX_LEVEL, spell.getSpellLevel());
    }

    @Test
    public void testUpgradeNeDepassePasMax() {
        for (int i = 0; i < Spell.MAX_LEVEL + 3; i++) spell.upgrade();
        assertTrue(spell.getSpellLevel() <= Spell.MAX_LEVEL);
    }

    @Test
    public void testMaxLevelVautCinq() {
        assertEquals(5, Spell.MAX_LEVEL);
    }

    @Test
    public void testTypeCrowdControl() {
        spell.setType(Type.CROWD_CONTROL);
        assertEquals(Type.CROWD_CONTROL, spell.getType());
    }

    @Test
    public void testTypeSupport() {
        spell.setType(Type.SUPPORT);
        assertEquals(Type.SUPPORT, spell.getType());
    }

    @Test
    public void testCastAppelleStrategy() {
        // On vérifie que cast() délègue bien à l'effet injecté
        boolean[] appele = {false};

        spell.upgrade(); // spellLevel = 1, sort déverrouillé

        spell.setEffect((caster, target, level) -> {
            appele[0] = true;
            assertEquals(1, level);
        });

        spell.cast(null, null); // caster/target peuvent être null
        assertTrue("La strategy doit être appelée par cast()", appele[0]);
    }

    @Test
    public void testCastPasseLeNiveauCorrect() {
        spell.upgrade();
        spell.upgrade(); // niveau 2

        int[] niveauRecu = {-1};
        spell.setEffect((caster, target, level) -> niveauRecu[0] = level);

        spell.cast(null, null);
        assertEquals(2, niveauRecu[0]);
    }
}