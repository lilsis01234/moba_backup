package engine.mobile;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.process.Arena;
import game_config.GameConfiguration;

public class Player extends Personnage {

    private double CibleX, CibleY;
    private boolean isMoving;

    private BufferedImage playerImage;

    public Player(double x, double y, int maxHp, int maxMana, double speed) {
        super(GameConfiguration.PLAYER_START_X, GameConfiguration.PLAYER_START_Y,  maxHp, maxMana, speed, 0);
        this.hp   = maxHp;
        this.mana = maxMana;
        this.atkDamage   = 20.0;
        this.atkRange    = 100.0;
        this.atkCooldown = 1.0;
        try {
            playerImage = ImageIO.read(getClass().getResourceAsStream("/res/Heroes/Green girl og.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(double deltaTime, Arena arena) {
        // mana regen
        if (mana < maxMana) {
            mana += GameConfiguration.PLAYER_MANA_REGEN * deltaTime;
            if (mana > maxMana) mana = maxMana;
        }
        if (isMoving) {
            System.out.println("Pos actuelle: " + x + "," + y + " | Cible: " + CibleX + "," + CibleY);
        }
        if (isMoving) updatePosition(deltaTime, arena);
    }

    public void updatePosition(double deltaTime, Arena arena) {
        double dx = CibleX - x;
        double dy = CibleY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        double moveStep = speed * deltaTime;

        if (distance < moveStep) {
            if (!arena.isCollidingWithWall(CibleX, CibleY)) {
                this.x = CibleX;
                this.y = CibleY;
            }
            isMoving = false;
        } else {
            double newX = x + (dx / distance) * moveStep;
            double newY = y + (dy / distance) * moveStep;
            if (!arena.isCollidingWithWall(newX, newY)) {
                x = newX;
                y = newY;
            } else { isMoving = false; }
        }
    }

    @Override
    public void render(Graphics2D g2, int width, int height) {
        int size = GameConfiguration.TILE_SIZE;
        int px = (int) x; // world coordinates
        int py = (int) y;

        if (playerImage != null) {
            g2.drawImage(playerImage, px - size/2, py - size/2, size, size, null);
        }
    }

    public void moveTo(double xDestination, double yDestination) {
        this.CibleX = xDestination;
        this.CibleY = yDestination;
        this.isMoving = true;
    }

    @Override
    public void respawn() {
        hp     = maxHp;
        mana   = maxMana;
        x      = GameConfiguration.PLAYER_START_X;
        y      = GameConfiguration.PLAYER_START_Y;
        active = true;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getCibleX() { return CibleX; }
    public double getCibleY() { return CibleY; }
    public boolean isMoving() { return isMoving; }
}
