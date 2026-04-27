package gui.menu;

import data.model.GameStats;
import data.model.HeroStats;
import data.model.TeamStats;
import gui.Theme;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel displayed after the game ends.
 * Shows match statistics across multiple tabs: scoreboard, performance,economy, items, and MVP. Also provides navigation buttons to return
 *  to the main menu or replay.
 */
public class AfterGamePanel extends JPanel {

    private static final long serialVersionUID = 1L;

  
    private static final Color BG_DARK       = new Color(12, 14, 20);
    private static final Color BG_CARD       = new Color(20, 24, 34);
    private static final Color BORDER_SUBTLE = new Color(40, 46, 64);

    private static final Color BLUE_ACCENT   = new Color(56, 140, 230);
    private static final Color RED_ACCENT    = new Color(220, 60, 60);

    private static final Color TEXT_PRIMARY  = new Color(220, 225, 240);
    private static final Color TEXT_MUTED    = new Color(120, 130, 155);


    private final GameStats gameStats;
    private final Dimension screenSize;
    private final AfterGameListener listener;
    


    /**
     * Listener interface for after-game navigation actions.
     */
    public interface AfterGameListener {
        void onReturnToMenu();
        void onPlayAgain();
    }


    /**
     * Constructs the AfterGamePanel with the given game statistics.
     *
     * @param stats :the game stats to display
     * @param screenSize : the screen dimensions
     * @param listener :  the navigation listener
     */
    public AfterGamePanel(GameStats stats, Dimension screenSize, AfterGameListener listener) {
        this.gameStats  = stats;
        this.screenSize = screenSize;
        this.listener   = listener;

        setPreferredSize(screenSize);
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(createHeader(),   BorderLayout.NORTH);
        add(createTabPanel(), BorderLayout.CENTER);
        add(createFooter(),   BorderLayout.SOUTH);

        setFocusable(true);
        requestFocusInWindow();
    }


