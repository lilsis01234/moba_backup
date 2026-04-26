package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import game_config.GameConfiguration;
import java.util.List;
import log.LoggerUtility;

public class Base extends Entity {

    private static final Logger logger = LoggerUtility.getLogger(Base.class);

    private static BufferedImage AllyImg;
    private static BufferedImage EnemyImg;

    public Base(double x, double y, int team) {
        super(x, y, GameConfiguration.BASE_MAX_HP, team);
        this.atkDamage   = GameConfiguration.BASE_DAMAGE;
        this.atkRange    = GameConfiguration.BASE_RANGE;
        this.atkCooldown = GameConfiguration.BASE_ATTACK_COOLDOWN;
        logger.debug("Base created at (" + x + ", " + y + ") for team " + team + " with HP " + maxHp);
        if (AllyImg == null || EnemyImg == null) {
            try {
                AllyImg  = ImageIO.read(Base.class.getResourceAsStream("/res/BaseFountain/AllyBase.png"));
                EnemyImg = ImageIO.read(Base.class.getResourceAsStream("/res/BaseFountain/EnemyBase.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

       
    }
    
    public void update(double deltaTime, List<Entity> enemies) {
        if (!active) return;
        logger.debug("Base updating - HP: " + hp + "/" + maxHp + ", active: " + active);
        Entity closest = EntityUtils.findClosestNotOnTeam(this, enemies, team);
        if (closest != null) {
            logger.debug("Base attacking at distance " + getDistanceTo(closest));
            attack(closest, deltaTime);
        }
    }
}