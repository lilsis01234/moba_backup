package engine.process;

import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import game_config.GameConfiguration;
import engine.mobile.Tower;

/**
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat
 */

public class Lane {
    public enum Type { top, middle, bot }
    private Type type;
    private List<Tower> towers;
    int T = GameConfiguration.TILE_SIZE;

    public Lane(Type type) {
        this.type = type;
        this.towers = new ArrayList<>();
        initTowers();
    }

    private void initTowers() {
        switch (type) {
        case top:
            towers.add(new Tower(6 * T, 44 * T, 0));  
            towers.add(new Tower(6 * T, 30 * T, 0));  
            towers.add(new Tower(6 * T, 15 * T, 0));  
            towers.add(new Tower(11 * T, 6 * T, 1));  
            towers.add(new Tower(26 * T, 6 * T, 1));   
            towers.add(new Tower(41 * T, 6 * T, 1));   
            break;

        case middle:
            towers.add(new Tower(14 * T, 46 * T, 0));
            towers.add(new Tower(20 * T, 40 * T, 0));
            towers.add(new Tower(26 * T, 34 * T, 0));
            towers.add(new Tower(34 * T, 26 * T, 1));
            towers.add(new Tower(40 * T, 20 * T, 1));
            towers.add(new Tower(46 * T, 14 * T, 1));
            break;

        case bot:
            towers.add(new Tower(17 * T, 54 * T, 0));  
            towers.add(new Tower(32 * T, 54 * T, 0)); 
            towers.add(new Tower(47 * T, 54 * T, 0));  
            towers.add(new Tower(54 * T, 49 * T, 1));  
            towers.add(new Tower(54 * T, 34 * T, 1));  
            towers.add(new Tower(54 * T, 19 * T, 1)); 
            break;
        }
    }

    public void render(Graphics2D g2, int width, int height, int lane_width) {
        g2.setColor(new Color(200, 200, 200));
        switch (this.type) {
            case top:
                g2.fillRect(0, 0, width, lane_width);
                g2.fillRect(0, 0, lane_width, height);
                break;
            case bot:
                g2.fillRect(0, height - lane_width, width, lane_width);
                g2.fillRect(width - lane_width, 0, lane_width, height);
                break;
            case middle:
                Path2D mid = new Path2D.Double();
                mid.moveTo(0, height);
                mid.lineTo(lane_width, height);
                mid.lineTo(width, lane_width);
                mid.lineTo(width, 0);
                mid.lineTo(width - lane_width, 0);
                mid.lineTo(0, height - lane_width);
                mid.closePath();
                g2.fill(mid);
                break;
        }

        for (Tower tower : towers) {
            tower.render(g2, width, height);
        }
    }

    public List<Tower> getAllTowers() {
        return towers;
    }

    public List<Tower> getAllyTowers() {
        List<Tower> allies = new ArrayList<>();
        for (Tower t : towers) {
            if (t.getTeam() == 0) {
                allies.add(t);
            }
        }
        return allies;
    }

    public List<Tower> getEnemyTowers() {
        List<Tower> enemies = new ArrayList<>();
        for (Tower t : towers) {
            if (t.getTeam() == 1) {
                enemies.add(t);
            }
        }
        return enemies;
    }

    public Type getType() {
        return type;
    }
}
