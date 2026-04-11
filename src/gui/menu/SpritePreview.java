package gui.menu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import gui.Theme;
import data.model.Hero;
import data.model.Category;
import engine.process.JsonDataProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpritePreview extends JPanel {
	
 private BufferedImage frame;

 SpritePreview(String spriteFile) {
     setOpaque(false);
     setPreferredSize(new Dimension(140, 140));
     setMaximumSize(new Dimension(140, 140));
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
         g.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
     } else {
         // grey shape
         g.setColor(new Color(80, 80, 100));
         g.fillOval(30, 10, 80, 80);
     }
 }
}