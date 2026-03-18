package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import config.GameConfiguration;

public class Minion extends Entity {

    private double speed;
    private int team; // 0 = ALLY, 1 = ENEMY

    // path
    private List<double[]> waypoints;
    private int waypointIndex = 0;
    private BufferedImage AllyImg;
    private BufferedImage EnemyImg;

    public Minion(double x, double y, int team, List<double[]> waypoints) {
        super(x, y, GameConfiguration.MINION_MAX_HP);
        this.speed = GameConfiguration.MINION_SPEED;
        this.team  = team;
        this.waypoints = waypoints;
        // attack stats
        this.atkDamage = GameConfiguration.MINION_DMG;
        this.atkRange = GameConfiguration.MINION_RANGE;
        this.atkCooldown = GameConfiguration.MINION_ATTACK_COOLDOWN;
        try {
            AllyImg  = ImageIO.read(getClass().getResourceAsStream("/res/minions/AllyMinions.png"));
            EnemyImg = ImageIO.read(getClass().getResourceAsStream("/res/minions/EnemyMinions.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(double deltaTime, List<Entity> enemies) {
        if (!active) return;
        Entity target = findTarget(enemies);
        if (target != null && getDistanceTo(target) <= atkRange) {
            attack(target, deltaTime);
        } else {
            followWaypoints(deltaTime);
        }
    }

    private Entity findTarget(List<Entity> enemies) {
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : enemies) {
            if (!e.isActive()) continue;
            double dist = getDistanceTo(e);
            if (dist < closestDist) { closestDist = dist; closest = e; }
        }
        return closest;
    }

    private void followWaypoints(double deltaTime) {
        if (waypointIndex >= waypoints.size()) return;

        double[] wp = waypoints.get(waypointIndex);
        double dx = wp[0] - x;
        double dy = wp[1] - y;
        double dist = Math.sqrt(dx*dx + dy*dy);

        if (dist < 5.0) {
            waypointIndex++; // reached waypoint, go to next
        } else {
            x += (dx / dist) * speed * deltaTime;
            y += (dy / dist) * speed * deltaTime;
        }
    }

    @Override
    public void render(Graphics2D g2, int width, int height) {
        if (!active) return;
        int px   = (int) x;
        int py   = (int) y;
        int size = GameConfiguration.TILE_SIZE / 2;

        BufferedImage img;
        if (team == 0) {
            img = AllyImg;
        } else {
            img = EnemyImg;
        }
        if (img != null) {
            g2.drawImage(img, px - size, py - size, size * 2, size * 2, null);
         } else {
             // draw the polygon like b4
             if(team==0) {
                 g2.setColor(new Color(0, 150, 255));
             }else {
                 g2.setColor(new Color(255, 80, 80));
             }
             int[] xPoints = {px - size, px + size, px};
             int[] yPoints = {py + size, py + size, py - size};
             g2.fillPolygon(xPoints, yPoints, 3);
             g2.setColor(Color.BLACK);
             g2.drawPolygon(xPoints, yPoints, 3);
         }
     }

    public int getTeam() { return team; }
    public boolean isTooCloseTo(Minion other) {
        double dx = other.getX() - x;
        double dy = other.getY() - y;
        return Math.sqrt(dx*dx + dy*dy) < 20.0;
    }
}
