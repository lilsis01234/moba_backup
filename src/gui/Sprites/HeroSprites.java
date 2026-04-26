package gui.Sprites;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

public class HeroSprites {
    private static final Logger logger = Logger.getLogger(HeroSprites.class.getName());
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
                logger.warning("File not found in classpath: " + filePath);
                this.frames = 1;
                return;
            }

            BufferedImage sheet = ImageIO.read(is);
            
            if (sheet == null) { 
                logger.warning("ImageIO.read returned null for: " + filePath); 
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
           logger.severe("Error cutting sprites: " + e.getMessage());
           this.frames = 1;
        }
    }
    
    public BufferedImage get(int dirIndex, int frame) {
        if (sprites == null) return null;
        return sprites[dirIndex ][frame];
    }
    
    
    public int getFramesPerDirection() { return frames; }
}