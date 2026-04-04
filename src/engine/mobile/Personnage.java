package engine.mobile;
import data.model.Hero;
import java.awt.*;
import java.awt.image.BufferedImage;
import data.model.KDA;

import java.util.ArrayList;
import java.util.HashMap;
import game_config.GameConfiguration;
import gui.Sprites.HeroSprites;

// make a better respawning logic(depending on lvl can cause issues)
//update the loot system( having to last hit to get any loot very difficult)
//add assist option nd reward (about 40% of kill gold and xp only when apply buff/heal/damage enemy 5 seconds before the kill)
//stats HUD	
//add lvl cap
//change catchy colors of borders
//gatekeep bases
//implement normal attack(not auto)
//esuipemet +HUD
//Spells
//possible but rare bug: killed at the same time: it gives double the loot ? 1 thread so almost impossible but consider



public abstract class Personnage extends Entity {

    protected double speed;
    protected double mana;
    protected double maxMana;
    private int gold  = 0;
    private int xp    = 0;
    private int level = 1;
    
    //for KDA
    HashMap<Personnage, Long> damageTimestamps = new HashMap<>();
    private KDA kda = new KDA();
    
    protected double respawnTimer = 0;
    
    //animation
    
    protected enum State  {IDLE,MOVING}; //can add attacking animation if we have time
    protected State currentState = State.IDLE;
    protected HeroSprites heroSprites;
    protected Direction currentDirection = Direction.DOWN;
    protected int animFrame = 0;
    protected double animTimer = 0;
    private static final double FRAME_DURATION = 0.12;
    
    public Personnage(double x, double y, int team) {
        super(x, y, 1, team);
        this.loot   = GameConfiguration.GOLD_CHAR;
        this.XPloot = GameConfiguration.XP_CHAR;
        this.currentState = State.IDLE;
    }
    
    public void loadFromHero(Hero hero) {
        this.maxHp     = hero.getMaxHp();
        this.hp        = hero.getMaxHp();
        this.maxMana   = hero.getMaxMana();
        this.mana      = hero.getMaxMana();
        this.speed     = hero.getSpeed();
        this.atkDamage = hero.getAttack();
        this.atkRange  = hero.getAtkRange();
        this.atkCooldown = 1.0 / hero.getAttackSpeed();
        loadHeroGraphics(hero.getSpriteFile());
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

   public void attack(Entity target, double deltaTime, ArrayList<Personnage> allPersonnages) {
    atkTimer -= deltaTime;
    if (atkTimer <= 0 && getDistanceTo(target) <= atkRange && target.isActive()) {
        if (target instanceof Personnage) {
            recordDamageDealtTo((Personnage) target);
        }
        target.takeDamage(atkDamage);
        checkKill(target, allPersonnages);
        atkTimer = atkCooldown;
    }
}
    
   private void checkKill(Entity target, ArrayList<Personnage> allPersonnages) {
	    if (!target.isActive()) {
	        this.addGold(target.getLoot());
	        this.addXp(target.getXPLoot());
	        this.kda.addKill();

	        if (target instanceof Personnage) {
	            Personnage deadTarget = (Personnage) target;
	            deadTarget.kda.addDeath();

	            for (Personnage p : allPersonnages) {
	                if (p == this) continue;
	                if (p.getTeam() != this.getTeam()) continue;
	                if (p.assisted(deadTarget)) {
	                    p.kda.addAssist();
	                }
	            }
	        }
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
                onRespawn(); 
            }
        }
    }
    protected abstract void onRespawn();
    
    public void die() {
        this.active = false;
        this.hp = 0;
        this.respawnTimer = 5.0 + (this.getLevel() * 2.0);
        this.currentState = State.IDLE;
    }
    
    protected void updateAnimation(double deltaTime) {
    	
    	//stop animation
        if (currentState == State.IDLE) { 
            animFrame = 0; 	
            animTimer = 0;
            return;
        }
        animTimer += deltaTime;
        if (animTimer >= FRAME_DURATION) {
            animTimer = 0;
            int totalFrames = (heroSprites != null) ? heroSprites.getFramesPerDirection() :1;
            animFrame = (animFrame + 1) % totalFrames;
        }
    }
    protected void renderSprite(Graphics2D g2) {
        int size = GameConfiguration.TILE_SIZE;
        int px   = (int) x;
        int py   = (int) y;
        int imgSize = size * 3;

        if (heroSprites != null) {
            int dirIndex;
            switch (currentDirection) {
                case UP:    dirIndex = 0; break;
                case LEFT:  dirIndex = 1; break;
                case DOWN:  dirIndex = 2; break;
                case RIGHT: dirIndex = 3; break;
                default:    dirIndex = 2;
            }
            BufferedImage frame = heroSprites.get(dirIndex, animFrame);
            if (frame != null) {
                g2.drawImage(frame, px - imgSize / 2, py - imgSize / 2, imgSize, imgSize, null);
                return;
            }
        }    //in case img didnt load
        g2.setColor(team == 0 ? new Color(0, 150, 255) : new Color(255, 0, 150));
        g2.fillOval(px - size / 2, py - size / 2, size, size);
        g2.setColor(Color.BLACK);
        g2.drawOval(px - size / 2, py - size / 2, size, size);
    }
    
    public void loadHeroGraphics(String path) {
        this.heroSprites = new HeroSprites(path);
    }	
    
    public BufferedImage getFrontFrame() {
        if (heroSprites == null) return null;
        return heroSprites.get(2, 0); 
    }
    public void recordDamageDealtTo(Personnage target) {
        damageTimestamps.put(target, System.currentTimeMillis());
    }
    
    //if interacted with target in less than 5seconds before its death u get assist
    public boolean assisted(Personnage target) {
        Long t = damageTimestamps.get(target);
        return t != null && (System.currentTimeMillis() - t) <= 5000;
    }
    
    public double getSpeed() { return speed; }
    public double getMana() { return mana; }
    public double getMaxMana() { return maxMana; }
    public int getLevel() { return level; }
    public int getGold()  { return gold; }
    public int getXp()    { return xp; }
    public double getRespawnTimer() {return respawnTimer;}
    public KDA getKDA() { return kda; }
}
