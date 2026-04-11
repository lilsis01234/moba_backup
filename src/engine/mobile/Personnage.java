package engine.mobile;
import data.model.Hero;
import data.model.Spell;
import java.awt.*;
import java.awt.image.BufferedImage;
import data.model.KDA;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import game_config.GameConfiguration;
import gui.Sprites.HeroSprites;

//krrp working on spell bug
//uh if no targer and cast?
//visual effects
//spell clicking
//spells on minions
//assist on towers / minions loot
//scaling


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
    
    protected enum State  {IDLE,MOVING,ATTACKING}; //can add attacking animation if we have time
    protected State currentState = State.IDLE;
    protected HeroSprites heroSprites;
    protected Direction currentDirection = Direction.DOWN;
    protected int animFrame = 0;
    protected double animTimer = 0;
    private static final double FRAME_DURATION = 0.12;
        
    //recall    
    private boolean recalling = false;
    private double recallTimer = 0;
    private double recallDuration=GameConfiguration.RECALL_DURATION;

    // spells
    protected List<Spell> spells = new ArrayList<>();
    protected double[] spellCooldownTimers = new double[3];
    private int skillPoints = 1;

    // stun
    private double stunTimer = 0;
    
    public Personnage(double x, double y, int team,Hero hero) {
        super(x, y, 1, team);
        this.loot   = GameConfiguration.GOLD_CHAR;
        this.XPloot = GameConfiguration.XP_CHAR; 
        this.currentState = State.IDLE;
        loadFromHero(hero);
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
        this.spells = new ArrayList<>(hero.getSpells());
        this.spellCooldownTimers = new double[3];
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

	public boolean attack(Entity target, double deltaTime, ArrayList<Personnage> allPersonnages) {
	    atkTimer -= deltaTime;
	    if (atkTimer <= 0 && getDistanceTo(target) <= atkRange && target.isActive()) {
	    	interruptRecall();
	        if (target instanceof Personnage) {
	            recordDamageDealtTo((Personnage) target);
	        }
	        target.takeDamage(atkDamage);
	        checkKill(target, allPersonnages);
	        atkTimer = atkCooldown;
	        return true;
	    }
	    return false;
	}
    
   private void checkKill(Entity target, ArrayList<Personnage> allPersonnages) {
	    if (!target.isActive()) {
	        this.addGold(target.getLoot());
	        this.addXp(target.getXPLoot());

	        if (target instanceof Personnage) {
		        this.kda.addKill();
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
    	if (level >= 15) return;
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
 
    protected void onRespawn() {
    	this.x= (team == 0) ? GameConfiguration.START_X : GameConfiguration.START_Y; 
        this.y= (team == 0) ? GameConfiguration.START_Y : GameConfiguration.START_X; 
        this.hp = maxHp;
        this.mana = maxMana;
        this.active = true;
        currentState = State.IDLE;
    }
    
    public void die() {
        this.active = false;
        this.hp = 0;
        this.respawnTimer = 5.0 + (this.getLevel() * 2.0);
        this.currentState = State.IDLE;
    }
    public void updateRecall(double deltaTime) {
        if (!recalling) return;
        recallTimer -= deltaTime;
        if (recallTimer <= 0) {
            recalling = false;
            this.x= (team == 0) ? GameConfiguration.START_X : GameConfiguration.START_Y; 
            this.y= (team == 0) ? GameConfiguration.START_Y : GameConfiguration.START_X; 
        }
    }

    public void updateTimers(double deltaTime) {
        if (stunTimer > 0) stunTimer -= deltaTime;
        for (int i = 0; i < spellCooldownTimers.length; i++) {
            if (spellCooldownTimers[i] > 0) spellCooldownTimers[i] -= deltaTime;
        }
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
    public boolean upgradeSpell(int index) {
        if (skillPoints <= 0) return false;
        if (index < 0 || index >= spells.size()) return false;
        if (spells.get(index).getSpellLevel() >= 5) return false;
        spells.get(index).upgrade();
        skillPoints--;
        return true;
    }
    public boolean isStunned() { return stunTimer > 0; }
    
    //this was impelmented to avoid the little bug or cheat of getting stunned by a stun of lower time while already being stunned and thus overriding it 
    public void applyStun(double seconds) { stunTimer = Math.max(stunTimer, seconds); }
    
    
    public void startRecall() {
        if (!active) return;
        currentState = State.IDLE;
        recalling = true;
        recallTimer = recallDuration;
    }
    public void interruptRecall() {
        recalling = false;
        recallTimer = 0;
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
    @Override
    public void takeDamage(double damage) {	
        interruptRecall();
        super.takeDamage(damage);
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
    public boolean isRecalling() { return recalling; }
    public double getRecallTimer() { return recallTimer; }
    public double getRecallDuration() { return recallDuration; }
    public List<Spell> getSpells() { return spells; }
    public double[] getSpellCooldownTimers() { return spellCooldownTimers; }
    public int getSkillPoints() { return skillPoints; }
}