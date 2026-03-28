package engine.mobile;

import java.awt.*;

import game_config.GameConfiguration;
//we will need to make a better respawning logic(depending on lvl can cause issues)
//we will need to update the loot system( having to last hit to get any loot very difficult)

public abstract class Personnage extends Entity {

    protected double speed;
    protected double mana;
    protected double maxMana;
    private int gold  = 0;
    private int xp    = 0;
    private int level = 1;

    protected double respawnTimer = 0;
    
    public Personnage(double x, double y, double maxHP, int team, double maxMana, double speed) {
        super(x, y, maxHP,team);
        this.speed = speed;
        this.maxMana = maxMana;
        this.loot = GameConfiguration.GOLD_CHAR;
        this.XPloot = GameConfiguration.XP_CHAR;
    }

    protected void drawManaBar(Graphics2D g2, int px, int py, int size, int yOffset) {
        g2.setColor(Color.GRAY);
        g2.fillRect(px - size/2, py - size - yOffset, size, 4);
        g2.setColor(Color.CYAN);
        int manaWidth = (int)((mana / maxMana) * size);
        g2.fillRect(px - size/2, py - size - yOffset, manaWidth, 4);
        g2.setColor(Color.BLACK);
        g2.drawRect(px - size/2, py - size - yOffset, size, 4);
    }
    
    public abstract void render(Graphics2D g2, int width, int height);

    public void attack(Entity target, double deltaTime) {
        atkTimer -= deltaTime;
        if (atkTimer <= 0 && getDistanceTo(target) <= atkRange && target.isActive()) {
            target.takeDamage(atkDamage);
            this.checkKill(target); 
            atkTimer = atkCooldown;
        }
    }
    
    private void checkKill(Entity target) {
        if (!target.isActive()) {
            this.addGold(target.getLoot());
            this.addXp(target.getXPLoot());
        }
    }
    public void addGold(int Goldreward) {
        gold += Goldreward;
    }

    public void addXp(int XPReward) {
        xp += XPReward;
        int threshold = this.level * 100;
        if (xp >= threshold) {
            xp -= threshold;
            level++;
            maxHp     += GameConfiguration.LEVEL_HP_BONUS;
            maxMana   += GameConfiguration.LEVEL_MANA_BONUS;
            atkDamage += GameConfiguration.LEVEL_DMG_BONUS;

        }
    }
    
    public void restoreMana(double amount) {
        mana += amount;
        if (mana > maxMana) mana = maxMana;
    }
    public void respawn(double deltaTime) {
        if (!active) {
            respawnTimer -= deltaTime;
            if (respawnTimer <= 0) {
                onRespawn(); // Trigger the specific placement logic
            }
        }
    }
    protected abstract void onRespawn();
    public void die() {
        this.active = false;
        this.hp = 0;
        // placeholder
        this.respawnTimer = 5.0 + (this.getLevel() * 2.0);
    }
    
    public double getSpeed() { return speed; }
    public double getMana() { return mana; }
    public double getMaxMana() { return maxMana; }
    public int getLevel() { return level; }
    public int getGold()  { return gold; }
    public int getXp()    { return xp; }
    public double getRespawnTimer() {return respawnTimer;}
}
