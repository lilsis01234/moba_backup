package engine.mobile;

import java.awt.*;

/**
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat
 */

public abstract class Entity {

    protected double x, y;
    protected double hp;
    protected double maxHp;
    protected boolean active = true;

    // attack stats
    protected double atkDamage;
    protected double atkRange;
    protected double atkCooldown;
    protected double atkTimer = 0;

    public Entity(double x, double y, double maxHp) {
        this.x = x;
        this.y = y;
        this.maxHp = maxHp;
        this.hp = maxHp;
    }

    public double getHp() { return hp; }
    public void setHp(double hp) { this.hp = hp; }
    public double getMaxHp() { return maxHp; }
    public void setMaxHp(double maxHp) { this.maxHp = maxHp; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public void heal(double amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    public void takeDamage(double dmg) {
        hp -= dmg;
        if (hp <= 0) {
            hp = 0;
            active = false;
        }
    }

    // handles cooldown, range check and damage in one place
    public void attack(Entity target, double deltaTime) {
        atkTimer -= deltaTime;
        if (atkTimer <= 0 && getDistanceTo(target) <= atkRange && target.isActive()) {
            target.takeDamage(atkDamage);
            atkTimer = atkCooldown;
        }
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public double getDistanceTo(Entity other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isActive() { return active; }

    public abstract void render(Graphics2D g2, int width, int height);

    protected void drawHealthBar(Graphics2D g2, int px, int py, int size, int yOffset) {
        g2.setColor(Color.GRAY);
        g2.fillRect(px - size/2, py - size - yOffset, size, 4);
        g2.setColor(Color.GREEN);
        int hpWidth = (int)((hp / maxHp) * size);
        g2.fillRect(px - size/2, py - size - yOffset, hpWidth, 4);
        g2.setColor(Color.BLACK);
        g2.drawRect(px - size/2, py - size - yOffset, size, 4);
    }
}
