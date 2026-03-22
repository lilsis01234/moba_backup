package data.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un héros jouable dans le jeu.
 * 
 * Concepts clés pour un débutants:
 * - Cette classe vient de la base de données JSON (Core/Database/)
 * - Chaque héros a des statistiques différentes (PV, attaque, défense, vitesse)
 * - spells = liste des sorts du héros
 * 
 * Les données sont chargées depuis JSON et transformées en objets Java
 */
public class Hero {
    private int id;
    private String name;
    private String history;
    private int categoryId;
    private int baseHp;
    private int maxHp;
    private int attack;
    private int defense;
    private double attackSpeed;
    private int maxMana;
    private double speed;
    private double atkRange;
    private List<Spell> spells;
    
    public Hero() {
        this.spells = new ArrayList<>();
    }
    
    public Hero(int id, String name, String history, int categoryId, int baseHp, int maxHp, 
                int attack, int defense, double attackSpeed, int maxMana) {
        this.id = id;
        this.name = name;
        this.history = history;
        this.categoryId = categoryId;
        this.baseHp = baseHp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.attackSpeed = attackSpeed;
        this.maxMana = maxMana;
        this.spells = new ArrayList<>();
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getHistory() {
        return history;
    }
    
    public void setHistory(String history) {
        this.history = history;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public int getBaseHp() {
        return baseHp;
    }
    
    public void setBaseHp(int baseHp) {
        this.baseHp = baseHp;
    }
    
    public int getMaxHp() {
        return maxHp;
    }
    
    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }
    
    public int getAttack() {
        return attack;
    }
    
    public void setAttack(int attack) {
        this.attack = attack;
    }
    
    public int getDefense() {
        return defense;
    }
    
    public void setDefense(int defense) {
        this.defense = defense;
    }
    
    public double getAttackSpeed() {
        return attackSpeed;
    }
    
    public void setAttackSpeed(double attackSpeed) {
        this.attackSpeed = attackSpeed;
    }
    
    public int getMaxMana() {
        return maxMana;
    }
    
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }
    
    public double getSpeed() {
        return speed;
    }
    
    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public double getAtkRange() { return atkRange; }
    
    public void setAtkRange(double atkRange) { this.atkRange = atkRange; }
    
    public List<Spell> getSpells() {
        return spells;
    }
    
    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }
    
    public void addSpell(Spell spell) {
        this.spells.add(spell);
    }
}
