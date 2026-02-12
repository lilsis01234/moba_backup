package engine;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Arena {
    private List<Lane> lanes;
    private Player player;
    private List<Bot> bots;
    private List<Bot> enemyBots;
    private Fountain playerFountain;
    private Fountain Enemy_Fountain ;

    public Arena() {
        lanes = new ArrayList<>();
        lanes.add(new Lane(Lane.Type.top));
        lanes.add(new Lane(Lane.Type.middle));
        lanes.add(new Lane(Lane.Type.bot));

        player = new Player(0.1, 0.9);
        
        bots = new ArrayList<>();
        bots.add(new Bot(0.15, 0.87, 0.45, 0.6,new Color(0, 150, 255), "Bot1"));
        bots.add(new Bot(0.22, 0.97,  0.9, 0.95,new Color(0, 150, 255), "Bot2"));
        bots.add(new Bot(0.02, 0.85, 0.02, 0.20, new Color(0, 150, 255), "Bot3"));
        bots.add(new Bot(0.05, 0.85, 0.06, 0.15,new Color(0, 150, 255), "Bot4"));

        enemyBots = new ArrayList<>();
        enemyBots.add(new Bot(0.85, 0.13,0.55, 0.4,new Color(255, 0, 150), "ENEMY_Bot1"));
        enemyBots.add(new Bot(0.78, 0.1,0.10, 0.10, new Color(255, 0, 150), "ENEMY_Bot2"));
        enemyBots.add(new Bot(0.98, 0.15,0.98, 0.80,new Color(255, 0, 150), "ENEMY_Bot3"));
        enemyBots.add(new Bot(0.85, 0.20, 0.55, 0.5, new Color(255, 0, 150), "ENEMY_Bot4"));
        enemyBots.add(new Bot(0.95, 0.15,0.94, 0.85, new Color(255, 0, 150), "ENEMY_Bot5"));


        playerFountain = new Fountain(0.05, 0.95);
        Enemy_Fountain = new Fountain(0.95, 0.05);
    }

    public void update(double deltaTime) {
        player.update( deltaTime );

        for (Bot bot : bots) {
            bot.update();
        }
        for (Bot Ebot : enemyBots) {
             Ebot.update();
             Ebot.attack(player);
        }
        

        playerFountain.update(deltaTime, player);
        Enemy_Fountain.update(deltaTime, player); /* place holder, will add a hero class later and update the function */
        Enemy_Fountain.attack(player);

        for (Lane lane : lanes) {
            for (Tower t : lane.getEnemyTowers()) {
                 t.attack(player);
         }
        }
        
        if(player.isDead()){
            player.respawn();
        }
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

        playerFountain.render(g2, width, height,new Color(50, 200, 255));

        Enemy_Fountain.render(g2, width, height, new Color(202,94,90));

        player.render(g2, width, height);
        for(Bot bot :bots){
            bot.render(g2, width, height);
        }
        for (Bot bot : enemyBots) {
            bot.render(g2, width, height);
        }
    }

    public Player getPlayer() {
        return player;
    }
}
