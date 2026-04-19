package gui.Sprites;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HeroSprites {
    private BufferedImage[][] sprites;
    private int frames;

   public HeroSprites(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) { 
                this.frames = 1;
                return;
            }

            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
            
            if (is == null) {
                System.err.println("File not found in classpath: " + filePath);
                this.frames = 1;
                return;
            }

            BufferedImage sheet = ImageIO.read(is);
            
            if (sheet == null) {  
                System.err.println("ImageIO.read returned null for: " + filePath);
                this.frames = 1;
                return;
            }

            int size = sheet.getHeight() / 4;
            this.frames = sheet.getWidth() / size;
            
            this.sprites = new BufferedImage[4][frames];

            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < frames; col++) {
                    sprites[row][col] = sheet.getSubimage(col * size, row * size, size, size);
                }
            }
        } catch (Exception e) {
            System.err.println("Error cutting sprites: " + e.getMessage());
            this.frames = 1;
        }
    }
    
    public BufferedImage get(int dirIndex, int frame) {
        if (sprites == null) return null;
        return sprites[dirIndex ][frame];
    }
    
    public int getFramesPerDirection() { return frames; }
}