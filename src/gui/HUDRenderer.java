package gui;

import engine.mobile.Bot;
import engine.mobile.Personnage;
import engine.mobile.Player;
import engine.process.Arena;
import engine.map.TilesManager;
import data.model.KDA;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import  java.util.List;
import data.model.Equipment;

import data.model.Hero;
import data.model.Spell;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class HUDRenderer {
    private final Player player;
    private final Arena arena;
    private int screenWidth;
    private int screenHeight;
    private boolean paused = false;
    private long matchStartTime;
    private int blueKills = 0;
    private int redKills = 0;
    private int playerGold = 0;
    private MinimapRenderer minimapRenderer;
    private Hero hero;
    private engine.mobile.Bot targetedBot = null;
    private HashMap<data.model.Spell.Type, BufferedImage> spellIcons = new java.util.HashMap<>();

    public HUDRenderer(Player player, Arena arena, TilesManager tilesManager, Hero hero) {
        this.player = player;
        this.arena = arena;
        this.matchStartTime = System.currentTimeMillis();
        this.minimapRenderer = new MinimapRenderer(player, arena, tilesManager, 0, 0, 180);
        this.hero = hero;
        loadSpellIcons();
    }

    public void setScreenSize(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        int margin = 10;
        minimapRenderer.setPosition(screenWidth - 180 - margin, margin);
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void setKills(int blue, int red) {
        this.blueKills = blue;
        this.redKills = red;
    }

    public void setGold(int gold) {
        this.playerGold = gold;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }
    public void setTargetedBot(engine.mobile.Bot bot) {
        this.targetedBot = bot;
    }

    public void render(Graphics2D g2) {
        int margin = 10;
        
        minimapRenderer.render(g2);
        
        renderSideTeamPanels(g2);

        renderScoreboard(g2, margin, margin);
        
        renderGoldDisplay(g2, screenWidth - 100, 200);
        
        renderCharacterPanel(g2, margin, screenHeight - 160);
        
        if (targetedBot != null && targetedBot.isActive()) {
        	renderEnemyPanel(g2, margin, screenHeight - 160 - 100);
        }
        
        renderAbilityBar(g2, screenWidth - 170 - margin, screenHeight - 60);
        renderRecallButton(g2,screenWidth - 170 - margin -60,screenHeight - 60);
        renderItemBar(g2, screenWidth - 140 - margin, screenHeight - 110);
        
        renderPauseButton(g2, margin, 100);
        
        if (!player.isActive()) {
            renderRespawnOverlay(g2);
        }
    }

    private void renderScoreboard(Graphics2D g2, int x, int y) {
        int width = 120;
        int height = 60;
        
        g2.setColor(Theme.PANEL_BG);
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        long elapsed = System.currentTimeMillis() - matchStartTime;
        int minutes = (int)(elapsed / 60000);
        int seconds = (int)((elapsed % 60000) / 1000);
        String timerText = String.format("%02d:%02d", minutes, seconds);
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 18));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(timerText, x + (width - fm.stringWidth(timerText)) / 2, y + 22);
        
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.setColor(new Color(100, 150, 255));
        g2.drawString("Blue: " + blueKills, x + 5, y + 40);
        
        g2.setColor(new Color(255, 100, 100));
        g2.drawString("Red: " + redKills, x + 5, y + 55);
    }

    private void renderGoldDisplay(Graphics2D g2, int x, int y) {
        int width = 90;
        int height = 25;
        
        g2.setColor(new Color(30, 30, 40, 220));
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.drawString("" + playerGold, x + 10, y + 18);
        
        g2.setColor(new Color(200, 200, 150));
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("GOLD", x + 60, y + 18);
    }

    private void renderCharacterPanel(Graphics2D g2, int x, int y) {
    	
        int width = 240;
        int height = 140;
        
        g2.setColor(Theme.PANEL_BG);
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        BufferedImage portrait = player.getFrontFrame();
        if (portrait != null) {
            g2.drawImage(portrait, x + 10, y + 10, 40, 40, null);
        } else {
            g2.setColor(new Color(50, 50, 70));
            g2.fillRect(x + 10, y + 10, 40, 40);
            g2.setColor(Color.GRAY);
            g2.drawRect(x + 10, y + 10, 40, 40);
        }
        
        //name
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(hero != null ? hero.getName() : "Hero", x + 65, y + 24);
        
        //lvl
        g2.setFont(new Font("Arial", Font.PLAIN, 11));
        g2.setColor(new Color(180, 180, 200));
        g2.drawString("Lv." + player.getLevel(), x + 65, y + 38);
        
        //xp
        
        int xpThreshold = player.getLevel() * 100;
        int xpBarX = x + 10 + 41;
        int xpBarY = y + 10;
        int xpBarW = 5;
        int xpBarH = 40;
        int xpFill = xpThreshold > 0 ? (int)((player.getXp() / (double) xpThreshold) * xpBarH) : 0;

        g2.setColor(Color.gray);
        g2.fillRect(xpBarX, xpBarY, xpBarW, xpBarH);

        g2.setColor(new Color(150, 80, 220));
        g2.fillRect(xpBarX, xpBarY + (xpBarH - xpFill), xpBarW, xpFill); 

        g2.setColor(Color.gray);
        g2.drawRect(xpBarX, xpBarY, xpBarW, xpBarH);
        
        //hp?
        int barX = x + 10;
        int barY = y + 60;
        int barWidth = width - 20;
        int barHeight = 14;
        
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        double hpPct = player.getHp() / player.getMaxHp();
        g2.setColor(new Color(50, 200, 50));
        g2.fillRect(barX, barY, (int)(hpPct * barWidth), barHeight);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString("HP", barX + 2, barY + 10);
        String hpText = (int)player.getHp() + "/" + (int)player.getMaxHp();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(hpText, barX + barWidth - fm.stringWidth(hpText) - 2, barY + 10);
        
        //mana?
        barY += 18;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        double manaPct = player.getMana() / player.getMaxMana();
        g2.setColor(new Color(50, 100, 200));
        g2.fillRect(barX, barY, (int)(manaPct * barWidth), barHeight);
        g2.setColor(Color.WHITE);
        g2.drawString("MANA", barX + 2, barY + 10);
        String manaText = (int)player.getMana() + "/" + (int)player.getMaxMana();
        g2.drawString(manaText, barX + barWidth - fm.stringWidth(manaText) - 2, barY + 10);
        
        //stats
        int statsY = y + 110;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        g2.drawString("ATK: " + (int) player.getAtkDamage(), x + 10,  statsY);
        g2.drawString("DEF: " + player.getDefense(),          x + 80,  statsY);
        g2.drawString("SPD: " + (int) player.getSpeed(),      x + 150, statsY);
}
    private void renderEnemyPanel(Graphics2D g2, int x, int y) {
        int width = 240;  
        int height = 100;

        g2.setColor(Theme.PANEL_BG);
        g2.fillRect(x, y, width, height);
        g2.setColor(new Color(180, 60, 60));
        g2.drawRect(x, y, width, height);

        // portrait
        int portraitSize = 80;
        int portraitX = x + 5;
        int portraitY = y + 10;
        java.awt.image.BufferedImage portrait = targetedBot.getFrontFrame();
        if (portrait != null) {
            g2.drawImage(portrait, portraitX, portraitY, portraitSize, portraitSize, null);
        } else {
            g2.setColor(new Color(50, 50, 70));
            g2.fillRect(portraitX, portraitY, portraitSize, portraitSize);
        }

        // name + bars shifted right of portrait
        int textX = portraitX + portraitSize + 8;
        int barX = textX;
        int barWidth = width - textX - 10;
        int barHeight = 14;

        g2.setColor(new Color(220, 100, 100));
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString(targetedBot.getName(), textX, y + 20);

        // HP bar
        int barY = y + 30;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        double hpPct = targetedBot.getHp() / targetedBot.getMaxHp();
        g2.setColor(new Color(200, 50, 50));
        g2.fillRect(barX, barY, (int)(hpPct * barWidth), barHeight);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        String hpText = (int)targetedBot.getHp() + "/" + (int)targetedBot.getMaxHp();
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString("HP", barX + 2, barY + 10);
        g2.drawString(hpText, barX + barWidth - fm.stringWidth(hpText) - 2, barY + 10);

        // Mana bar
        barY += 18;
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(barX, barY, barWidth, barHeight);
        double manaPct = targetedBot.getMana() / targetedBot.getMaxMana();
        g2.setColor(new Color(50, 100, 200));
        g2.fillRect(barX, barY, (int)(manaPct * barWidth), barHeight);
        g2.setColor(Color.WHITE);
        String manaText = (int)targetedBot.getMana() + "/" + (int)targetedBot.getMaxMana();
        g2.drawString("MANA", barX + 2, barY + 10);
        g2.drawString(manaText, barX + barWidth - fm.stringWidth(manaText) - 2, barY + 10);
    }
    
    private void renderSideTeamPanels(Graphics2D g2) {
    	
        int panelWidth = 140; 
        int panelHeight = 70; 
        int yMargin = 10;
        int xMargin = 10; 
        
        
        ArrayList<Bot> allBots = arena.getBotManager().getAllBots();
        ArrayList<Personnage> teamAllies = new ArrayList<>();
  
        teamAllies.add(player);
        
        ArrayList<Personnage> teamEnemies = new ArrayList<>();
        
        for (Bot b : allBots) {
            if (b.getTeam() == 0) teamAllies.add(b);
            else teamEnemies.add(b);
        }
        
        // Render allies
        int allyX = xMargin;
        int allyY = yMargin + 160;
        for (Personnage p : teamAllies) {
            drawSideHeroPanel(g2, allyX, allyY, panelWidth, panelHeight, p, Color.CYAN, true);
            allyY += panelHeight + yMargin;
        }
        
        // Render enemies
        int enemyX = screenWidth - panelWidth - xMargin;
        int enemyY = yMargin + 250; 
        for (Personnage p : teamEnemies) {
            drawSideHeroPanel(g2, enemyX, enemyY, panelWidth, panelHeight, p, Color.RED, false);
            enemyY += panelHeight + yMargin;
        }
    }

    private void drawSideHeroPanel(Graphics2D g2, int x, int y, int w, int h, Personnage p, Color teamColor, boolean isAlly) {
        // 1. Draw Panel Background
        g2.setColor(Theme.PANEL_BG); 
        g2.fillRect(x, y, w, h);
        
        // 2. Panel Border
        g2.setColor(teamColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(x, y, w, h);
         
	    // portrait 
	    int portraitX = x + 5;
	    int portraitY = y + 5;
	    int portraitS = h - 10;
	    
	    BufferedImage portrait = p.getFrontFrame();
	    if (portrait != null) {
	        g2.drawImage(portrait, portraitX, portraitY, portraitS, portraitS, null);
	    } else {
	        g2.setColor(new Color(50, 50, 70));
	        g2.fillRect(portraitX, portraitY, portraitS, portraitS);
	    }
	    
	    // border
	    g2.setColor(teamColor);
	    g2.drawRect(portraitX, portraitY, portraitS, portraitS);
	    
	    
	    int textX = portraitX + portraitS + 8;
	    int textY = y + 15;
	    
	    g2.setFont(new Font("Arial", Font.BOLD, 12));
	    g2.setColor(Color.WHITE);
	    String nameStr = (p instanceof Player) ? "You" : ((Bot)p).getName();
	    g2.drawString(nameStr, textX, textY);
	    
	
	    //lvl
	    g2.setFont(new Font("Arial", Font.PLAIN, 10));
	    g2.setColor(new Color(180, 180, 200));
	    g2.drawString("Lv." + p.getLevel(), textX, textY + 13);
	    
	    //KDA placeholder
	    KDA s = p.getKDA();
	    String kda = s.getKills() + "/" + s.getDeaths() + "/" + s.getAssists();
	    g2.setFont(new Font("Arial", Font.PLAIN, 10));
	    g2.setColor(Color.WHITE);
	    g2.drawString(kda, textX, textY + 46);
	        
	    // HP
	    int hpBarW = w - portraitS - 20;
	    int hpBarH = 8;
	    int hpBarX = textX;
	    int hpBarY = textY + 20;
	        
	    double hpPercent = (double) p.getHp() / p.getMaxHp();
	    g2.setColor(Color.DARK_GRAY);
	    g2.fillRect(hpBarX, hpBarY, hpBarW, hpBarH);
	     
	    g2.setColor(new Color(50, 200, 50));
	    g2.fillRect(hpBarX, hpBarY, (int)(hpPercent * hpBarW), hpBarH);
	     
	    g2.setColor(Color.WHITE);
	    g2.drawRect(hpBarX, hpBarY, hpBarW, hpBarH);
	    
	    if (!p.isActive()) {
	        
	        
	        g2.setColor(new Color(0, 0, 0, 150));
	        g2.fillRect(x, y, w, h);
	        
	        
	        // Timer Text
	        int timeLeft = (int) p.getRespawnTimer();
	        g2.setColor(Color.WHITE);
	        g2.setFont(new Font("Arial", Font.BOLD, 22)); 
	            FontMetrics fm = g2.getFontMetrics();
	            String timeText = String.valueOf(timeLeft);
	            g2.drawString(timeText, x + (w - fm.stringWidth(timeText)) / 2, y + (h / 2) + 8);
	  
	            
	        }
	        
    
    }
    

    private void renderAbilityBar(Graphics2D g2, int x, int y) {
        int width = 180;
        int height = 95;
        
        g2.setColor(Theme.PANEL_BG);
        g2.fillRect(x, y, width, height);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, width, height);
        
        int gap = 10;
        int padding = 10;
        int startX = x + padding;
        int slotSize = (width - 2 * padding - 2 * gap) / 3;
        
        for (int i = 0; i < 3; i++) {
            int slotX = startX + i * (slotSize + gap);
            int slotY = y + 6;

            // background
            g2.setColor(new Color(40, 40, 60));
            g2.fillRect(slotX, slotY, slotSize, slotSize);

            // icon
            if (i < player.getSpells().size()) {
                Spell spell = player.getSpells().get(i);
                BufferedImage icon = spellIcons.get(spell.getType());
                if (icon != null)
                    g2.drawImage(icon, slotX, slotY, slotSize, slotSize, null);

                // cooldown 
                double timeLeft = player.getSpellCooldownTimers()[i];
                if (timeLeft > 0) {
                    double duration = spell.getCooldown();
                    int fillHeight = (int)((timeLeft / duration) * slotSize);
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillRect(slotX, slotY + (slotSize - fillHeight), slotSize, fillHeight);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 11));
                    g2.drawString(String.format("%.1fs", timeLeft), slotX + 10, slotY + slotSize - 20);
                }
            }

            // border
            g2.setColor(new Color(70, 70, 90));
            g2.drawRect(slotX, slotY, slotSize, slotSize);

            // keybind number
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString((i + 1) + "", slotX + 3, slotY + 12);
        }
    }
 
	private void renderRecallButton(Graphics2D g2, int x, int y) {
	    int width = 60;
	    int height = 95;
	    
	    g2.setColor(Theme.PANEL_BG);
	    g2.fillRect(x, y, width, height);
	    g2.setColor(Color.GRAY);
	    g2.drawRect(x, y, width, height);
	    
	    int padding = 10;
	    int slotSize = width - (padding * 2);
	    int slotX = x + padding;
	    int slotY = y + 10;
	
	    g2.setColor(new Color(40, 40, 60));
	    g2.fillRect(slotX, slotY, slotSize, slotSize);
	
	    if (player.isRecalling()) {
	        double timeLeft = player.getRecallTimer();
	        double duration = player.getRecallDuration();
	
	        g2.setColor(new Color(0, 150, 255, 120));
	        int fillHeight = (int)((timeLeft / duration) * slotSize);
	        g2.fillRect(slotX, slotY + (slotSize - fillHeight), slotSize, fillHeight);
	
	        
	        g2.setColor(Color.WHITE);
	        g2.setFont(new Font("Arial", Font.BOLD, 14));
	        g2.drawString(String.format("%.1fs", timeLeft), slotX + 6, slotY + 25);
	    } else {
	    	g2.setColor(new Color(155, 173, 199));
	        g2.setFont(new Font("Arial", Font.BOLD, 22));
	        g2.drawString("R", slotX + 11, slotY + 28);
	    }
	    
	    g2.setColor(new Color(70, 70, 90));
	    g2.drawRect(slotX, slotY, slotSize, slotSize);
	}


	public boolean handleRecallClick(int clickX, int clickY) {
	    int margin = 10;
	    int x = screenWidth - 170 - margin - 60;
	    int y = screenHeight - 60;
	    
	    if (clickX >= x && clickX <= x + 60 && clickY >= y && clickY <= y + 95) {
	        player.startRecall();
	        return true;
	    }
	    return false;
	}    

