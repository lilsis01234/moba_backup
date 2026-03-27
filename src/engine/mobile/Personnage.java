package engine.mobile;

import java.awt.*;

public abstract class Personnage extends Entity {

    protected double speed;
    protected double mana;
    protected double maxMana;

    public Personnage(double x, double y, double maxHP, int team, double maxMana, double speed) {
        super(x, y, maxHP,team);
        this.speed = speed;
        this.maxMana = maxMana;
    }

	protected void drawManaBar(Graphics2D g2, int px, int py, int size, int yOffset) {
        g2.setColor(Color.GRAY);
        g2.fillRect(px - size/2, py - size - yOffset, size, 4);
        g2.setColor(Color.CYAN);
        int manaWidth = (int)((mana / maxMana) * size);
        g2.fillRect(px - size/2, py - size - yOffset, manaWidth, 4);
        g2.setColor(Color.BLACK);
        g2.drawRect(px - size/2, py - size - yOffset, size, 4);
    }

    public void heal(double amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    public void restoreMana(double amount) {
        mana += amount;
        if (mana > maxMana) mana = maxMana;
    }

    public double getSpeed() {
        return speed;
    }
    public int getTeam() {
        return team;
    }
    public double getMana() {
        return mana;
    }
    public double getMaxMana() {
        return maxMana;
    }
    public abstract void respawn();

    public abstract void render(Graphics2D g2, int width, int height);
}
