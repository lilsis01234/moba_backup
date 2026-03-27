package gui.menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JPanel {
    
    private static final long serialVersionUID = 1L;

	public interface MenuListener {
        void onStartGame();
        void onExit();
    }
    
    private MenuListener listener;
    private int selectedIndex = 0;
    private int hoveredIndex = -1;
    private String[] menuItems = {"DEMARRER LE JEU", "QUITTER"};

    private final Color BACKGROUND_DARK = new Color(20, 20, 30);
    private final Color ACCENT = new Color(180, 140, 90);       
    private final Color ACCENT_BRIGHT = new Color(220, 180, 120);
    private final Color BUTTON_BG = new Color(40, 35, 50);
    private final Color BUTTON_HOVER = new Color(60, 50, 70);
    private final Color BUTTON_BORDER = new Color(100, 90, 70);
    private final Color TEXT_MAIN = new Color(240, 230, 200);
    private final Color TEXT_DIM = new Color(160, 150, 130);

    public MainMenu(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(BACKGROUND_DARK);
        setFocusable(true);
        setLayout(null);
        
        // Gestion clavier
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
            	switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:    navigate(-1); break;
                case KeyEvent.VK_DOWN:  navigate(1); break;
                case KeyEvent.VK_ENTER: handleSelection(); break;
            }
            }
        });
        
        // Gestion Souris
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateHoveredButton(e.getX(), e.getY());
            }
        });
    }
    
    private void updateHoveredButton(int x, int y) {
        int w = getWidth(), h = getHeight();
        int btnWidth = 220, btnHeight = 50, spacing = 15;
        int startY = h / 2 - 50;
        
        int newHovered = -1;
        for (int i = 0; i < menuItems.length; i++) {
            int btnX = (w - btnWidth) / 2;
            int btnY = startY + i * (btnHeight + spacing);
            
            if (x >= btnX && x <= btnX + btnWidth && y >= btnY && y <= btnY + btnHeight) {
                newHovered = i;
                break;
            }
        }
        
        if (newHovered != hoveredIndex) {
            hoveredIndex = newHovered;
            setCursor(newHovered >= 0 ? new Cursor(Cursor.HAND_CURSOR) : new Cursor(Cursor.DEFAULT_CURSOR));
            repaint();
        }
    }

    private void navigate(int dir) {
        selectedIndex = (selectedIndex + dir + menuItems.length) % menuItems.length;
        repaint();
    }
    
    public void setMenuListener(MenuListener listener) {
        this.listener = listener;
    }
    
    private void handleClick(int x, int y) {
        int w = getWidth(), h = getHeight();
        int btnWidth = 220, btnHeight = 50, spacing = 15;
        int startY = h / 2 - 50;
        
        for (int i = 0; i < menuItems.length; i++) {
            int btnX = (w - btnWidth) / 2;
            int btnY = startY + i * (btnHeight + spacing);
            
            if (x >= btnX && x <= btnX + btnWidth && y >= btnY && y <= btnY + btnHeight) {
                selectedIndex = i;
                handleSelection();
            }
        }
    }
    
    private void handleSelection() {
        if (listener == null) return;
        if (selectedIndex == 0) listener.onStartGame();
        else if (selectedIndex == 1) listener.onExit();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Désactiver l'anti-aliasing pour un look "Pixel" net
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
 
        GradientPaint gradient = new GradientPaint(0, 0, BACKGROUND_DARK, 0, getHeight(), new Color(10, 10, 20));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font("Serif", Font.BOLD, 52));
        FontMetrics fm = g2.getFontMetrics();
        String title = "MOBA";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = getHeight() / 4;
     
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(title, titleX + 4, titleY + 4);
        g2.setColor(ACCENT);
        g2.drawString(title, titleX, titleY);
        g2.setColor(ACCENT_BRIGHT);
        g2.fillRect(titleX - 20, titleY + 15, fm.stringWidth(title) + 40, 3);

        // BOUTONS
        int btnWidth = 220, btnHeight = 50, spacing = 15;
        int startY = getHeight() / 2 - 50;
        
        for (int i = 0; i < menuItems.length; i++) {
            boolean isSelected = (i == selectedIndex);
            int btnX = (getWidth() - btnWidth) / 2;
            int btnY = startY + i * (btnHeight + spacing);

            if (isSelected) {
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRect(btnX + 4, btnY + 4, btnWidth, btnHeight);
            }

            g2.setColor(isSelected ? BUTTON_HOVER : BUTTON_BG);
            g2.fillRect(btnX, btnY, btnWidth, btnHeight);

            g2.setColor(isSelected ? ACCENT : BUTTON_BORDER);
            int b = 2; // épaisseur bordure
            g2.fillRect(btnX, btnY, btnWidth, b);              // haut
            g2.fillRect(btnX, btnY + btnHeight - b, btnWidth, b); // bas
            g2.fillRect(btnX, btnY, b, btnHeight);             // gauche
            g2.fillRect(btnX + btnWidth - b, btnY, b, btnHeight); // droite

            g2.setColor(isSelected ? TEXT_MAIN : TEXT_DIM);
            g2.setFont(new Font("SansSerif", Font.BOLD, 18));
            FontMetrics btnFm = g2.getFontMetrics();
            String txt = menuItems[i];
            g2.drawString(txt, btnX + (btnWidth - btnFm.stringWidth(txt)) / 2, btnY + (btnHeight + btnFm.getAscent()) / 2 - 4);
        }

        // 4. FOOTER (Version)
        g2.setColor(TEXT_DIM);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        String ver = "v0.0.1 - Alpha";
        g2.drawString(ver, (getWidth() - g2.getFontMetrics().stringWidth(ver)) / 2, getHeight() - 25);
    }
}