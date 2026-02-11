package engine;

import java.awt.*;

public class Tower {
    private double positionX; 
    private double positionY; 
    private Color teamColor;

    public Tower(double positionX, double positionY, Color teamColor) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.teamColor = teamColor;
    }

    public void render(Graphics2D g2, int width, int height) {
        int x = (int) (positionX * width);
        int y = (int) (positionY * height);
        int size = width / 60;

        g2.setColor(teamColor);
        g2.fillOval(x - size/2, y - size/2, size, size); 
        g2.setColor(Color.BLACK);
        g2.drawOval(x - size/2, y - size/2, size, size); 
    }
    
}