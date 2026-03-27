package gui;

import java.awt.*;

public class MenuButtonRenderer {
    private static final Color BUTTON_BG = new Color(40, 35, 50);
    private static final Color BUTTON_HOVER = new Color(60, 50, 70);
    private static final Color BUTTON_BORDER = new Color(100, 90, 70);
    private static final Color ACCENT = new Color(180, 140, 90);
    private static final Color TEXT_MAIN = new Color(240, 230, 200);
    private static final Color TEXT_DIM = new Color(160, 150, 130);

    public static void renderButton(Graphics2D g2, int x, int y, int width, int height, boolean selected, String text) {
        if (selected) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(x + 4, y + 4, width, height);
        }

        g2.setColor(selected ? BUTTON_HOVER : BUTTON_BG);
        g2.fillRect(x, y, width, height);

        g2.setColor(selected ? ACCENT : BUTTON_BORDER);
        int b = 2;
        g2.fillRect(x, y, width, b);
        g2.fillRect(x, y + height - b, width, b);
        g2.fillRect(x, y, b, height);
        g2.fillRect(x + width - b, y, b, height);

        g2.setColor(selected ? TEXT_MAIN : TEXT_DIM);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + (height + fm.getAscent()) / 2 - 4);
    }

    public static int getButtonY(int screenHeight, int index, int btnHeight, int spacing) {
        return screenHeight / 2 - 50 + index * (btnHeight + spacing);
    }
}