private void renderItemBar(Graphics2D g2, int x, int y) {
    int width  = 210;
    int height = 45;

    g2.setColor(Theme.PANEL_BG);
    g2.fillRect(x, y, width, height);
    g2.setColor(Color.GRAY);
    g2.drawRect(x, y, width, height);

    g2.setColor(new Color(180, 180, 200));
    g2.setFont(new Font("Arial", Font.BOLD, 10));
    g2.drawString("ITEMS  [B]=Boutique", x + 5, y + 12);

    List<Equipment> gear = player.getEquippedGear();
    int slotSize = 28;
    int gap      = 3;

    for (int i = 0; i < 6; i++) {
        int slotX = x + 5 + i * (slotSize + gap);
        int slotY = y + 15;

        if (i < gear.size()) {
            Equipment eq = gear.get(i);
            g2.setColor(typeColor(eq));
            g2.fillRect(slotX, slotY, slotSize, slotSize);
            g2.setColor(Color.YELLOW);
            g2.drawRect(slotX, slotY, slotSize, slotSize);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.drawString(eq.getName().substring(0, 1), slotX + 9, slotY + 19);
        } else {
            g2.setColor(new Color(40, 40, 60));
            g2.fillRect(slotX, slotY, slotSize, slotSize);
            g2.setColor(new Color(70, 70, 90));
            g2.drawRect(slotX, slotY, slotSize, slotSize);
        }
    }
}

