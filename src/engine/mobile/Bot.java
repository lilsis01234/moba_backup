package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import data.model.Hero;
import game_config.GameConfiguration;

public class Bot extends Personnage {


    private String name;      
    private String heroName;

 

    private List<double[]> waypoints; 
    private int waypointIndex = 0;
    private boolean retreating = false;
    private double retreatSafeRadius=GameConfiguration.RETREAT_SAFE_RADIUS;
    private double retreatTresHold=GameConfiguration.RETREAT_HP_THRESHOLD;

    public Bot(List<double[]> waypoints, int team, String name, Hero hero) {
        super(
            (team == 0) ? GameConfiguration.START_X : GameConfiguration.START_Y, //if ally x=startx else x=starty
            (team == 0) ? GameConfiguration.START_Y : GameConfiguration.START_X, 
            team, 
            hero
        );
        this.waypoints = waypoints;
        this.name      = name;
        this.heroName  = hero.getName();
    }

    public void update(double deltaTime, List<Entity> enemies, List<Bot> allBots, ArrayList<Personnage> allPersonnages) {
    	//die and respawn
        if (hp <= 0 && active) { die(); }
        if (!active) { super.respawn(deltaTime); return; }
        updateTimers(deltaTime);
        if (isStunned()) { updateAnimation(deltaTime); return; }
        //move
        Move(deltaTime, enemies, allBots, allPersonnages);
        //recall
        updateRecall(deltaTime);
    }
    
    public void Move(double deltaTime, List<Entity> enemies, List<Bot> allBots, ArrayList<Personnage> allPersonnages) {
        double hpPercent = hp / maxHp;
        
        if (hpPercent < retreatTresHold) {retreating = true;}
        else {retreating = false;}
        
        if (retreating) { handleRetreat(deltaTime, enemies, allBots);}
        
        else {
        	Entity target = EntityUtils.findClosest(this, enemies);
	        if (target != null && getDistanceTo(target) <= atkRange) {
	            currentState = State.IDLE;
	            attack(target, deltaTime, allPersonnages);
	        } else {
	            boolean moved = followWaypoints(deltaTime, allBots);
	            currentState = moved ? State.MOVING : State.IDLE;
	        }
	    }
	        updateAnimation(deltaTime);
	        
    }
        
    private void handleRetreat(double deltaTime, List<Entity> enemies, List<Bot> allBots) {
       
        double fX = (team == 0) ? GameConfiguration.START_X : GameConfiguration.START_Y;
        double fY = (team == 0) ? GameConfiguration.START_Y : GameConfiguration.START_X;

        double distToFountain = Math.sqrt(Math.pow(fX - x, 2) + Math.pow(fY - y, 2));
        Entity closestEnemy = EntityUtils.findClosest(this, enemies);
        double distToEnemy = (closestEnemy != null) ? getDistanceTo(closestEnemy) : Double.MAX_VALUE;

        //close to fountain aka just walk
        if (distToFountain <= retreatSafeRadius) {
            interruptRecall(); 
            moveToPoint(fX, fY, deltaTime);
        } 
        // no enemies recall
        else if (distToEnemy > retreatSafeRadius) { 
            if (!isRecalling()) {startRecall();  }
        } 
        // enemies around  retreat
        else {
            interruptRecall(); 
            followWaypointsReverse(deltaTime, allBots);
        }
    }
    private void moveToPoint(double targetX, double targetY, double deltaTime) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        if (dist > 5.0) {
            x += (dx / dist) * speed * deltaTime;
            y += (dy / dist) * speed * deltaTime;
            currentDirection = Direction.fromDelta((int) dx, (int) dy, currentDirection);
            currentState = State.MOVING;
        } else {
            currentState = State.IDLE;
        }
    }

    private void followWaypointsReverse(double deltaTime, List<Bot> allBots) {
        if (waypointIndex <= 0) {
            
            moveToPoint((team == 0) ? GameConfiguration.START_X : GameConfiguration.START_Y, 
                        (team == 0) ? GameConfiguration.START_Y : GameConfiguration.START_X, deltaTime);
            return;
        }
        
        double[] wp = waypoints.get(waypointIndex - 1); 
        double dx = wp[0] - x;
        double dy = wp[1] - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 8.0) {
            waypointIndex--; 
        } else {
            x += (dx / dist) * speed * deltaTime;
            y += (dy / dist) * speed * deltaTime;
            currentDirection = Direction.fromDelta((int) dx, (int) dy, currentDirection);
            currentState = State.MOVING;
        }
    }
  
    
    public String getName() {return this.name; }

    @Override
    public void render(Graphics2D g2, int width, int height) {
        if (!active) return;
        renderSprite(g2); 
        
        // name label 
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString(heroName, (int) x - 15, (int) y - GameConfiguration.TILE_SIZE * 2);
     }

    private boolean followWaypoints(double deltaTime, List<Bot> allBots) {
    	
        if (waypointIndex >= waypoints.size()) return false;
        double[] wp = waypoints.get(waypointIndex);
        
        double dx = wp[0] - x;
        double dy = wp[1] - y;
        
        // update direction 
        currentDirection = Direction.fromDelta((int) dx, (int) dy, currentDirection);
        
        //check for other bots to avoid collision
        for (Bot other : allBots) {
            if (other == this || !other.isActive()) continue;
            double odx = other.getX() - wp[0];
            double ody = other.getY() - wp[1];
            if (Math.sqrt(odx*odx + ody*ody) < GameConfiguration.TILE_SIZE * 0.6) return false; // occupied, wait
        }

        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist < 8.0) {
            waypointIndex++; // epsilon, may not reach exact point
            return false;
        } else {
            x += (dx / dist) * speed * deltaTime;
            y += (dy / dist) * speed * deltaTime;
            return true;
        }
    }
    
    
    public double getRespawnTimer() { return respawnTimer; }
    
}