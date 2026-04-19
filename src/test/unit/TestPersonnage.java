package test.unit;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import data.model.Hero;
import data.model.Spell;
import data.model.Equipment;
import engine.mobile.Bot;
import game_config.GameConfiguration;

import java.util.ArrayList;
import java.util.List;

public class TestPersonnage {

    private Bot bot;
    private Hero hero;
    private List<double[]> waypoints;

    @Before
    public void prepare() {
        waypoints = new ArrayList<>();
        waypoints.add(new double[]{200, 200});

        hero = makeHero(1, "Arthur", 500, 50, 0, 1.0, 200, 300, 150);
        bot  = new Bot(waypoints, 0, "Bot1", hero);
    }

    @Test
    public void testHpChargéDepuisHero() {
        assertEquals(hero.getMaxHp(), (int) bot.getMaxHp());
    }

    @Test
    public void testManaChargéeDepuisHero() {
        assertEquals(hero.getMaxMana(), (int) bot.getMaxMana());
    }

    @Test
    public void testSpeedChargéeDepuisHero() {
        assertEquals(hero.getSpeed(), bot.getSpeed(), 0.001);
    }

    @Test
    public void testDefenseChargéeDepuisHero() {
        assertEquals(hero.getDefense(), bot.getDefense());
    }

    @Test
    public void testHpInitialEgalMaxHp() {
        assertEquals(bot.getMaxHp(), bot.getHp(), 0.001);
    }

    @Test
    public void testManaInitialeEgaleMaxMana() {
        assertEquals(bot.getMaxMana(), bot.getMana(), 0.001);
    }

    @Test
    public void testNiveauInitialUn() {
        assertEquals(1, bot.getLevel());
    }

    @Test
    public void testGoldInitialZero() {
        assertEquals(0, bot.getGold());
    }

    @Test
    public void testXpInitialZero() {
        assertEquals(0, bot.getXp());
    }

    @Test
    public void testSkillPointInitialUn() {
        assertEquals(1, bot.getSkillPoints());
    }

    @Test
    public void testAddGoldIncremente() {
        bot.addGold(100);
        assertEquals(100, bot.getGold());
    }

    @Test
    public void testAddGoldPlusieurs() {
        bot.addGold(100);
        bot.addGold(50);
        assertEquals(150, bot.getGold());
    }

    @Test
    public void testAddXpIncremente() {
        bot.addXp(50);
        assertEquals(50, bot.getXp());
    }

    @Test
    public void testLevelUpQuandXpSuffisant() {
        bot.addXp(100); 
        assertEquals(2, bot.getLevel());
    }

    @Test
    public void testXpReinitialiseeApresLevelUp() {
        bot.addXp(150);
        assertEquals(50, bot.getXp());
    }

    @Test
    public void testLevelUpAugmenteMaxHp() {
        double maxHpAvant = bot.getMaxHp();
        bot.addXp(100);
        assertEquals(maxHpAvant + GameConfiguration.LEVEL_HP_BONUS, bot.getMaxHp(), 0.001);
    }

    @Test
    public void testLevelUpAugmenteMaxMana() {
        double maxManaAvant = bot.getMaxMana();
        bot.addXp(100);
        assertEquals(maxManaAvant + GameConfiguration.LEVEL_MANA_BONUS, bot.getMaxMana(), 0.001);
    }

    @Test
    public void testLevelUpAugmenteAtkDamage() {
        double atkAvant = bot.getAtkDamage();
        bot.addXp(100);
        assertEquals(atkAvant + GameConfiguration.LEVEL_DMG_BONUS, bot.getAtkDamage(), 0.001);
    }

    @Test
    public void testLevelUpDonneUnSkillPoint() {
        int spAvant = bot.getSkillPoints();
        bot.addXp(100);
        assertEquals(spAvant + 1, bot.getSkillPoints());
    }

    @Test
    public void testNiveauMaxQuinze() {
        for (int i = 0; i < 20; i++) bot.addXp(99999);
        assertEquals(15, bot.getLevel());
    }

    @Test
    public void testPasDeXpAuNiveauMax() {
        for (int i = 0; i < 20; i++) bot.addXp(99999);
        int xpAvantMax = bot.getXp();
        bot.addXp(99999);
        assertEquals(xpAvantMax, bot.getXp());
    }

    // Dégâts et défense

    @Test
    public void testTakeDamageSansDefenseValeurPleine() {
        double hpAvant = bot.getHp();
        bot.takeDamage(100);
        assertEquals(hpAvant - 100, bot.getHp(), 0.01);
    }

    @Test
    public void testTakeDamageAvecDefenseReduit() {
        Hero tankHero = makeHero(2, "Tank", 500, 50, 100, 1.0, 200, 300, 150);
        Bot tank = new Bot(waypoints, 0, "Tank", tankHero);
        double hpAvant = tank.getHp();
        tank.takeDamage(100);
        // réduction = 100/(100+100)
        assertTrue(tank.getHp() > hpAvant - 100);
    }

