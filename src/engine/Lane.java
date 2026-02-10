package engine;

import java.awt.*;
import java.awt.geom.Path2D;

public class Lane {
    public enum Type { top, middle, bot }
    private Type type;

    public Lane(Type type) {
        this.type = type;
    }

    public void render(Graphics2D g2, int width, int height, int lane_width) {
        g2.setColor(Color.WHITE); 
        
        switch (this.type) {
            case top:
                g2.fillRect(0, 0, width, lane_width);
                g2.fillRect(0, 0, lane_width, height);
                break;

            case bot:
                g2.fillRect(0, height - lane_width, width, lane_width);
                g2.fillRect(width - lane_width, 0, lane_width, height);
                break;

            case middle:
                Path2D mid = new Path2D.Double();
                mid.moveTo(0, height);                         
                mid.lineTo(lane_width, height);                 
                mid.lineTo(width, lane_width);                  
                mid.lineTo(width, 0);                           
                mid.lineTo(width - lane_width, 0);             
                mid.lineTo(0, height - lane_width);             
                mid.closePath();
                g2.fill(mid);
                break;
        }
    }
}