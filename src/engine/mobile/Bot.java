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

    private double spawnX, spawnY;
    private String name;
    
    
    private String heroName;

 

    private List<double[]> waypoints; 
    private int waypointIndex = 0;

    public Bot(double x, double y, List<double[]> waypoints, int team, String name, Hero hero) {
        super(x, y, team);
        this.spawnX    = x;
        this.spawnY    = y;
        this.waypoints = waypoints;
        this.name      = name;
        this.heroName  = hero.getName();
        loadFromHero(hero);
    }

    public void update(double deltaTime, List<Entity> enemies, List<Bot> allBots, ArrayList<Personnage> allPersonnages) {
        if (hp <= 0 && active) { die(); }
        if (!active) { super.respawn(deltaTime); return; }
        Move(deltaTime, enemies, allBots, allPersonnages);
    }
    
    public void Move(double deltaTime, List<Entity> enemies, List<Bot> allBots, ArrayList<Personnage> allPersonnages) {
        Entity target = EntityUtils.findClosest(this, enemies);
        if (target != null && getDistanceTo(target) <= atkRange) {
            currentState = State.IDLE;
            attack(target, deltaTime, allPersonnages);
        } else {
            boolean moved = followWaypoints(deltaTime, allBots);
            currentState = moved ? State.MOVING : State.IDLE;
        }
        updateAnimation(deltaTime);
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

    
    @Override
    protected void onRespawn() {
        if (waypoints != null && !waypoints.isEmpty()) {
            this.x = waypoints.get(0)[0];
            this.y = waypoints.get(0)[1];
            this.waypointIndex = 0; 
        }
        this.hp = maxHp;
        this.mana = maxMana;
        this.active = true;
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