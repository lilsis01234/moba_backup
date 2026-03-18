package engine.map;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class Tile {

    public BufferedImage image;
    public boolean collision = false;

    public Color getColor() {
        if (image != null) {
            return new Color(image.getRGB(0, 0));
        }
        return Color.GRAY;
    }
}
