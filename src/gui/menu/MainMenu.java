package gui.menu;

import gui.ButtonLayout;
import gui.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JPanel {

    private static final long serialVersionUID = 1L;
    private static MainMenu instance;

    public interface MenuListener {
        void onStartGame();
        void onExit();
    }

    private MenuListener listener;
    private int selectedIndex = 0;
    private int hoveredIndex = -1;
    private String[] menuItems = {"START GAME", "QUIT"};

    private MainMenu(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(Theme.BACKGROUND_DARK);
        setFocusable(true);
        setLayout(null);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    navigate(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    navigate(1);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleSelection();
                }
            }
        });

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

    public static MainMenu getInstance(Dimension screenSize) {
        if (instance == null) {
            instance = new MainMenu(screenSize);
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private void updateHoveredButton(int x, int y) {
        int w = getWidth(), h = getHeight();
        ButtonLayout[] btns = makeButtons(w, h);
        int newHovered = ButtonLayout.findButtonAt(x, y, btns);
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

    private ButtonLayout[] makeButtons(int w, int h) {
        ButtonLayout[] btns = new ButtonLayout[menuItems.length];
        for (int i = 0; i < btns.length; i++) {
            btns[i] = new ButtonLayout(w, h, i, 220, 50, 15, -50);
        }
        return btns;
    }

    public void setMenuListener(MenuListener listener) {
        this.listener = listener;
    }

    private void handleClick(int x, int y) {
        int w = getWidth(), h = getHeight();
        ButtonLayout[] btns = makeButtons(w, h);
        int clicked = ButtonLayout.findButtonAt(x, y, btns);
        if (clicked >= 0) {
            selectedIndex = clicked;
            handleSelection();
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

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

        GradientPaint gradient = new GradientPaint(0, 0, Theme.BACKGROUND_DARK, 0, getHeight(), new Color(10, 10, 20));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setFont(new Font("Serif", Font.BOLD, 52));
        FontMetrics fm = g2.getFontMetrics();
        String title = "MOBA";
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = getHeight() / 4;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(title, titleX + 4, titleY + 4);
        g2.setColor(Theme.ACCENT);
        g2.drawString(title, titleX, titleY);
        g2.setColor(Theme.ACCENT_BRIGHT);
        g2.fillRect(titleX - 20, titleY + 15, fm.stringWidth(title) + 40, 3);

        int w = getWidth(), h = getHeight();
        ButtonLayout[] btns = makeButtons(w, h);
        for (int i = 0; i < btns.length; i++) {
            ButtonLayout.renderButton(g2, btns[i], i == selectedIndex, menuItems[i]);
        }

        g2.setColor(Theme.TEXT_DIM);
        g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
        String ver = "v0.0.1 - Alpha";
        g2.drawString(ver, (getWidth() - g2.getFontMetrics().stringWidth(ver)) / 2, getHeight() - 25);
    }
}