    @Test
    public void testTakeDamageMinimumUnDegat() {
        // Même avec énorme défense, minimum 1 dégât
        Hero forteresse = makeHero(3, "Fort", 500, 50, 9999, 1.0, 200, 300, 150);
        Bot fort = new Bot(waypoints, 0, "Fort", forteresse);
        double hpAvant = fort.getHp();
        fort.takeDamage(10);
        assertTrue(fort.getHp() < hpAvant);
    }

    @Test
    public void testHpNeTombePasEnNegatif() {
        bot.takeDamage(999999);
        assertTrue(bot.getHp() >= 0);
    }

    //Mort et respawn

    @Test
    public void testDieRendInactif() {
        bot.die();
        assertFalse(bot.isActive());
    }

    @Test
    public void testDieMetHpAZero() {
        bot.die();
        assertEquals(0.0, bot.getHp(), 0.001);
    }

    @Test
    public void testDieInitialiseRespawnTimer() {
        bot.die();
        // timer = 5 + level*2 = 5 + 1*2 = 7
        assertEquals(7.0, bot.getRespawnTimer(), 0.001);
    }

    @Test
    public void testRespawnTimerAugmenteAvecNiveau() {
        bot.addXp(100); // level 2
        bot.die();
        assertEquals(5.0 + bot.getLevel() * 2.0, bot.getRespawnTimer(), 0.001);
    }

    @Test
    public void testRespawnApresTimerEcoule() {
        bot.die();
        bot.respawn(10.0);
        assertTrue(bot.isActive());
    }

    @Test
    public void testRespawnRestaurerHp() {
        bot.die();
        bot.respawn(10.0);
        assertEquals(bot.getMaxHp(), bot.getHp(), 0.001);
    }

    @Test
    public void testRespawnRestaurerMana() {
        bot.die();
        bot.respawn(10.0);
        assertEquals(bot.getMaxMana(), bot.getMana(), 0.001);
    }

    @Test
    public void testPasDeRespawnSiTimerPasEcoule() {
        bot.die();
        bot.respawn(1.0); // timer = 7s, on attend seulement 1s
        assertFalse(bot.isActive());
    }

    @Test
    public void testRestoreManaAugmente() {
        bot.takeDamage(1); // Pour forcer un état vivant
        double manaMax = bot.getMaxMana();
       bot.restoreMana(999);
        assertEquals(manaMax, bot.getMana(), 0.001);
    }

    @Test
    public void testRestoreManaNeDepassePasMax() {
        bot.restoreMana(99999);
        assertEquals(bot.getMaxMana(), bot.getMana(), 0.001);
    }

    //Recall 

    @Test
    public void testStartRecallActiveLeRecall() {
        bot.startRecall();
        assertTrue(bot.isRecalling());
    }

    @Test
    public void testInterruptRecallAnnule() {
        bot.startRecall();
        bot.interruptRecall();
        assertFalse(bot.isRecalling());
    }

    @Test
    public void testRecallTimerInitialisé() {
        bot.startRecall();
        assertEquals(GameConfiguration.RECALL_DURATION, bot.getRecallTimer(), 0.001);
    }

    @Test
    public void testRecallDecrementeTimer() {
        bot.startRecall();
        double timerAvant = bot.getRecallTimer();
        bot.updateRecall(1.0);
        assertEquals(timerAvant - 1.0, bot.getRecallTimer(), 0.001);
    }

    @Test
    public void testRecallTermineApresTimer() {
        bot.startRecall();
        bot.updateRecall(GameConfiguration.RECALL_DURATION + 0.1);
        assertFalse(bot.isRecalling());
    }

    @Test
    public void testTakeDamageInterruptRecall() {
        bot.startRecall();
        bot.takeDamage(10);
        assertFalse(bot.isRecalling());
    }

    @Test
    public void testRecallSurBotMortImpossible() {
        bot.die();
        bot.startRecall();
        assertFalse(bot.isRecalling());
    }

    //Stun
    @Test
    public void testApplyStunEstStunned() {
        bot.applyStun(2.0);
        assertTrue(bot.isStunned());
    }

    @Test
    public void testStunExpireApresTimer() {
        bot.applyStun(1.0);
        bot.updateTimers(1.5);
        assertFalse(bot.isStunned());
    }

    @Test
    public void testStunPlusLongNEcrasesPasLePlusLong() {
        bot.applyStun(5.0);
        bot.applyStun(2.0); // plus court, ne doit pas écraser
        bot.updateTimers(3.0);
        assertTrue(bot.isStunned()); // il reste encore à peu près 2s
    }

    //Sorts

    @Test
    public void testUpgradeSpellSansPointsEchoue() {
        // skillPoints = 1 au départ, on le dépense d'abord
        Spell s = makeSpell(1, "Feu");
        hero.addSpell(s);
        Bot b = new Bot(waypoints, 0, "B", hero);
        b.upgradeSpell(0); // dépense le point
        assertFalse(b.upgradeSpell(0)); // plus de points
    }

