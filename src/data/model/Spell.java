package data.model;

import engine.mobile.Entity;

import engine.mobile.Personnage;
import engine.mobile.SpellStrategy;

public class Spell {
    private int id;
    private int heroId;
    private String name;
    private String description;
    private int damage;
    private double cooldown;
    private int manaCost;
  
    //deleted the type class replaces by enum
    public enum Type { DAMAGE, CROWD_CONTROL, SUPPORT; }
    private Type type;
    
    private SpellStrategy effect;
 
    public Spell() {}
  
    
    public int getId() {
        return id;
    }
    
    public void cast(Personnage caster, Entity target) {
        effect.cast(caster, target);
    }
    public void setEffect(SpellStrategy effect) {
        this.effect = effect;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public int getHeroId() {
        return heroId;
    }
    
    public void setHeroId(int heroId) {
        this.heroId = heroId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public double getCooldown() {
        return cooldown;
    }
    
    public void setCooldown(double cooldown) {
        this.cooldown = cooldown;
    }
    
    public int getManaCost() {
        return manaCost;
    }
    
    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }
    
    public Type getType() {
        return type;
    }
    
    public void setType(Type type) {
        this.type = type;
    }
}
