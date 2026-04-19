package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import game_config.GameConfiguration;

public class Tower extends Entity {

    private static BufferedImage AllyImg;
    private static BufferedImage EnemyImg;

     public Tower(double x, double y, int team) {
         super(x, y, GameConfiguration.TOWER_MAX_HP,team);
         
         //loot 
         this.loot = GameConfiguration.GOLD_TOWER;
         this.XPloot = GameConfiguration.XP_TOWER;
         
         // attack stats
         this.atkDamage   = GameConfiguration.TOWER_DAMAGE;
         this.atkRange    = GameConfiguration.TOWER_RANGE;
         this.atkCooldown = 1.5;
         try {
             AllyImg  = ImageIO.read(Tower.class.getResourceAsStream("/res/towers/AllyTowers.png"));
             EnemyImg = ImageIO.read(Tower.class.getResourceAsStream("/res/towers/EnemyTowers.png"));
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

    @Override
    public void render(Graphics2D g2, int width, int height) {
        if (!active) return;

        int px = (int) getX();
        int py = (int) getY();
        int size = GameConfiguration.TILE_SIZE;

        int imgSize = size * 4; // tower was small
        BufferedImage img;
        if (team == 0) {
            img = AllyImg;
        } else {
            img = EnemyImg;
        }

        if (img != null) {
            g2.drawImage(img, px - imgSize/2, py - imgSize/2, imgSize, imgSize, null);
        } else {
            g2.setColor(team == 0 ? Color.BLUE : Color.RED);
            g2.fillRect(px - size/2, py - size/2, size, size);
            g2.setColor(Color.BLACK);
            g2.drawRect(px - size/2, py - size/2, size, size);
        }
   
    }

    public void update(double deltaTime, ArrayList<Entity> enemies) {
        if (!active) return;
        Entity closest = EntityUtils.findClosest(this, enemies);
        if (closest != null && closest.getTeam() != this.team) { 
            this.attack(closest, deltaTime);
        }
    }

    public boolean isAlly()  { return team == 0; }
    public boolean isEnemy() { return team == 1; }



}
