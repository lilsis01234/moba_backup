package engine.mobile;

import org.apache.log4j.Logger;

import data.model.Hero;
import log.LoggerUtility;
import java.awt.*;
import java.util.ArrayList;

import engine.process.Arena;
import game_config.GameConfiguration;

public class Player extends Personnage {

    private double targetX, targetY;
    private Entity targetEnemy = null;
    
    //visual purposes
    private double attackRadiusTimer = 0;
    private static final double ATTACK_RADIUS_DURATION = 0.5;

    private static final Logger logger = LoggerUtility.getLogger(Player.class);

    public Player(Hero hero) {
        super(GameConfiguration.START_X, GameConfiguration.START_Y, 0, hero);
    }

    public void update(double deltaTime, Arena arena, ArrayList<Personnage> allPersonnages) {
        if (mana < maxMana) {
            mana += GameConfiguration.PLAYER_MANA_REGEN * deltaTime;
            if (mana > maxMana) mana = maxMana;
        }
        
        addPassiveGold(GameConfiguration.PASSIVE_GOLD_PER_SECOND * deltaTime);
        
        if (attackRadiusTimer > 0) attackRadiusTimer -= deltaTime;
        
        updateRecall(deltaTime);
        updateTimers(deltaTime);

        if (currentState == State.MOVING) {
            updatePosition(deltaTime, arena);
        }
        updateAnimation(deltaTime);

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
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double moveStep = speed * deltaTime;

        currentDirection = Direction.fromDelta((int) dx, (int) dy, currentDirection);

        if (distance < moveStep) {
            if (!arena.isCollidingWithWall(targetX, targetY)) {
                this.x = targetX;
                this.y = targetY;
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
        if (attackRadiusTimer > 0) {
            int r = (int) atkRange;
            g2.setColor(new Color(255, 0, 0, 40));
            g2.fillOval((int)x - r, (int)y - r, r * 2, r * 2);
            g2.setColor(new Color(255, 0, 0, 120));
            g2.drawOval((int)x - r, (int)y - r, r * 2, r * 2);
        }
        renderSprite(g2);
    }

    public void moveTo(double xDestination, double yDestination) {
        interruptRecall();
        this.targetX = xDestination;
        this.targetY = yDestination;
        this.currentState = State.MOVING;
        this.targetEnemy = null;
    }
    //for the attack visual effect
    public void showAttackRadius() {
        attackRadiusTimer = ATTACK_RADIUS_DURATION;
    }
    
    public double getTargetX() { return targetX; }
    public double getTargetY() { return targetY; }
    public State getState() { return currentState; }
    public void setTarget(Entity target) { this.targetEnemy = target; }
    public Entity getTargetEnemy() { return targetEnemy; }
}