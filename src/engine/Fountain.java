package engine;

import java.awt.*;

public class Fountain {
    private double x, y; 
    private double radius = 0.08; 
    private double healPerSecond = 20.0;

    private int dmg=20;

    public Fountain(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(double deltaTime, Player player) {
         double dx = player.getX() - x;
        double dy = player.getY() - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < radius) {
            player.heal(healPerSecond * deltaTime);
        }
    }

    public void render(Graphics2D g2, int width, int height, Color color) {
        int px = (int) (x * width);
        int py = (int) (y * height);
        int r = (int) (radius * width);
        g2.setColor(lowerTransperacy(color, 60));
        g2.fillOval(px - r, py - r, r * 2, r * 2);

        //dessin de la fontaine
        g2.setColor(color);
        int fSize = width / 30;
        g2.fillRect(px - fSize/2, py - fSize/2, fSize, fSize);
    }

    public Color lowerTransperacy(Color color, int trans){
        return (new Color(color.getRed(),color.getGreen(),color.getBlue(),trans) );
        }

    public void attack(Player player){  /*will change to hero later  */

        double dx = player.getX() - x;
        double dy = player.getY() - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if(dist<radius){player.takeDamage(dmg);}
    }

}