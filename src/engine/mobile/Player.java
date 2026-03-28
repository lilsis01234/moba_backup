package engine.mobile;

import org.apache.log4j.Logger;
import log.LoggerUtility;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.process.Arena;
import game_config.GameConfiguration;

public class Player extends Personnage {
	


    private double CibleX, CibleY;
    private boolean isMoving;
    private Entity targetEnemy = null; //we need it to attack found using a method
    private BufferedImage playerImage;
    private static final Logger logger = LoggerUtility.getLogger(Player.class, "text");
    
    public Player(double x, double y, double maxHp, double maxMana, double speed, double atkRange) {
        super(GameConfiguration.PLAYER_START_X, GameConfiguration.PLAYER_START_Y,  maxHp, 0 , maxMana, speed);
        this.hp   = maxHp;
        this.mana = maxMana;
        this.atkDamage   = 20.0;
        this.atkRange    = atkRange;
        this.atkCooldown = 1.0;
        try {
            playerImage = ImageIO.read(getClass().getResourceAsStream("/res/Heroes/Green girl og.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(double deltaTime, Arena arena) {
        // mana regen
        if (mana < maxMana) {
            mana += GameConfiguration.PLAYER_MANA_REGEN * deltaTime;
            if (mana > maxMana) mana = maxMana;
        }
        if (isMoving) updatePosition(deltaTime, arena);

        // to attack
        if (targetEnemy != null) {
            if (!targetEnemy.isActive()) {
                targetEnemy = null; // it died
            } else {
            	logger.debug("target=" + targetEnemy.getClass().getSimpleName()
                        + " dist=" + (int)getDistanceTo(targetEnemy)
                        + " range=" + atkRange
                        + " timer=" + (int)atkTimer);
                attack(targetEnemy, deltaTime);
            }
        }


    }

    public void updatePosition(double deltaTime, Arena arena) {
        double dx = CibleX - x;
        double dy = CibleY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double moveStep = speed * deltaTime;

        if (distance < moveStep) {
            if (!arena.isCollidingWithWall(CibleX, CibleY)) {
                this.x = CibleX;
                this.y = CibleY;
            }
            isMoving = false;
        } else {
            double newX = x + (dx / distance) * moveStep;
            double newY = y + (dy / distance) * moveStep;
            if (!arena.isCollidingWithWall(newX, newY)) {
                x = newX;
                y = newY;
            } else { isMoving = false; }
        }
    }

    @Override
    public void render(Graphics2D g2, int width, int height) {
    	if (!active) return;
        int size = GameConfiguration.TILE_SIZE;
        int px = (int) x; // world coordinates
        int py = (int) y;
        
        int imgSize = size * 2; 

        if (playerImage != null) {
        	 g2.drawImage(playerImage, px - imgSize/2, py - imgSize/2, imgSize, imgSize, null);
        }
    }

    public void moveTo(double xDestination, double yDestination) {
        this.CibleX = xDestination;
        this.CibleY = yDestination;
        this.isMoving = true;
    }
    
  
    @Override
    protected void onRespawn() {
        this.x = GameConfiguration.PLAYER_START_X;
        this.y = GameConfiguration.PLAYER_START_Y;
        this.hp = maxHp;
        this.mana = maxMana;
        this.active = true;
        this.isMoving = false; 
    }

    

    public double getX() { return x; }
    public double getY() { return y; }
    public double getCibleX() { return CibleX; }
    public double getCibleY() { return CibleY; }
    public boolean isMoving() { return isMoving; }
    public void setTarget(Entity target) { this.targetEnemy = target; }
}
