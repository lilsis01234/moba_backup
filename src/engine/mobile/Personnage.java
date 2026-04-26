package engine.mobile;

import data.model.Equipment;
import data.model.Hero;
import data.model.KDA;
import data.model.Spell;
import game_config.GameConfiguration;
import gui.Sprites.HeroSprites;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Personnage extends Entity {

    protected double speed;
    protected double mana;
    protected double maxMana;
    private int gold = 0;
    private int totalGoldEarned = 0;
    private double goldAccumulator = 0;
    private double goldFlushTimer = 0;
    private int xp = 0;
    private int level = 1;
    protected int defense = 0;
    private List<Equipment> equippedGear = new ArrayList<Equipment>();
    
    protected Hero hero;
    
    //for KDA
    private int damageDealtToHeroes = 0;
    private int damageDealtToBuildings = 0;
    private KDA kda = new KDA();
    private int csCreeps = 0;
    
    protected double respawnTimer = 0;
    
    //animation
    protected enum State {IDLE, MOVING, ATTACKING}; 
    protected State currentState = State.IDLE;
    protected int animFrame = 0;
    
    	//walking
    protected HeroSprites heroSprites;
    protected Direction currentDirection = Direction.DOWN;
    protected double animTimer = 0;
    private static final double FRAME_DURATION = 0.12;
    
    	//attack
    protected HeroSprites atkSprites;           
    private  double attackAnimTimer  = 0;   
    private  static final double ATTACK_ANIM_DURATION = 0.5; 
        
    //recall    
    private boolean recalling = false;
    private double recallTimer = 0;
    private double recallDuration = GameConfiguration.RECALL_DURATION;

    // spells
    protected List<Spell> spells = new ArrayList<>();
    protected double[] spellCooldownTimers = new double[3];
    private int skillPoints = 1;

    // stun
    private double stunTimer = 0;

    //spell effects 
    private static BufferedImage effectAttacked = load("attacked.png");
    private static BufferedImage effectHealed   = load("healed.png");
    private static BufferedImage effectStunned  = load("stunned.png");
    private double effectTimer = 0;
    private BufferedImage currentEffect = null;
    private static final double EFFECT_DURATION = 1.0;

    static {
        try {
            effectAttacked = ImageIO.read(Personnage.class.getClassLoader().getResourceAsStream("res/Sorts/effects/attacked.png"));
            effectHealed   = ImageIO.read(Personnage.class.getClassLoader().getResourceAsStream("res/Sorts/effects/healed.png"));
            effectStunned  = ImageIO.read(Personnage.class.getClassLoader().getResourceAsStream("res/Sorts/effects/stunned.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static final Logger logger = LoggerUtility.getLogger(Personnage.class);

    public Personnage(double x, double y, int team, Hero hero) {
        super(x, y, 1, team);
        this.hero = hero;
        this.loot   = GameConfiguration.GOLD_CHAR;
        this.XPloot = GameConfiguration.XP_CHAR; 
        this.currentState = State.IDLE;
        loadFromHero(hero);
    }

    public void addCsCreep() { csCreeps++; }

    public void addDamageToBuildings(int dmg) {
        damageDealtToBuildings += dmg;
    }

    public void addDamageToHeroes(int dmg) {
        damageDealtToHeroes += dmg;
    }

    public void addGold(int Goldreward) {
        gold += Goldreward;
        totalGoldEarned += Goldreward;
    }

    public void addPassiveGold(double ratePerSecond, double deltaTime) {
        goldAccumulator += ratePerSecond * deltaTime;
        goldFlushTimer  += deltaTime;
        if (goldFlushTimer >= 1.0) {
            goldFlushTimer -= 1.0;
            int goldToAdd = (int) goldAccumulator;
            if (goldToAdd > 0) {
                gold += goldToAdd;
                totalGoldEarned += goldToAdd;
                goldAccumulator -= goldToAdd;
            }
        }
    }

    public void addXp(int XPReward) {
        if (level >= 15) return;
        xp += XPReward;
        int threshold = this.level * 100;
        if (xp >= threshold) {
            xp -= threshold;
            level++;
            maxHp     += GameConfiguration.LEVEL_HP_BONUS;
            maxMana   += GameConfiguration.LEVEL_MANA_BONUS;
            atkDamage += GameConfiguration.LEVEL_DMG_BONUS;
            skillPoints++; // earn one skill point per level-up
        }
    }

    //this was impelmented to avoid the little bug or cheat of getting stunned by a stun of lower time while already being stunned and thus overriding it 
    public void applyStun(double seconds) {
        stunTimer = Math.max(stunTimer, seconds);
        showEffect(effectStunned);
    }

    public boolean attack(Entity target, double deltaTime, ArrayList<Personnage> allPersonnages) {
        atkTimer -= deltaTime;
        if (atkTimer <= 0 && getDistanceTo(target) <= atkRange && target.isActive()) {
            interruptRecall();
            
            // Track damage on the target for assists
            target.trackDamage(this);

            if (target instanceof Personnage) {
                addDamageToHeroes((int) atkDamage);
            } else if (target instanceof Tower || target instanceof Base) {
                addDamageToBuildings((int) atkDamage);
            }
            
            target.takeDamage(atkDamage);

            if (!target.isActive()) {
                distributeRewards(target);
            }         
            atkTimer = atkCooldown;
            
            currentState = State.ATTACKING;
            attackAnimTimer = ATTACK_ANIM_DURATION;
            animFrame = 0;
            animTimer = 0;
            
            return true;
        }
        return false;
    }

    public void buyEquipment(Equipment eq) {
        if (gold < eq.getPrice()) return; 
        if (equippedGear.size() >= 6) return;
        gold -= eq.getPrice();
        equippedGear.add(eq);
        this.atkDamage += eq.getAttackBonus();
        this.defense   += eq.getDefenseBonus();
        logger.info("achat d'équipement réussi");
    }

    //we need to know if it was cast succesfully or no so we use a bool instead of a void
    public boolean castSpell(int index, Entity target) {
        if (index < 0 || index >= spells.size()) return false;
        Spell spell = spells.get(index);
        if (!spell.isUnlocked()) return false;
        if (spellCooldownTimers[index] > 0) return false;
        if (mana < spell.getManaCost()) return false;
        mana -= spell.getManaCost();
        spellCooldownTimers[index] = spell.getCooldown();
        spell.cast(this, target);
        return true;
    }

    public void die() {
        this.active = false;
        this.hp = 0;
        this.respawnTimer = 5.0 + (this.getLevel() * 2.0);
        this.currentState = State.IDLE;
        int goldLost = (int)(gold * 0.15);
        gold -= goldLost;
    }

    private void distributeRewards(Entity victim) {
        // Killer rewards
        this.addGold(victim.getLoot());
        this.addXp(victim.getXPLoot());

        if (victim instanceof Personnage) {
            this.kda.addKill();
            ((Personnage) victim).getKDA().addDeath();
        } else {
            this.addCsCreep();
        }

        // Assist rewards (60% to teammates who hit victim in last 5s)
        int assistGold = (int) (victim.getLoot() * 0.6);
        int assistXp = (int) (victim.getXPLoot() * 0.6);

       for (Personnage helper : victim.getAttackers().keySet()) {
		    if (helper == this || helper.getTeam() != this.getTeam()) continue;
		    long timestamp = victim.getAttackers().get(helper);
		    if (System.currentTimeMillis() - timestamp <= 5000) {
		        helper.addGold(assistGold);
		        helper.addXp(assistXp);
		        helper.getKDA().addAssist();
		        if (!(victim instanceof Personnage)) {
		            helper.addCsCreep();
        }
    }
}
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

    private Equipment findEquipped(int id) {
        for (Equipment e : equippedGear) {
            if (e.getId() == id) return e;
         }
         return null;
    }

    public void fuseEquipment(int id1, int id2, Equipment result) {
        Equipment e1 = findEquipped(id1);
        Equipment e2 = findEquipped(id2);
        if (e1 == null || e2 == null) return;
        equippedGear.remove(e1);
        equippedGear.remove(e2);
        this.atkDamage -= (e1.getAttackBonus()  + e2.getAttackBonus());
        this.defense   -= (e1.getDefenseBonus() + e2.getDefenseBonus());
        equippedGear.add(result);
        this.atkDamage += result.getAttackBonus();
        this.defense   += result.getDefenseBonus();
        logger.info("fusion d'équipement réussi");
    }

    public int getCsCreeps() { return csCreeps; }

    public int getDamageDealtToBuildings() { return damageDealtToBuildings; }

    public int getDamageDealtToHeroes() { return damageDealtToHeroes; }

    public int getDefense()  { return defense; }

    public List<Equipment> getEquippedGear() { return equippedGear; }

    public BufferedImage getFrontFrame() {
        if (heroSprites == null) return null;
        return heroSprites.get(2, 0); 
    }

    public int getGold()  { return gold; }

    public KDA getKDA() { return kda; }

    public int getLevel() { return level; }

    public double getMana() { return mana; }

    public double getMaxMana() { return maxMana; }

    public double getRecallDuration() { return recallDuration; }

    public double getRecallTimer() { return recallTimer; }

    public double getRespawnTimer() {return respawnTimer;}

    public int getSkillPoints() { return skillPoints; }

    public double[] getSpellCooldownTimers() { return spellCooldownTimers; }

    public List<Spell> getSpells() { return spells; }

    public double getSpeed() { return speed; }

    public int getTotalGoldEarned() {
        return totalGoldEarned;
    }

    public int getXp()    { return xp; }

    public boolean hasEquipment(int id) { return findEquipped(id) != null; }

    @Override
    public void heal(double amount) {
        if (this.hp < this.maxHp) {
            super.heal(amount);
            showEffect(effectHealed);
        }
    }

    public void interruptRecall() {
        recalling = false;
        recallTimer = 0;
        currentState = State.IDLE;  
    }

    public boolean isRecalling() { return recalling; }

    public boolean isStunned() { return stunTimer > 0; }
    
    
    private static BufferedImage load(String name) {
        try {
            return ImageIO.read(Personnage.class.getResourceAsStream("/res/Sorts/effects/" + name));
        } catch (Exception e) { return null; }
    }

    public void loadAtkGraphics(String path) {
        this.atkSprites = new HeroSprites(path);
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
        loadAtkGraphics(hero.getAtkSpriteFile());
        this.spells = new ArrayList<>(hero.getSpells());
        this.spellCooldownTimers = new double[3];
        this.defense = hero.getDefense();
    }

    public void loadHeroGraphics(String path) {
        this.heroSprites = new HeroSprites(path);
    }   

    protected void onRespawn() {
        this.x = (team == 0) ? GameConfiguration.START_X : GameConfiguration.START_Y; 
        this.y = (team == 0) ? GameConfiguration.START_Y : GameConfiguration.START_X; 
        this.hp = maxHp;
        this.mana = maxMana;
        this.active = true;
        currentState = State.IDLE;
    }

    public abstract void render(Graphics2D g2, int width, int height);

    protected void renderSprite(Graphics2D g2) {
    	
        int size = GameConfiguration.TILE_SIZE;
        int px   = (int) x;
        int py   = (int) y;
        int imgSize = size * 3;

        if (heroSprites != null) {
            int dirIndex;
            if (currentDirection == Direction.UP) {
                dirIndex = 0;
            } else if (currentDirection == Direction.LEFT) {
                dirIndex = 1;
            } else if (currentDirection == Direction.DOWN) {
                dirIndex = 2;
            } else if (currentDirection == Direction.RIGHT) {
                dirIndex = 3;
            } else {
                dirIndex = 2;
            }
            
            HeroSprites activeSheet;
            if (currentState == State.ATTACKING && atkSprites != null) {
                activeSheet = atkSprites;
            } else {
                activeSheet = heroSprites;
            }
            BufferedImage frame = activeSheet.get(dirIndex, animFrame);
            if (frame != null) {
                g2.drawImage(frame, px - imgSize / 2, py - imgSize / 2, imgSize, imgSize, null);
                if (effectTimer > 0 && currentEffect != null) {
                    int iconSize = GameConfiguration.TILE_SIZE;
                    g2.drawImage(currentEffect, px - iconSize / 2+8, py - GameConfiguration.TILE_SIZE - iconSize -10, iconSize, iconSize, null);
                }
                return;
            }
        }    //in case img didnt load
        g2.setColor(team == 0 ? new Color(0, 150, 255) : new Color(255, 0, 150));
        g2.fillOval(px - size / 2, py - size / 2, size, size);
        g2.setColor(Color.BLACK);
        g2.drawOval(px - size / 2, py - size / 2, size, size);
    }

    public void respawn(double deltaTime) {
        if (!active) {
            respawnTimer -= deltaTime;
            if (respawnTimer <= 0) {
                onRespawn(); 
            }
        }
    }

    public void restoreMana(double amount) {
        mana += amount;
        if (mana > maxMana) mana = maxMana;
    }

    private void showEffect(BufferedImage img) {
        currentEffect = img;
        effectTimer = EFFECT_DURATION;
    }

    public void startRecall() {
        if (!active) return;
        currentState = State.IDLE;
        recalling = true;
        recallTimer = recallDuration;
    }

    @Override
    public void takeDamage(double damage) {
        interruptRecall();
        double reduction = defense / (defense + 100.0);
        double reduced = Math.max(1.0, damage * (1.0 - reduction));
        super.takeDamage(reduced);
        showEffect(effectAttacked);
    }

    protected void updateAnimation(double deltaTime) {

        if (attackAnimTimer > 0) {
            attackAnimTimer -= deltaTime;
            if (attackAnimTimer <= 0 && currentState == State.ATTACKING) { currentState = State.IDLE;}
        }
        if (currentState == State.IDLE) {
            animFrame = 0;
            animTimer = 0;
            return;
        }
        animTimer += deltaTime;
        if (animTimer >= FRAME_DURATION) {
            animTimer = 0;

            HeroSprites sheet;
            if (currentState == State.ATTACKING && atkSprites != null) {
                sheet = atkSprites;
            } else {
                sheet = heroSprites;
            }
            int totalFrames;
            if (sheet != null) {
                totalFrames = sheet.getFramesPerDirection();
            } else {
                totalFrames = 1;
            }
            animFrame = (animFrame + 1) % totalFrames;
        }
    }

    public void updateRecall(double deltaTime) {
        if (!recalling) return;
        recallTimer -= deltaTime;
        if (recallTimer <= 0) {
            recalling = false;
            this.x = (team == 0) ? GameConfiguration.START_X : GameConfiguration.START_Y; 
            this.y = (team == 0) ? GameConfiguration.START_Y : GameConfiguration.START_X; 
        }
    }

    public boolean upgradeSpell(int index) {
        if (skillPoints <= 0) return false;
        if (index < 0 || index >= spells.size()) return false;
        if (spells.get(index).getSpellLevel() >= Spell.MAX_LEVEL) return false;
        spells.get(index).upgrade();
        skillPoints--;
        return true;
    }

    public void updateTimers(double deltaTime) {
        if (stunTimer > 0) stunTimer -= deltaTime;
        if (effectTimer > 0) effectTimer -= deltaTime;
        for (int i = 0; i < spellCooldownTimers.length; i++) {
            if (spellCooldownTimers[i] > 0) spellCooldownTimers[i] -= deltaTime;
        }
    }

}