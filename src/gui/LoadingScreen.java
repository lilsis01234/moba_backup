package gui;

import javax.swing.*;
import java.awt.*;

public class LoadingScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    private String message;
    private int dotCount;
    private Color backgroundDark = new Color(20, 20, 30);
    private Color accent = new Color(180, 140, 90);
    private Color accentBright = new Color(220, 180, 120);
    private Color buttonBg = new Color(40, 35, 50);
    private Color buttonBorder = new Color(100, 90, 70);
    private Color textMain = new Color(240, 230, 200);
    private Color textDim = new Color(160, 150, 130);

    public LoadingScreen(int width, int height) {
        System.out.println("LoadingScreen created: " + width + "x" + height);
        setPreferredSize(new Dimension(width, height));
        setBackground(backgroundDark);
        setLayout(null);
        setOpaque(true);
        message = "Initializing game...";
        dotCount = 0;
        
        System.out.println("LoadingScreen: calling first repaint");
        repaint();
        System.out.println("LoadingScreen: first repaint done");

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                    dotCount = (dotCount + 1) % 4;
                    System.out.println("Thread calling repaint, dotCount=" + dotCount);
                    repaint();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    public void setMessage(String msg) {
        this.message = msg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = getWidth();
        int h = getHeight();
        
        if (w <= 0 || h <= 0) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundDark);
        g2.fillRect(0, 0, w, h);
        

        g2.setFont(new Font("Serif", Font.BOLD, 72));
        String title = "MOBA";
        int titleWidth = g2.getFontMetrics().stringWidth(title);
        int titleX = (w - titleWidth) / 2;
        int titleY = h / 3 - 30;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(title, titleX + 4, titleY + 4);
        g2.setColor(accent);
        g2.drawString(title, titleX, titleY);
        g2.setColor(accentBright);
        g2.fillRect(titleX - 20, titleY + 15, titleWidth + 40, 3);

        int barWidth = 400;
        int barHeight = 20;
        int barX = (w - barWidth) / 2;
        int barY = h / 2;

        g2.setColor(buttonBg);
        g2.fillRect(barX, barY, barWidth, barHeight);

        g2.setColor(buttonBorder);
        g2.drawRect(barX, barY, barWidth, barHeight);

        int loadingWidth = (int) ((barWidth - 10) * 0.7);
        g2.setColor(accent);
        g2.fillRect(barX + 5, barY + 4, loadingWidth, barHeight - 8);

        String dots = "";
        for (int i = 0; i < dotCount; i++) dots += ".";
        String fullMessage = message + dots;

        g2.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g2.setColor(textMain);
        int msgWidth = g2.getFontMetrics().stringWidth(fullMessage);
        g2.drawString(fullMessage, (w - msgWidth) / 2, barY + barHeight + 35);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g2.setColor(textDim);
        String hint = "Preparing battlefield...";
        int hintWidth = g2.getFontMetrics().stringWidth(hint);
        g2.drawString(hint, (w - hintWidth) / 2, barY + barHeight + 60);
    }
}