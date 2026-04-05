package engine.mobile;

import org.apache.log4j.Logger;

import data.model.Hero;
import log.LoggerUtility;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import engine.process.Arena;
import game_config.GameConfiguration;
import gui.Sprites.HeroSprites;

public class Player extends Personnage {

    private double CibleX, CibleY;
    private Entity targetEnemy = null; //we need it to attack found using a method

    private static final Logger logger = LoggerUtility.getLogger(Player.class, "text");




    public Player(Hero hero) {
        super(GameConfiguration.START_X, GameConfiguration.START_Y, 0, hero);
        
    }

    public void update(double deltaTime, Arena arena, ArrayList<Personnage> allPersonnages) {
    	//mana
        if (mana < maxMana) {
            mana += GameConfiguration.PLAYER_MANA_REGEN * deltaTime;
            if (mana > maxMana) mana = maxMana;
        }
        //recalling 
        updateRecall(deltaTime);
        //moving

        if (currentState == State.MOVING) {
            updatePosition(deltaTime, arena);
        }
        updateAnimation(deltaTime);
        //attacking
        
        if (targetEnemy != null) {
            if (!targetEnemy.isActive()) {
                targetEnemy = null;
            } else {
                boolean fired = attack(targetEnemy, deltaTime, allPersonnages);
                if (fired) {
                    interruptRecall();
                    targetEnemy = null;
                }
            }
        }
    }

    
    public void updatePosition(double deltaTime, Arena arena) {
    	
        double dx = CibleX - x;
        double dy = CibleY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double moveStep = speed * deltaTime;

        // update direction 
        currentDirection = Direction.fromDelta((int) dx, (int) dy, currentDirection);

        if (distance < moveStep) {
            if (!arena.isCollidingWithWall(CibleX, CibleY)) {
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
    	
	    interruptRecall();
	    this.CibleX = xDestination;
	    this.CibleY = yDestination;
	    this.currentState = State.MOVING;
	    this.targetEnemy = null;
	
    }

     	

    public double getX() { return x; }
    public double getY() { return y; }
    public double getCibleX() { return CibleX; }
    public double getCibleY() { return CibleY; }
    public State getState() { return currentState; }
    public void setTarget(Entity target) { this.targetEnemy = target; }
}