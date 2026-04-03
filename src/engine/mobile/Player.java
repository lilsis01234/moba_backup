package engine.mobile;

import org.apache.log4j.Logger;
import log.LoggerUtility;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.process.Arena;
import game_config.GameConfiguration;
import gui.Sprites.HeroSprites;

public class Player extends Personnage {

    private double CibleX, CibleY;
    private Entity targetEnemy = null; //we need it to attack found using a method

    private static final Logger logger = LoggerUtility.getLogger(Player.class, "text");
    private HeroSprites heroSprites;



    public Player(double maxHp, double maxMana, double speed, double atkRange) {
        super(GameConfiguration.PLAYER_START_X, GameConfiguration.PLAYER_START_Y, maxHp, 0, maxMana, speed);
        this.hp   = maxHp;
        this.mana = maxMana;
        this.atkDamage   = 20.0;
        this.atkRange    = atkRange;
        this.atkCooldown = 1.0;
        
    }

    public void update(double deltaTime, Arena arena) {
    	
        // mana regen  
        if (mana < maxMana) {
            mana += GameConfiguration.PLAYER_MANA_REGEN * deltaTime;
            if (mana > maxMana) mana = maxMana;
        }

        if (currentState == State.MOVING) {
            updatePosition(deltaTime, arena);
        }
            updateAnimation(deltaTime);

        // attack
        if (targetEnemy != null) {
            if (!targetEnemy.isActive()) {
                targetEnemy = null;
            } else {
                logger.debug("target=" + targetEnemy.getClass().getSimpleName()
                        + " dist=" + (int) getDistanceTo(targetEnemy)
                        + " range=" + atkRange
                        + " timer=" + (int) atkTimer);
                attack(targetEnemy, deltaTime);
            }
        }
    }

    
    public void updatePosition(double deltaTime, Arena arena) {
    	
        double dx = CibleX - x;
        double dy = CibleY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double moveStep = speed * deltaTime;

        // update direction 
        currentDirection = Direction.fromDelta((int) dx, (int) dy);

        if (distance < moveStep) {
            if (!arena.isCollidingWithWall(CibleX, CibleY)) {
            	currentState = State.MOVING;
                this.x = CibleX;
                this.y = CibleY;
            }
            currentState = State.IDLE;
        } else {
            double newX = x + (dx / distance) * moveStep;
            double newY = y + (dy / distance) * moveStep;
            if (!arena.isCollidingWithWall(newX, newY)) {
            	 currentState = State.MOVING;
                x = newX;
                y = newY;
            } else {
            	 currentState = State.IDLE;
            }
        }
    }

    @Override
    public void render(Graphics2D g2, int width, int height) {
    	if (!active) return;
    	renderSprite(g2);

    }

    public void moveTo(double xDestination, double yDestination) {
        this.CibleX = xDestination;
        this.CibleY = yDestination;
        this. currentState = State.MOVING;
    }

    @Override
    protected void onRespawn() {
        this.x = GameConfiguration.PLAYER_START_X;
        this.y = GameConfiguration.PLAYER_START_Y;
        this.hp = maxHp;
        this.mana = maxMana;
        this.active = true;
        currentState = State.IDLE;
    }

     	

    public double getX() { return x; }
    public double getY() { return y; }
    public double getCibleX() { return CibleX; }
    public double getCibleY() { return CibleY; }
    public State getState() { return currentState; }
    public void setTarget(Entity target) { this.targetEnemy = target; }
}