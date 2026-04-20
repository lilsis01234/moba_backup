package gui;

import data.model.Equipment;
import data.model.EquipmentType;
import engine.mobile.Player;
import engine.process.EquipmentLoader;
import engine.process.ShopManager;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;

public class ShopPanel {

    private static final Logger logger = LoggerUtility.getLogger(ShopPanel.class);

    private static ShopPanel instance;
    private boolean   visible  = false;
    private int       tab      = 0;
    private Equipment selected = null;

    private ShopManager shopManager;
    private List<Equipment> basicList;
    private List<Equipment> fusedList;
    private Player player;

    private BufferedImage imgSword;
    private BufferedImage imgHelmet;
    private BufferedImage imgArmor;

    private int px, py;
    private static final int W     = 600;
    private static final int H     = 430;
    private static final int ROW_H = 38;

    private ShopPanel() {}

    public static ShopPanel getInstance() {
        return instance;
    }

    public static ShopPanel create(Player player) {
        instance = new ShopPanel();
        instance.init(player);
        return instance;
    }

    public static void reset() {
        logger.info("ShopPanel réinitialisé");
        instance = null;
    }

    private void init(Player player) {
        this.player      = player;
        EquipmentLoader loader = EquipmentLoader.getInstance();
        this.basicList   = loader.getBasicList();
        this.fusedList   = loader.getFusedList();
        this.shopManager = ShopManager.create(player);
        logger.info("ShopPanel initialisé - " + basicList.size()
                  + " items basiques, " + fusedList.size() + " items fusionnés");
        loadImages();
    }

    private void loadImages() {
        imgSword  = loadImg("/res/equipment/sword.png");
        imgHelmet = loadImg("/res/equipment/helmet.png");
        imgArmor  = loadImg("/res/equipment/armor.png");

        logger.debug("Images chargées =" + (imgSword  != null)
                   + " helmet=" + (imgHelmet != null)
                   + " armor="  + (imgArmor  != null));
    }

