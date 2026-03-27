package gui;

import java.awt.*;

public class MenuButtonRenderer {

    public static void renderButton(Graphics2D g2, int x, int y, int width, int height, boolean selected, String text) {
        if (selected) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(x + 4, y + 4, width, height);
        }

        g2.setColor(selected ? Theme.BUTTON_HOVER : Theme.BUTTON_BG);
        g2.fillRect(x, y, width, height);

        g2.setColor(selected ? Theme.ACCENT : Theme.BUTTON_BORDER);
        int b = 2;
        g2.fillRect(x, y, width, b);
        g2.fillRect(x, y + height - b, width, b);
        g2.fillRect(x, y, b, height);
        g2.fillRect(x + width - b, y, b, height);

        g2.setColor(selected ? Theme.TEXT_MAIN : Theme.TEXT_DIM);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, x + (width - fm.stringWidth(text)) / 2, y + (height + fm.getAscent()) / 2 - 4);
    }

    public static int getButtonY(int screenHeight, int index, int btnHeight, int spacing) {
        return screenHeight / 2 - 50 + index * (btnHeight + spacing);
    }
}