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
    private boolean isMoving;
    private Entity targetEnemy = null; //we need it to attack found using a method

    private static final Logger logger = LoggerUtility.getLogger(Player.class, "text");
    private HeroSprites heroSprites;

    // direction + animation
    private Direction currentDirection = Direction.DOWN;
    private int animFrame = 0;
    private double animTimer = 0;
    private static final double FRAME_DURATION = 0.12; // seconds per frame

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

        if (isMoving) {
            updatePosition(deltaTime, arena);
            
            animTimer += deltaTime;
            if (animTimer >= FRAME_DURATION) {
                animTimer = 0;
                int totalFrames = (heroSprites != null) ? heroSprites.getFramesPerDirection() : 1;
                animFrame = (animFrame + 1) % totalFrames;
            }
	        } else {
	            animFrame = 0;
	            animTimer = 0;
	        }

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

        // update direction from movement vector
        currentDirection = Direction.fromDelta((int) dx, (int) dy);

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
            } else {
                isMoving = false;
            }
        }
    }

    @Override
    public void render(Graphics2D g2, int width, int height) {
    	
        if (!active) return;
        int size = GameConfiguration.TILE_SIZE;
        int px = (int) x;
        int py = (int) y;
        int imgSize = size * 3;

        BufferedImage frame = null;
        if (heroSprites != null) {
            int dirIndex;
            switch (currentDirection) {
	            case UP:    dirIndex = 0; break;
	            case LEFT:  dirIndex = 1; break;
	            case DOWN:  dirIndex = 2; break;
	            case RIGHT: dirIndex = 3; break;
                default:    dirIndex = 0;
            }
            frame = heroSprites.get(dirIndex, animFrame);
            if (frame != null) {
                g2.drawImage(frame, px - imgSize / 2, py - imgSize / 2, imgSize, imgSize, null);
            } else {
 
                g2.setColor(Color.RED);
                g2.fillRect(px - size / 2, py - size / 2, size, size);
                g2.setColor(Color.BLACK);
                g2.drawRect(px - size / 2, py - size / 2, size, size);
            }
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

    public void loadHeroGraphics(String path) {
        this.heroSprites = new HeroSprites(path);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getCibleX() { return CibleX; }
    public double getCibleY() { return CibleY; }
    public boolean isMoving() { return isMoving; }
    public void setTarget(Entity target) { this.targetEnemy = target; }
}