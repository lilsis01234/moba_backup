package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import game_config.GameConfiguration;

public class Fountain extends Entity {

    private int team;
    private double healPerSecond = GameConfiguration.FOUNTAIN_HEAL_PER_SEC;
    private static BufferedImage AllyImg;
    private static BufferedImage EnemyImg;

    public Fountain(double x, double y, int team) {
        super(x, y, 100);
        this.team = team;
        this.atkDamage   = GameConfiguration.FOUNTAIN_DAMAGE;
        this.atkRange    = GameConfiguration.FOUNTAIN_RADIUS;
        this.atkCooldown = 0.5; // make global later
        try {
            AllyImg  = ImageIO.read(getClass().getResourceAsStream("/res/BaseFountain/AllyFountaine.png"));
            EnemyImg = ImageIO.read(getClass().getResourceAsStream("/res/BaseFountain/EnemyFountaine.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(double deltaTime, List<Entity> enemies, List<Entity> allies) {
        for (Entity e : allies) {
            if (e.isActive() && getDistanceTo(e) <= atkRange) {
                e.heal(healPerSecond * deltaTime);
                if (e instanceof Personnage) {
                    ((Personnage) e).restoreMana(healPerSecond * deltaTime);
                }
            }
        }
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : enemies) {
            if (!e.isActive()) continue;
            double d = getDistanceTo(e);
            if (d < closestDist) { closestDist = d; closest = e; }
        }
        if (closest != null) attack(closest, deltaTime);
    }

    @Override
    public void render(Graphics2D g2, int width, int height) {
        int px = (int) getX();
        int py = (int) getY();
        int r  = (int) atkRange;

        int size = GameConfiguration.TILE_SIZE;

        int imgSize = size * 4; // make fountain bigger
        BufferedImage img;
        if (team == 0) {
            img = AllyImg;
        } else {
            img = EnemyImg;
        }

        if (img != null) {
            g2.drawImage(img, px - imgSize/2, py - imgSize/2, imgSize, imgSize, null);
        } else {
            // radius circle
            g2.setColor(team == 0 ? new Color(50, 200, 255, 60) : new Color(202, 94, 90, 60));
            g2.fillOval(px - r, py - r, r * 2, r * 2);

            // base square
            g2.setColor(team == 0 ? new Color(50, 200, 255) : new Color(202, 94, 90));
            int fSize = GameConfiguration.TILE_SIZE * 2;
            g2.fillRect(px - fSize/2, py - fSize/2, fSize, fSize);
        }
    }
}