private Color typeColor(Equipment eq) {
    if (eq.getType() == data.model.EquipmentType.SWORD)  return new Color(160, 50, 50);
    if (eq.getType() == data.model.EquipmentType.HELMET) return new Color(50, 80, 160);
    return new Color(50, 130, 50);
}

    private void renderPauseButton(Graphics2D g2, int x, int y) {
        int size = 30;
        
        g2.setColor(new Color(40, 40, 55, 220));
        g2.fillRect(x, y, size, size);
        g2.setColor(Color.GRAY);
        g2.drawRect(x, y, size, size);
        
        g2.setColor(Color.WHITE);
        int barWidth = 4;
        int barHeight = 12;
        int spacing = 4;
        g2.fillRect(x + (size - barWidth * 2 - spacing) / 2, y + (size - barHeight) / 2, barWidth, barHeight);
        g2.fillRect(x + (size - barWidth * 2 - spacing) / 2 + barWidth + spacing, y + (size - barHeight) / 2, barWidth, barHeight);
    }

    private void renderRespawnOverlay(Graphics2D g2) {
    	
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);
       
        int timeLeft = (int) player.getRespawnTimer();
        String text = "RESPAWNING IN " + timeLeft + "s";
        
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        g2.drawString(text, (screenWidth - textWidth) / 2, screenHeight / 2);
        
        //warning
        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String subText = "Beware of the enemies!!";
        int subWidth = g2.getFontMetrics().stringWidth(subText);
        g2.drawString(subText, (screenWidth - subWidth) / 2, (screenHeight / 2) + 30);
        
    }

    public boolean handleMinimapClick(int clickX, int clickY) {
        return minimapRenderer.handleClick(clickX, clickY);
    }
    
    public boolean handlePauseButtonClick(int clickX, int clickY) {
        int pauseMargin = 10;
        int pauseY = 100;
        int pauseSize = 30;
        
        if (clickX >= pauseMargin && clickX <= pauseMargin + pauseSize &&
            clickY >= pauseY && clickY <= pauseY + pauseSize) {
            return true;
        }
        return false;
    }
    
    public boolean isMouseOverPauseButton(int mouseX, int mouseY) {
        int pauseMargin = 10;
        int pauseY = 100;
        int pauseSize = 30;
        
        return mouseX >= pauseMargin && mouseX <= pauseMargin + pauseSize &&
               mouseY >= pauseY && mouseY <= pauseY + pauseSize;
    }
    
    private void loadSpellIcons() {
        try {
            spellIcons.put(Spell.Type.DAMAGE,ImageIO.read(getClass().getResourceAsStream("/res/Sorts/dmg.png")));
            spellIcons.put(Spell.Type.CROWD_CONTROL,ImageIO.read(getClass().getResourceAsStream("/res/Sorts/cc.png")));
            spellIcons.put(Spell.Type.SUPPORT,ImageIO.read(getClass().getResourceAsStream("/res/Sorts/heal.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
