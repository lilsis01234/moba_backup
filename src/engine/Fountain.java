package engine;

import java.awt.*;

public class Fountain {
    private double x, y; 
    private double radius = 0.15; 
    private double healPerSecond = 20.0;

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

    public void render(Graphics2D g2, int width, int height) {
        int px = (int) (x * width);
        int py = (int) (y * height);
        int r = (int) (radius * width);
        g2.setColor(new Color(50, 200, 255, 60));
        g2.fillOval(px - r, py - r, r * 2, r * 2);

        //dessin de la fontaine
        g2.setColor(new Color(50, 150, 255));
        int fSize = width / 30;
        g2.fillRect(px - fSize/2, py - fSize/2, fSize, fSize);
    }
}