    /**
     * Creates the top header showing the game result , while Background color changes depending on the outcome.
     *
     * @return the header panel
     */
    private JPanel createHeader() {
        boolean win       = gameStats != null && "WIN".equals(gameStats.getGameResult());
        Color resultColor = win ? new Color(80, 220, 130) : new Color(220, 80, 80);
        String resultText = win ? "VICTOIRE" : "DÉFAITE";

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(win ? new Color(14, 28, 18) : new Color(28, 14, 14));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(resultColor);
                g2.fillRect(0, getHeight() - 3, getWidth(), 3);

                g2.dispose();
            }
        };

        panel.setBorder(new EmptyBorder(22, 32, 22, 32));

        // Result label
        JLabel title = new JLabel(resultText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(resultColor);
        panel.add(title, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the tabbed panel containing all stat views.
     *
     * @return the tabbed pane
     */
    private JTabbedPane createTabPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 14));

        tabs.addTab("Scoreboard",  createScoreboardTab());
        tabs.addTab("Performance", createPerformanceTab());
        tabs.addTab("Économie",    createEconomyTab());
        tabs.addTab("Items",       createItemsTab());
        tabs.addTab("MVP",         createMVPTab());

        return tabs;
    }

    /**
     * Creates the footer panel and 'REJOUER' buttons.
     *
     * @return the footer panel
     */
    private JPanel createFooter() {
        JPanel panel = new JPanel(null) {
            private int hoveredIndex = -1;

            /**
             * Computes the two button rectangles centered in the footer.
             * @return array [Menu Principal rect, Rejouer rect]
             */
            private Rectangle[] getBtnRects() {
                int bw = 180, bh = 40, gap = 30;
                int startX = (getWidth() - (bw * 2 + gap)) / 2;
                int y = (getHeight() - bh) / 2;
                return new Rectangle[] {
                    new Rectangle(startX, y, bw, bh),
                    new Rectangle(startX + bw + gap, y, bw, bh)
                };
            }

            {
                setPreferredSize(new Dimension(0, 70));
                setBackground(BG_DARK);

                // Hover detection
                addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        Rectangle[] rects = getBtnRects();
                        int prev = hoveredIndex;
                        hoveredIndex = -1;
                        for (int i = 0; i < rects.length; i++) {
                            if (rects[i].contains(e.getPoint())) {
                                hoveredIndex = i;
                                break;
                            }
                        }
                        setCursor(hoveredIndex >= 0
                            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                            : Cursor.getDefaultCursor());
                        if (hoveredIndex != prev) repaint();
                    }
                });

                // Click detection
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        Rectangle[] rects = getBtnRects();
                        for (int i = 0; i < rects.length; i++) {
                            if (rects[i].contains(e.getPoint())) {
                                if (i == 0 && listener != null) listener.onReturnToMenu();
                                else if (i == 1 && listener != null) listener.onPlayAgain();
                            }
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoveredIndex = -1;
                        setCursor(Cursor.getDefaultCursor());
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle[] rects  = getBtnRects();
                String[]    labels = { "MENU PRINCIPAL", "REJOUER" };
                Color[]     colors = { new Color(100, 100, 100), Theme.ACCENT };

                for (int i = 0; i < rects.length; i++) {
                    Rectangle r       = rects[i];
                    boolean   hovered = (i == hoveredIndex);
                    Color     bg      = hovered ? colors[i].brighter() : colors[i];

                    // Button background
                    g2.setColor(bg);
                    g2.fillRoundRect(r.x, r.y, r.width, r.height, 10, 10);

                    // Button label
                    g2.setFont(new Font("Arial", Font.BOLD, 13));
                    g2.setColor(TEXT_PRIMARY);
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(labels[i],
                        r.x + (r.width  - fm.stringWidth(labels[i])) / 2,
                        r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };

        return panel;
    }

   
    /**
     * Creates the scoreboard tab showing stats for both teams side by side.
     *
     * @return the scoreboard panel
     */
    private JPanel createScoreboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        TeamStats blue = gameStats != null ? gameStats.getTeamStats(0) : null;
        TeamStats red  = gameStats != null ? gameStats.getTeamStats(1) : null;

        JPanel teamsPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        teamsPanel.setBackground(BG_DARK);
        teamsPanel.add(createTeamCard("ÉQUIPE BLEUE (VOTRE ÉQUIPE)", blue, BLUE_ACCENT, true));
        teamsPanel.add(createTeamCard("ÉQUIPE ROUGE (ENNEMIE)",      red,  RED_ACCENT,  false));

        panel.add(teamsPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a stat card for one team.
     *
     * @param title : the card title
     * @param team  : the team stats to display
     * @param color : the accent color for this team
     * @param isPlayerTeam whether this is the player's team to add extra stats
     * @return the team card panel
     */
    private JPanel createTeamCard(String title, TeamStats team, Color color, boolean isPlayerTeam) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Team title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);

        // Team stats grid
        JPanel stats = new JPanel(new GridLayout(0, 2, 10, 8));
        stats.setBackground(BG_CARD);

        if (team != null) {
            addStatRow(stats, "Kills",           String.valueOf(team.getTotalKills()));
            addStatRow(stats, "Deaths",          String.valueOf(team.getTotalDeaths()));
            addStatRow(stats, "Assists",         String.valueOf(team.getTotalAssists()));
            addStatRow(stats, "KDA",             String.format("%.2f", team.getTeamKDA()));
            addStatRow(stats, "Dégats",          formatNumber(team.getTotalDamageDealt()));
            addStatRow(stats, "Tours détruites", String.valueOf(team.getTowersDestroyed()));
            addStatRow(stats, "Or total",        formatGold(team.getGoldEarned()));
        } else {
            addStatRow(stats, "-", "-");
        }

        card.add(stats, BorderLayout.CENTER);

        // Extra player stats row 
        if (isPlayerTeam && gameStats != null) {
            TeamStats blue = gameStats.getTeamStats(0);
            if (blue != null && !blue.getHeroes().isEmpty()) {
                HeroStats player = blue.getHeroes().stream()
                    .filter(HeroStats::isPlayer)
                    .findFirst()
                    .orElse(blue.getHeroes().get(0));
                card.add(createPlayerStatsPanel(player), BorderLayout.SOUTH);
            }
        }

        return card;
    }

    /**
     * Creates a small panel showing extra stats for the player hero.
     *
     * @param player the player's hero stats
     * @return the player stats panel
     */
    private JPanel createPlayerStatsPanel(HeroStats player) {
        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 5));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        addStatRow(panel, "Niveau",    String.valueOf(player.getLevel()));
        addStatRow(panel, "CS",        String.valueOf(player.getCsCreeps()));
        addStatRow(panel, "GPM",       String.valueOf(player.getGoldPerMinute()));
        addStatRow(panel, "Net Worth", player.getNetWorth());

        return panel;
    }

    /**
     * Creates the performance tab with a bar chart comparing team damage.
     *
     * @return the performance panel
     */
    private JPanel createPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Build dataset
        DefaultCategoryDataset barData = new DefaultCategoryDataset();
        if (gameStats != null) {
            TeamStats blue = gameStats.getTeamStats(0);
            TeamStats red  = gameStats.getTeamStats(1);
            if (blue != null) barData.setValue(blue.getTotalDamageDealt(), "Bleu",  "Bleu");
            if (red  != null) barData.setValue(red.getTotalDamageDealt(),  "Rouge", "Rouge");
        }

        // Build chart
        JFreeChart chart = ChartFactory.createBarChart(
            "Dégats par Équipe", "Équipe", "Dégats",
            barData, PlotOrientation.VERTICAL, true, true, false
        );

        chart.getTitle().setPaint(TEXT_PRIMARY);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
        chart.getLegend().setItemPaint(TEXT_PRIMARY);
        chart.getLegend().setItemFont(new Font("Arial", Font.PLAIN, 11));
        chart.setTextAntiAlias(true);

        // Style the plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(BG_CARD);
        plot.setRangeGridlinePaint(BORDER_SUBTLE);
        plot.getDomainAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.BOLD, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 11));
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.BOLD, 12));

        // Style the bars
        org.jfree.chart.renderer.category.BarRenderer renderer =
            (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.15);
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
        renderer.setSeriesPaint(0, BLUE_ACCENT);
        renderer.setSeriesPaint(1, RED_ACCENT);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(screenSize.width / 3, 250));
        chartPanel.setBackground(BG_CARD);
        chartPanel.setMinimumSize(new Dimension(200, 200));
        chartPanel.setMaximumSize(new Dimension(400, 300));

        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the economy tab with a pie chart comparing gold earned per team.
     *
     * @return the economy panel
     */
    private JPanel createEconomyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Build dataset
        DefaultPieDataset goldData = new DefaultPieDataset();
        if (gameStats != null) {
            for (TeamStats team : gameStats.getTeamStatsMap().values()) {
                String name = team.getTeamId() == 0 ? "Bleu" : "Rouge";
                goldData.setValue(name, team.getGoldEarned());
            }
        } else {
            goldData.setValue("Bleu",  10000);
            goldData.setValue("Rouge", 10000);
        }

        // Build chart
        JFreeChart chart = ChartFactory.createPieChart(
            "Or total par équipe", goldData, true, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(TEXT_PRIMARY);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));

        // Style the pie
        PiePlot pie = (PiePlot) chart.getPlot();
        pie.setBackgroundPaint(Color.WHITE);
        pie.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        pie.setSectionPaint("Bleu",  BLUE_ACCENT);
        pie.setSectionPaint("Rouge", RED_ACCENT);
        pie.setStartAngle(90);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(screenSize.width / 3, 250));
        chartPanel.setBackground(BG_CARD);
        chartPanel.setMinimumSize(new Dimension(200, 200));
        chartPanel.setMaximumSize(new Dimension(400, 300));

        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the items tab. 
     *
     * @return the items panel
     */
    private JPanel createItemsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("Inventaire final", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.GRAY);

        JPanel placeholder = new JPanel();
        placeholder.setBackground(BG_CARD);
        placeholder.add(label);

        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates the MVP tab showing the best performing hero.
     *
     * @return the MVP panel
     */
   private JPanel createMVPTab() {
	    JPanel panel = new JPanel(new BorderLayout(10, 10));
	    panel.setBackground(BG_DARK);
	    panel.setBorder(new EmptyBorder(15, 15, 15, 15));
	
	    HeroStats mvp = null;
	    if (gameStats != null) {
	        for (TeamStats team : gameStats.getTeamStatsMap().values()) {
	            for (HeroStats h : team.getHeroes()) {
	                if (h.isMVP()) {
	                    mvp = h;
	                    break;
	                }
	            }
	            if (mvp != null) break;
	        }
    }

    Color accentColor = (mvp != null && mvp.getTeamId() == 1) ? RED_ACCENT : BLUE_ACCENT;

    JPanel mvpCard = new JPanel(new BorderLayout(20, 20));
    mvpCard.setBackground(BG_CARD);
    mvpCard.setBorder(BorderFactory.createCompoundBorder(
        new LineBorder(accentColor, 3),
        new EmptyBorder(30, 30, 30, 30)
    ));

    JLabel mvpTitle = new JLabel("MATCH MVP", SwingConstants.CENTER);
    mvpTitle.setFont(new Font("Arial", Font.BOLD, 28));
    mvpTitle.setForeground(accentColor);
    mvpCard.add(mvpTitle, BorderLayout.NORTH);

    JPanel stats = new JPanel(new GridLayout(0, 2, 20, 15));
    stats.setBackground(BG_CARD);

    if (mvp != null) {
        addStatRow(stats, "Héros",  mvp.getHeroName());
        addStatRow(stats, "Équipe", mvp.getTeamId() == 0 ? "Bleue" : "Rouge");
        addStatRow(stats, "K/D/A",  mvp.getKills() + "/" + mvp.getDeaths() + "/" + mvp.getAssists());
        addStatRow(stats, "Score MVP", String.valueOf(mvp.getMVPScore()));
        addStatRow(stats, "KDA Ratio", String.format("%.2f", mvp.getKDA()));
        addStatRow(stats, "Dégats", formatNumber(mvp.getDamageDealtToHeroes() + mvp.getDamageDealtToBuildings()));
        addStatRow(stats, "Or Gagné", formatGold(mvp.getGoldEarned()));
    } else {
        addStatRow(stats, "Statut", "Non déterminé");
        addStatRow(stats, "K/D/A",  "0/0/0");
    }

    mvpCard.add(stats, BorderLayout.CENTER);
    panel.add(mvpCard, BorderLayout.CENTER);
    return panel;
}


    /**
     * Adds a label-value row to the given parent panel.
     *
     * @param parent the panel to add to
     * @param label  the stat name
     * @param value  the stat value
     */
    private void addStatRow(JPanel parent, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(TEXT_MUTED);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        parent.add(lbl);

        JLabel val = new JLabel(value);
        val.setForeground(TEXT_PRIMARY);
        val.setFont(new Font("Arial", Font.BOLD, 13));
        parent.add(val);
    }

    /**
     * Formats a gold value with a "k" suffix if >= 1000.
     *
     * @param gold the gold amount
     * @return formatted string
     */
    private String formatGold(int gold) {
        return gold >= 1000 ? String.format("%.1fk", gold / 1000.0) : String.valueOf(gold);
    }

    /**
     * Formats a large number with "k" or "M" suffix.
     *
     * @param num the number to format
     * @return formatted string
     */
    private String formatNumber(int num) {
        if (num >= 1_000_000) return String.format("%.1fM", num / 1_000_000.0);
        if (num >= 1_000)     return String.format("%.1fk", num / 1_000.0);
        return String.valueOf(num);
    }
}