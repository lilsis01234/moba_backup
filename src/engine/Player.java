package engine;

import java.awt.*;

public class Player {
    private double x;  
    private double y;
    private double CibleX;
    private double CibleY;
    private boolean isMoving;
    private double speed = 0.002;
 
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public void moveTo(double CibleX, double CibleY){
        this.CibleX=CibleX;
        this.CibleY=CibleY;
        this.isMoving=true;

    }
    public void update(){
        if(isMoving){
             double dx=CibleX-x;
             double dy=CibleY-y;
             double distance = Math.sqrt(dx * dx + dy * dy);
             if(distance<speed){
                this.x=CibleX;
                this.y=CibleY;
                isMoving= false;
             }
             else{
                x=x+(dx/distance)*speed;
                y=y+(dy/distance)*speed;
             }

        }
    }
 

    public void render(Graphics2D g2, int width, int height) {

        
        int px = (int) (x * width);
        int py = (int) (y * height);
        int size = width / 40;

        g2.setColor(Color.BLACK);
        g2.fillOval(px - size/2, py - size/2, size, size);
        g2.setColor(Color.RED);
        g2.drawOval(px - size/2, py - size/2, size, size);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
