package engine;

import java.awt.*;

public class Player {
    private double x, y;
    private double CibleX, CibleY;
    private boolean isMoving;

    private double speed = 0.2; 

    private double hp;
    private double MaxHp = 100;
    private double mana;
    private double MaxMana = 100; 
 
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.hp = 20;
        this.mana = 20; 
    }

    public void update(double deltaTime) {
        double manaRegenRate = 2.0; 
        if (mana < MaxMana) {
            mana += manaRegenRate * deltaTime; 
            if (mana > MaxMana) mana = MaxMana;
        }
        if (isMoving) {
            updatePosition(deltaTime);
        }
    }

    public void updatePosition(double deltaTime) {
        double dx = CibleX - x;
        double dy = CibleY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        
        double moveStep = speed * deltaTime;

        if (distance < moveStep) {
            this.x = CibleX;
            this.y = CibleY;
            isMoving = false;
        } else {
            x += (dx / distance) * moveStep;
            y += (dy / distance) * moveStep;
        }
    }

    public void render(Graphics2D g2, int width, int height) {
        int px = (int) (x * width);
        int py = (int) (y * height);
        int size = width / 40;

        g2.setColor(Color.BLACK);
        g2.fillOval(px - size/2, py - size/2, size, size);
        g2.setColor(Color.WHITE);
        g2.drawOval(px - size/2, py - size/2, size, size);

        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("Hero", px - 15, py - size/2 - 10);

        //barre de HP
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

    public void moveTo(double CibleX, double CibleY) {
        this.CibleX = CibleX;
        this.CibleY = CibleY;
        this.isMoving = true;
    }

    public void heal(double amount) {
        this.hp += amount;
        if (this.hp > MaxHp) {
            this.hp = MaxHp;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }
}