package gui.menu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;


import javax.swing.*;
import java.awt.*;


public class SpritePreview extends JPanel {
	
 private BufferedImage frame;

 SpritePreview(String spriteFile) {
     setOpaque(false);
     setAlignmentX(Component.CENTER_ALIGNMENT);
     try {
         InputStream is = getClass().getClassLoader().getResourceAsStream(spriteFile);
         if (is != null) {
             BufferedImage sheet = ImageIO.read(is);
             int size = sheet.getHeight() / 4; 
             frame = sheet.getSubimage(0, 2 * size, size, size); 
         }
     } catch (Exception e) {}
 }
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (frame != null) {
        Graphics2D g2 = (Graphics2D) g;

        // BILINEAR pour lisser
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        // Antialiasing pour les contours
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int imgW = frame.getWidth();
        int imgH = frame.getHeight();
        
        // 80% de la zone disponible
        double margin = 0.8;
        double scale = Math.min((double) getWidth() * margin / imgW, (double) getHeight() * margin / imgH);
        
        int targetW = (int) (imgW * scale);
        int targetH = (int) (imgH * scale);

        int x = (getWidth() - targetW) / 2;
        int y = (getHeight() - targetH) / 2;

        g2.drawImage(frame, x, y, targetW, targetH, null);
    }
}
}