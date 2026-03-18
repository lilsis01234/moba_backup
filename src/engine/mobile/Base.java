package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import config.GameConfiguration;

import java.util.List;

public class Base extends Entity {

    private int team;
    private static BufferedImage AllyImg;
    private static BufferedImage EnemyImg;

    public Base(double x, double y, int team) {
        super(x, y, GameConfiguration.BASE_MAX_HP);
        this.team        = team;
        this.atkDamage   = GameConfiguration.BASE_DAMAGE;
        this.atkRange    = GameConfiguration.BASE_RANGE;
        this.atkCooldown = 2.0;

        if (AllyImg == null || EnemyImg == null) {
            try {
                AllyImg  = ImageIO.read(Base.class.getResourceAsStream("/res/BaseFountain/AllyBase.png"));
                EnemyImg = ImageIO.read(Base.class.getResourceAsStream("/res/BaseFountain/EnemyBase.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getTeam() { return team; }

    @Override
    public void render(Graphics2D g2, int width, int height) {
        if (!active) return;
        int px     = (int) getX();
        int py     = (int) getY();
        int size   = GameConfiguration.TILE_SIZE;
        int imgSize = size * 6;

        BufferedImage img;
        if (team == 0) {
            img = AllyImg;
        } else {
            img = EnemyImg;
        }
        if (img != null) {
            g2.drawImage(img, px - imgSize/2, py - imgSize/2, imgSize, imgSize, null);
        } else {
        	if (team == 0) {
        	    g2.setColor(Color.BLUE);
        	} else {
        	    g2.setColor(Color.RED);
        	}
            g2.fillRect(px - imgSize/2, py - imgSize/2, imgSize, imgSize);
            g2.setColor(Color.BLACK);
            g2.drawRect(px - imgSize/2, py - imgSize/2, imgSize, imgSize);
        }

        // hp bar
        g2.setColor(Color.GRAY);
        g2.fillRect(px - imgSize/2, py - imgSize/2 - 8, imgSize, 5);
        g2.setColor(Color.GREEN);
        g2.fillRect(px - imgSize/2, py - imgSize/2 - 8, (int)((hp / maxHp) * imgSize), 5);
        g2.setColor(Color.BLACK);
        g2.drawRect(px - imgSize/2, py - imgSize/2 - 8, imgSize, 5);
    }
    
    public void update(double deltaTime, List<Entity> enemies) {
        if (!active) return;
        Entity closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity e : enemies) {
            if (!e.isActive()) continue;
            double d = getDistanceTo(e);
            if (d < closestDist) { closestDist = d; closest = e; }
        }
        if (closest != null) attack(closest, deltaTime);
    }
}