package engine;

import java.awt.*;

public class Bot {
    private double x, y;
 
     private double targetX, targetY;

    private double speed = 0.001;

    private double hp;
    private double MaxHp = 100;
    private double mana;
    private double MaxMana = 100; 

    private Color color;
    private String name;



    public Bot(double x, double y, double targetX, double targetY, Color color, String name) {
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;
        this.color = color;
        this.name = name;
        this.hp = MaxHp;
        this.mana = 20;
    }



    public void update() {
        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        if (distance > speed) {
            x = x + (dx / distance) * speed;
            y = y + (dy / distance) * speed;
        }
    }

    public void render(Graphics2D g2, int width, int height) {
        int px = (int) (x * width);
        int py = (int) (y * height);
        int size = width / 40;

        g2.setColor(color);
        g2.fillOval(px - size/2, py - size/2, size, size);
        g2.setColor(Color.WHITE);
        g2.drawOval(px - size/2, py - size/2, size, size);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString(name, px - 15, py - size/2 - 10);

        //barre de vie
        g2.setColor(Color.GRAY);
        g2.fillRect(px - size/2, py - size - 8, size, 4); 
        g2.setColor(Color.GREEN);

        int hpWidth = (int)((hp / MaxHp) * size);
        g2.fillRect(px - size/2, py - size - 8, hpWidth, 4); 
        g2.setColor(Color.BLACK);
        g2.drawRect(px - size/2, py - size - 8, size, 4); 

        // barre de mana
        g2.setColor(Color.GRAY);
        g2.fillRect(px - size/2, py - size - 15, size, 4); 
        g2.setColor(Color.CYAN);
        int manaWidth = (int)((mana / MaxMana) * size);
        g2.fillRect(px - size/2, py - size - 15, manaWidth, 4); 
        g2.setColor(Color.BLACK);
        g2.drawRect(px - size/2, py - size - 15, size, 4); 
    }

}