    @Test
    public void testUpgradeSpellAvecPointReussit() {
        Spell s = makeSpell(1, "Feu");
        hero.addSpell(s);
        Bot b = new Bot(waypoints, 0, "B", hero);
        assertTrue(b.upgradeSpell(0));
    }

    @Test
    public void testUpgradeSpellDecrementeSkillPoints() {
        Spell s = makeSpell(1, "Feu");
        hero.addSpell(s);
        Bot b = new Bot(waypoints, 0, "B", hero);
        int spAvant = b.getSkillPoints();
        b.upgradeSpell(0);
        assertEquals(spAvant - 1, b.getSkillPoints());
    }

    @Test
    public void testUpgradeSpellIndexInvalideEchoue() {
        assertFalse(bot.upgradeSpell(99));
    }

    @Test
    public void testCastSpellSortVerrouillEchoue() {
        Spell s = makeSpell(1, "Feu");
        hero.addSpell(s);
        Bot b = new Bot(waypoints, 0, "B", hero);
        // sort non déverrouillé (level = 0)
        assertFalse(b.castSpell(0, null));
    }

    @Test
    public void testCastSpellManqueManaMechoue() {
        Spell s = makeSpell(1, "Feu");
        s.setManaCost(99999);
        s.upgrade(); // déverrouille le sort
        hero.addSpell(s);
        Bot b = new Bot(waypoints, 0, "B", hero);
        assertFalse(b.castSpell(0, null));
    }

    @Test
    public void testBuyEquipmentDeduiteOr() {
        bot.addGold(500);
        Equipment sword = new Equipment(1, "Epée", null, 15, 0, 350, "test");
        bot.buyEquipment(sword);
        assertEquals(500 - 350, bot.getGold());
    }

    @Test
    public void testBuyEquipmentAppliqueAttackBonus() {
        bot.addGold(500);
        double atkAvant = bot.getAtkDamage();
        Equipment sword = new Equipment(1, "Epée", null, 15, 0, 350, "test");
        bot.buyEquipment(sword);
        assertEquals(atkAvant + 15, bot.getAtkDamage(), 0.001);
    }

    @Test
    public void testBuyEquipmentSansOrEchoue() {
        double atkAvant = bot.getAtkDamage();
        Equipment sword = new Equipment(1, "Epée", null, 15, 0, 350, "test");
        bot.buyEquipment(sword); // gold = 0
        assertEquals(atkAvant, bot.getAtkDamage(), 0.001);
    }

    @Test
    public void testHasEquipmentApresAchat() {
        bot.addGold(500);
        Equipment sword = new Equipment(1, "Epée", null, 15, 0, 350, "test");
        bot.buyEquipment(sword);
        assertTrue(bot.hasEquipment(1));
    }

    @Test
    public void testFusionRetireLesDeux() {
        bot.addGold(9999);
        Equipment s1 = new Equipment(1, "Epée de fer",   null, 15, 0, 350, "test");
        Equipment s2 = new Equipment(2, "Epée d'acier",  null, 25, 0, 600, "test");
        Equipment sf = new Equipment(101, "Lame Fusionnée", null, 65, 0, "fusion", 1, 2);
        bot.buyEquipment(s1);
        bot.buyEquipment(s2);
        bot.fuseEquipment(1, 2, sf);
        assertFalse(bot.hasEquipment(1));
        assertFalse(bot.hasEquipment(2));
    }

    @Test
    public void testFusionAjouteNouvelItem() {
        bot.addGold(9999);
        Equipment s1 = new Equipment(1, "Epée de fer",   null, 15, 0, 350, "test");
        Equipment s2 = new Equipment(2, "Epée d'acier",  null, 25, 0, 600, "test");
        Equipment sf = new Equipment(101, "Lame Fusionnée", null, 65, 0, "fusion", 1, 2);
        bot.buyEquipment(s1);
        bot.buyEquipment(s2);
        bot.fuseEquipment(1, 2, sf);
        assertTrue(bot.hasEquipment(101));
    }

    private Hero makeHero(int id, String name, int hp, int atk,
                          int def, double atkSpd, int mana, double spd, double range) {
        Hero h = new Hero();
        h.setId(id);
        h.setName(name);
        h.setMaxHp(hp);
        h.setAttack(atk);
        h.setDefense(def);
        h.setAttackSpeed(atkSpd);
        h.setMaxMana(mana);
        h.setSpeed(spd);
        h.setAtkRange(range);
        h.setSpriteFile("");
        return h;
    }

    private Spell makeSpell(int id, String name) {
        Spell s = new Spell();
        s.setId(id);
        s.setName(name);
        s.setManaCost(30);
        s.setCooldown(1.0);
        s.setDamage(50);
        s.setEffect((caster, target, level) -> {});
        return s;
    }
}