package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PauseMenu extends JPanel {
    
    public interface PauseMenuListener {
        void onResume();
        void onExit();
    }
    
    private PauseMenuListener listener;
    private int selectedIndex = 0;
    private boolean isVisible = false;
    private String[] menuItems = {"RESUME", "EXIT"};
    
    public PauseMenu() {
        setOpaque(false);
        setFocusable(false);
        setVisible(false);
        setLayout(null);
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isVisible) return;
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        selectedIndex = (selectedIndex - 1 + menuItems.length) % menuItems.length;
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        selectedIndex = (selectedIndex + 1) % menuItems.length;
                        repaint();
                        break;
                    case KeyEvent.VK_ENTER:
                        handleSelection();
                        break;
                    case KeyEvent.VK_ESCAPE:
                        hideMenu();
                        if (listener != null) listener.onResume();
                        break;
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isVisible) {
                    handleClick(e.getX(), e.getY());
                }
            }
        });
    }
    
    public void setPauseMenuListener(PauseMenuListener listener) {
        this.listener = listener;
    }
    
    public void showMenu(int screenWidth, int screenHeight) {
        isVisible = true;
        selectedIndex = 0;
        setSize(screenWidth, screenHeight);
        setVisible(true);
        repaint();
    }
    
    public void hideMenu() {
        isVisible = false;
        repaint();
    }
    
    public boolean isMenuVisible() {
        return isVisible;
    }
    
    @Override
    public boolean isVisible() {
        return isVisible;
    }
    
    private void handleSelection() {
        if (listener == null) return;
        
        switch (selectedIndex) {
            case 0:
                hideMenu();
                listener.onResume();
                break;
            case 1:
                hideMenu();
                listener.onExit();
                break;
        }
    }
    
    private void handleClick(int x, int y) {
        int w = getWidth();
        int h = getHeight();
        
        int menuWidth = 250;
        int menuHeight = 180;
        
        int btnWidth = 180;
        int btnHeight = 40;
        int spacing = 12;
        int startY = h / 2 - 30;
        
        for (int i = 0; i < menuItems.length; i++) {
            int btnX = (w - btnWidth) / 2;
            int btnY = startY + i * (btnHeight + spacing);
            
            if (x >= btnX && x <= btnX + btnWidth && y >= btnY && y <= btnY + btnHeight) {
                selectedIndex = i;
                handleSelection();
                return;
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!isVisible) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        int menuWidth = 250;
        int menuHeight = 180;
        int menuX = (getWidth() - menuWidth) / 2;
        int menuY = (getHeight() - menuHeight) / 2;
        
        g2.setColor(new Color(30, 30, 45, 240));
        g2.fillRect(menuX, menuY, menuWidth, menuHeight);
        
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(menuX, menuY, menuWidth, menuHeight);
        
        g2.setColor(new Color(150, 150, 200));
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        String title = "PAUSED";
        int titleX = menuX + (menuWidth - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, menuY + 35);
        
        int btnWidth = 180;
        int btnHeight = 40;
        int spacing = 12;
        int startY = menuY + 60;
        
        for (int i = 0; i < menuItems.length; i++) {
            int btnX = (getWidth() - btnWidth) / 2;
            int btnY = startY + i * (btnHeight + spacing);
            
            boolean isHovered = (i == selectedIndex);
            
            if (isHovered) {
                g2.setColor(new Color(60, 60, 80));
                g2.fillRect(btnX, btnY, btnWidth, btnHeight);
            }
            
            g2.setColor(isHovered ? new Color(150, 150, 200) : new Color(100, 100, 120));
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(btnX, btnY, btnWidth, btnHeight);
            
            g2.setColor(isHovered ? Color.WHITE : new Color(180, 180, 180));
            g2.setFont(new Font("Arial", Font.BOLD, 16));
            fm = g2.getFontMetrics();
            String text = menuItems[i];
            int textX = btnX + (btnWidth - fm.stringWidth(text)) / 2;
            int textY = btnY + (btnHeight + fm.getAscent()) / 2 - 2;
            g2.drawString(text, textX, textY);
        }
    }
}
