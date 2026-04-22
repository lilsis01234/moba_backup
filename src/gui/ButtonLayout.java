package gui;

import java.awt.*;

public class ButtonLayout {
    public final int x;
    public final int y;
    public final int width;
    public final int height;

    public ButtonLayout(int screenWidth, int screenHeight, int index, int width, int height, int spacing, int yOffset) {
        this.x = (screenWidth - width) / 2;
        this.y = screenHeight / 2 + yOffset + index * (height + spacing);
        this.width = width;
        this.height = height;
    }

    public static void renderButton(Graphics2D g2, ButtonLayout btn, boolean selected, String text) {
        if (selected) {
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(btn.x + 4, btn.y + 4, btn.width, btn.height);
        }
        g2.setColor(selected ? Theme.BUTTON_HOVER : Theme.BUTTON_BG);
        g2.fillRect(btn.x, btn.y, btn.width, btn.height);
        g2.setColor(selected ? Theme.ACCENT : Theme.BUTTON_BORDER);
        int b = 2;
        g2.fillRect(btn.x, btn.y, btn.width, b);
        g2.fillRect(btn.x, btn.y + btn.height - b, btn.width, b);
        g2.fillRect(btn.x, btn.y, b, btn.height);
        g2.fillRect(btn.x + btn.width - b, btn.y, b, btn.height);
        g2.setColor(selected ? Theme.TEXT_MAIN : Theme.TEXT_DIM);
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, btn.x + (btn.width - fm.stringWidth(text)) / 2, btn.y + (btn.height + fm.getAscent()) / 2 - 4);
    }

    public static int findButtonAt(int mouseX, int mouseY, ButtonLayout[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            ButtonLayout btn = buttons[i];
            if (mouseX >= btn.x && mouseX <= btn.x + btn.width && mouseY >= btn.y && mouseY <= btn.y + btn.height) {
                return i;
            }
        }
        return -1;
    }
}