    private BufferedImage loadImg(String path) {
        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is != null) return ImageIO.read(is);
            logger.warn("Image introuvable : " + path);
        } catch (Exception e) {
            logger.error("Erreur chargement image " + path + " : " + e.getMessage());
        }
        return null;
    }

    public void render(Graphics2D g2, int screenW, int screenH) {
        if (!visible) return;

        px = (screenW - W) / 2;
        py = (screenH - H) / 2;

        g2.setColor(new Color(18, 18, 30, 245));
        g2.fillRect(px, py, W, H);
        g2.setColor(new Color(150, 130, 60));
        g2.drawRect(px, py, W, H);

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 17));
        g2.drawString("BOUTIQUE  [B] fermer", px + 15, py + 24);

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString("Or : " + player.getGold(), px + W - 110, py + 24);

        renderTabs(g2);
        renderList(g2);
        if (selected != null) renderDetail(g2);
    }

    private void renderTabs(Graphics2D g2) {
        String[] labels = { "Basiques", "Fusionnes" };
        for (int i = 0; i < 2; i++) {
            int tx = px + 10 + i * 155;
            int ty = py + 33;
            g2.setColor(tab == i ? new Color(80, 70, 20) : new Color(35, 35, 50));
            g2.fillRect(tx, ty, 145, 26);
            g2.setColor(tab == i ? Color.YELLOW : Color.GRAY);
            g2.drawRect(tx, ty, 145, 26);
            g2.setColor(tab == i ? Color.YELLOW : Color.LIGHT_GRAY);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(labels[i], tx + 10, ty + 17);
        }
    }

    private void renderList(Graphics2D g2) {
        List<Equipment> list = tab == 0 ? basicList : fusedList;
        EquipmentType lastType = null;
        int ry = py + 70;

        for (int i = 0; i < list.size(); i++) {
            Equipment eq = list.get(i);

            if (eq.getType() != lastType) {
                lastType = eq.getType();
                g2.setColor(new Color(120, 120, 160));
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.drawString("--- " + typeName(eq.getType()) + " ---", px + 15, ry + 11);
                ry += 16;
            }

            g2.setColor(eq == selected ? new Color(80, 70, 20, 200) : new Color(35, 35, 50, 200));
            g2.fillRect(px + 10, ry, 340, ROW_H - 2);

            BufferedImage img = getImage(eq.getType());
            if (img != null) {
                g2.drawImage(img, px + 13, ry + 3, 30, 30, null);
            } else {
                g2.setColor(typeColor(eq.getType()));
                g2.fillRect(px + 13, ry + 4, 28, 28);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.drawString(typeIcon(eq.getType()), px + 18, ry + 22);
            }

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.drawString(eq.getName(), px + 48, ry + 20);

            if (eq.isFused()) {
                g2.setColor(new Color(120, 200, 255));
                g2.setFont(new Font("Arial", Font.BOLD, 11));
                g2.drawString("FUSION", px + 295, ry + 20);
            } else {
                g2.setColor(Color.YELLOW);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                g2.drawString(eq.getPrice() + " or", px + 295, ry + 20);
            }

            ry += ROW_H;
            if (ry > py + H - 90) break;
        }
    }

    private void renderDetail(Graphics2D g2) {
        int dx = px + 360;
        int dy = py + 62;
        int dw = W - 370;
        int dh = H - 72;

        g2.setColor(new Color(22, 22, 38, 235));
        g2.fillRect(dx, dy, dw, dh);
        g2.setColor(new Color(150, 130, 60));
        g2.drawRect(dx, dy, dw, dh);

        if (selected.isFused()) {
            renderFusionTree(g2, dx, dy, dw, dh);
            return;
        }

        BufferedImage img = getImage(selected.getType());
        if (img != null) {
            g2.drawImage(img, dx + 10, dy + 10, 60, 60, null);
        } else {
            g2.setColor(typeColor(selected.getType()));
            g2.fillRect(dx + 10, dy + 10, 60, 60);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString(typeIcon(selected.getType()), dx + 22, dy + 48);
        }

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString(selected.getName(), dx + 80, dy + 22);

        g2.setColor(typeColor(selected.getType()));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.drawString(typeName(selected.getType()), dx + 80, dy + 38);

        g2.setColor(new Color(255, 120, 120));
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("ATK : +" + selected.getAttackBonus(),  dx + 10, dy + 85);
        g2.setColor(new Color(120, 180, 255));
        g2.drawString("DEF : +" + selected.getDefenseBonus(), dx + 10, dy + 101);

        g2.setColor(new Color(200, 200, 200));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        drawWrapped(g2, selected.getDescription(), dx + 10, dy + 122, dw - 20);

        renderActionButton(g2, dx, dy, dw, dh);
    }

    public boolean handleClick(int mx, int my) {
        if (!visible) return false;

        if (mx >= px+10 && mx <= px+155 && my >= py+33 && my <= py+59) {
            logger.debug("Onglet basiques sélectionné");
            tab = 0; selected = null; return true;
        }
        if (mx >= px+165 && mx <= px+310 && my >= py+33 && my <= py+59) {
            logger.debug("Onglet fusionnés sélectionné");
            tab = 1; selected = null; return true;
        }

        if (selected != null) {
            int dx = px + 360;
            int dy = py + 62;
            int dh = H - 72;
            int bx = dx + 8;
            int by = dy + dh - 42;
            int bw = (W - 370) - 16;
            int bh = 28;
            if (mx >= bx && mx <= bx+bw && my >= by && my <= by+bh) {
                doAction(); return true;
            }
        }

        List<Equipment> list = tab == 0 ? basicList : fusedList;
        EquipmentType lastType = null;
        int ry = py + 70;
        for (int i = 0; i < list.size(); i++) {
            Equipment eq = list.get(i);
            if (eq.getType() != lastType) { lastType = eq.getType(); ry += 16; }
            if (mx >= px+10 && mx <= px+350 && my >= ry && my <= ry+ROW_H-2) {
                selected = (selected == eq) ? null : eq;
                logger.debug("Item sélectionné : "
                    + (selected != null ? selected.getName() : "aucun"));
                return true;
            }
            ry += ROW_H;
        }

        return true;
    }

    private boolean canDoAction() {
        return shopManager.canDoAction(selected);
    }

    private void doAction() {
        if (selected == null) return;
        boolean fused = selected.isFused();
        String nom    = selected.getName();

        boolean ok = shopManager.canDoAction(selected);
        if (ok) {
            shopManager.doAction(selected);
            if (fused) {
                logger.info("Fusion réussie : " + nom
                    + " (or restant : " + player.getGold() + ")");
            } else {
                logger.info("Achat réussi : " + nom
                    + " - prix : " + selected.getPrice()
                    + " - or restant : " + player.getGold());
            }
        } else {
            if (fused) {
                logger.warn("Fusion impossible : items requis manquants pour " + nom);
            } else {
                logger.warn("Achat impossible : or insuffisant pour " + nom
                    + " (prix : " + selected.getPrice()
                    + ", or : " + player.getGold() + ")");
            }
        }
        selected = null;
    }

    private BufferedImage getImage(EquipmentType t) {
        if (t == EquipmentType.SWORD)  return imgSword;
        if (t == EquipmentType.HELMET) return imgHelmet;
        return imgArmor;
    }

    private String typeName(EquipmentType t) {
        if (t == EquipmentType.SWORD)  return "Epees";
        if (t == EquipmentType.HELMET) return "Casques";
        return "Armures";
    }

    private String typeIcon(EquipmentType t) {
        if (t == EquipmentType.SWORD)  return "E";
        if (t == EquipmentType.HELMET) return "C";
        return "A";
    }

    private Color typeColor(EquipmentType t) {
        if (t == EquipmentType.SWORD)  return new Color(220, 80, 80);
        if (t == EquipmentType.HELMET) return new Color(80, 120, 220);
        return new Color(80, 180, 80);
    }

    private String findName(int id) {
        for (Equipment e : basicList) {
            if (e.getId() == id) return e.getName();
        }
        return "ID " + id;
    }

    private void drawWrapped(Graphics2D g2, String text, int x, int y, int maxW) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int cy = y;
        for (String word : words) {
            if (fm.stringWidth(line.toString() + word) > maxW) {
                g2.drawString(line.toString(), x, cy);
                cy += 14;
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        if (line.length() > 0) g2.drawString(line.toString(), x, cy);
    }

    private void renderActionButton(Graphics2D g2, int dx, int dy, int dw, int dh) {
        boolean canAct = canDoAction();
        int bx = dx + 8;
        int by = dy + dh - 42;
        int bw = dw - 16;
        int bh = 28;

        g2.setColor(canAct ? new Color(55, 130, 55) : new Color(100, 40, 40));
        g2.fillRect(bx, by, bw, bh);
        g2.setColor(canAct ? Color.GREEN : Color.RED);
        g2.drawRect(bx, by, bw, bh);

        String label;
        if (selected.isFused()) {
            label = canAct ? "FUSIONNER" : "Items requis manquants";
        } else {
            label = canAct ? "ACHETER (" + selected.getPrice() + " or)" : "Or insuffisant";
        }
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, bx + (bw - fm.stringWidth(label)) / 2, by + 19);
    }

    public void toggle() {
        visible = !visible;
        selected = null;
        logger.debug("ShopPanel " + (visible ? "ouvert" : "fermé"));
    }

    public boolean isVisible() { return visible; }

    private void renderFusionTree(Graphics2D g2, int dx, int dy, int dw, int dh) {
        int rootX  = dx + dw / 2;
        int rootY  = dy + 80;
        int leftX  = dx + 55;
        int rightX = dx + dw - 55;
        int childY = dy + 200;
        int r      = 28;

        String req1Name = findName(selected.getReq1());
        String req2Name = findName(selected.getReq2());

        g2.setColor(new Color(150, 130, 60));
        g2.drawLine(rootX, rootY + r, leftX,  childY - r);
        g2.drawLine(rootX, rootY + r, rightX, childY - r);

        g2.setColor(new Color(60, 50, 15));
        g2.fillOval(rootX - r, rootY - r, r * 2, r * 2);
        g2.setColor(new Color(200, 170, 60));
        g2.drawOval(rootX - r, rootY - r, r * 2, r * 2);

        g2.setColor(new Color(25, 40, 80));
        g2.fillOval(leftX - r, childY - r, r * 2, r * 2);
        g2.setColor(new Color(80, 120, 220));
        g2.drawOval(leftX - r, childY - r, r * 2, r * 2);

        g2.setColor(new Color(25, 40, 80));
        g2.fillOval(rightX - r, childY - r, r * 2, r * 2);
        g2.setColor(new Color(80, 120, 220));
        g2.drawOval(rightX - r, childY - r, r * 2, r * 2);

        BufferedImage img = getImage(selected.getType());
        if (img != null) {
            g2.drawImage(img, rootX - 20, rootY - 20, 40, 40, null);
            g2.drawImage(img, leftX  - 20, childY - 20, 40, 40, null);
            g2.drawImage(img, rightX - 20, childY - 20, 40, 40, null);
        } else {
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            FontMetrics fm2 = g2.getFontMetrics();
            String icon = typeIcon(selected.getType());
            g2.drawString(icon, rootX  - fm2.stringWidth(icon)/2, rootY  + 6);
            g2.drawString(icon, leftX  - fm2.stringWidth(icon)/2, childY + 6);
            g2.drawString(icon, rightX - fm2.stringWidth(icon)/2, childY + 6);
        }

        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(selected.getName(),
            rootX - fm.stringWidth(selected.getName()) / 2, rootY + r + 14);

        g2.setColor(new Color(120, 200, 255));
        g2.setFont(new Font("Arial", Font.PLAIN, 10));
        fm = g2.getFontMetrics();
        g2.drawString(req1Name, leftX  - fm.stringWidth(req1Name)  / 2, childY + r + 14);
        g2.drawString(req2Name, rightX - fm.stringWidth(req2Name) / 2, childY + r + 14);

        g2.setColor(new Color(255, 120, 120));
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("ATK : +" + selected.getAttackBonus(),  dx + 8, dy + dh - 110);
        g2.setColor(new Color(120, 180, 255));
        g2.drawString("DEF : +" + selected.getDefenseBonus(), dx + 8, dy + dh - 92);

        g2.setColor(new Color(200, 200, 200));
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        drawWrapped(g2, selected.getDescription(), dx + 8, dy + dh - 68, dw - 16);

        renderActionButton(g2, dx, dy, dw, dh);
    }
}