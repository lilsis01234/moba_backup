package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import game_config.GameConfiguration;

public class Bot extends Personnage {

    private double spawnX, spawnY;
    private String name;
    private double respawnTimer = 15; // placeholder
    private BufferedImage AllyImage;
    private BufferedImage EnemyImage;

    private List<double[]> waypoints; // path
    private int waypointIndex = 0;

    private enum State { MOVING, FIGHTING, RETREATING } // for animations
    private State state = State.MOVING;

    public Bot(double x, double y, List<double[]> waypoints, int team, String name) {
        super(x, y, GameConfiguration.BOT_MAX_HP, team, GameConfiguration.BOT_MAX_MANA, GameConfiguration.BOT_SPEED);
        this.spawnX    = x;
        this.spawnY    = y;
        this.waypoints = waypoints;
        this.name      = name;
        this.maxMana   = GameConfiguration.BOT_MAX_MANA;
        this.mana      = this.maxMana;
        // attack stats
        this.atkDamage   = GameConfiguration.BOT_DAMAGE;
        this.atkRange    = GameConfiguration.BOT_RANGE;
        this.atkCooldown = 1.0;
        try {
            AllyImage =ImageIO.read(getClass().getResourceAsStream("/res/Heroes/Angel.png"));
            EnemyImage = ImageIO.read(getClass().getResourceAsStream("/res/Heroes/DemonLord.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(double deltaTime, List<Entity> enemies, List<Bot> allBots) {
        if (!active) {
            respawnTimer -= deltaTime;
            if (respawnTimer <= 0) respawn();
            return;
        }

        Entity target = findClosestEnemy(enemies);
        if (target != null && getDistanceTo(target) <= atkRange) {
            state = State.FIGHTING;
            attack(target, deltaTime);
        } else {
            state = State.MOVING;
            followWaypoints(deltaTime, allBots);
        }
    }
    public String getName() {return this.name; }

    @Override
    public void render(Graphics2D g2, int width, int height) {
        if (!active) return;

        int px   = (int) getX();
        int py   = (int) getY();
        int size = GameConfiguration.TILE_SIZE;
        
        int imgSize = size * 3; 

        if ((AllyImage != null) && (EnemyImage != null)) {
 
        	if (team == 0) {
        		g2.drawImage(AllyImage, px - imgSize/2, py - imgSize/2, imgSize, imgSize, null);
        	} else {
        		g2.drawImage(EnemyImage, px - imgSize/2, py - imgSize/2, imgSize, imgSize, null);
        	}
        	 
        
	    	}else { //if problem with the img, placeholder
			        // team color
		    		Color teamColor;
		    		if (team == 0) {
		    		    teamColor = new Color(0, 150, 255);
		    		} else {
		    		    teamColor = new Color(255, 0, 150);
		    		}
			
			        // placeholder, will use hero render later
			        g2.fillOval(px - size/2, py - size/2, size, size);
			        g2.setColor(Color.BLACK);
			        g2.drawOval(px - size/2, py - size/2, size, size);
			
			         g2.setFont(new Font("Arial", Font.BOLD, 12));
			         g2.drawString(name, px - 15, py - size/2 - 10);

    }
    }

    private Entity findClosestEnemy(List<Entity> enemies) {
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : enemies) {
            if (!e.isActive()) continue;
            double d = getDistanceTo(e);
            if (d < closestDist) { closestDist = d; closest = e; }
        }
        return closest;
    }

    private void followWaypoints(double deltaTime, List<Bot> allBots) {
        if (waypointIndex >= waypoints.size()) return;
        double[] wp = waypoints.get(waypointIndex);

        for (Bot other : allBots) {
            if (other == this || !other.isActive()) continue;
            double dx = other.getX() - wp[0];
            double dy = other.getY() - wp[1];
            if (Math.sqrt(dx*dx + dy*dy) < 30.0) return; // occupied, wait
        }

        double dx   = wp[0] - x;
        double dy   = wp[1] - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist < 8.0) {
            waypointIndex++; // epsilon, may not reach exact point
        } else {
            x += (dx / dist) * speed * deltaTime;
            y += (dy / dist) * speed * deltaTime;
        }
    }

    @Override
    public void respawn() {
        x = spawnX;
        y = spawnY;
        hp   = getMaxHp();
        mana = maxMana;
        active        = true;
        state         = State.MOVING;
        waypointIndex = 0;
        respawnTimer  = 15;
    }
}
