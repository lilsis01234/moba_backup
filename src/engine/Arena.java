package engine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    private List<Lane> lanes;
    private Player player;
    private List<Bot> bots;
    private Fountain playerFountain;

    public Arena() {
        lanes = new ArrayList<>();
        lanes.add(new Lane(Lane.Type.top));
        lanes.add(new Lane(Lane.Type.middle));
        lanes.add(new Lane(Lane.Type.bot));

        player = new Player(0.1, 0.9);
        
        bots = new ArrayList<>();
        bots.add(new Bot(0.15, 0.87, 0.45, 0.6, Color.RED, "Bot1"));
        bots.add(new Bot(0.22, 0.97,  0.9, 0.95,Color.ORANGE, "Bot2"));
        bots.add(new Bot(0.02, 0.85, 0.02, 0.20, Color.YELLOW, "Bot3"));
        bots.add(new Bot(0.05, 0.85, 0.06, 0.15, Color.PINK, "Bot4"));
        playerFountain = new Fountain(0.05, 0.95);
    }

    public void update(double deltaTime) {
        player.update( deltaTime );

        for (Bot bot : bots) {
        bot.update();

     }
        playerFountain.update(deltaTime, player);
    }
    

    public void render(Graphics2D g2, int width, int height) {
        // fond
        g2.setColor(new Color(20, 80, 20));
        g2.fillRect(0, 0, width, height);

        // lanes
        int lane_width = width / 12; 
        for (Lane lane : lanes) {
            lane.render(g2, width, height, lane_width);
        }

        g2.setColor(new Color(0, 100, 0));
        double[][] positionsArbres = {
            {0.16, 0.2}, {0.33, 0.25}, {0.5, 0.16}, {0.2, 0.5}, {0.5, 0.5}
        };
        
        int aW = width / 60; 
        int aH = height / 30;

        for (double[] pos : positionsArbres) {
            g2.fillRect((int)(pos[0] * width), (int)(pos[1] * height), aW, aH);
        }
        playerFountain.render(g2, width, height);
        player.render(g2, width, height);
        for(Bot bot :bots){
            bot.render(g2, width, height);
        }
    }

    public Player getPlayer() {
        return player;
    }
}
