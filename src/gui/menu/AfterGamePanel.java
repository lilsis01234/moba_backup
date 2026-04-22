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
import java.util.Map;

public class AfterGamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private GameStats gameStats;
    private Dimension screenSize;
    private AfterGameListener listener;
    
    private static final Color BG_DARK       = new Color(12, 14, 20);
    private static final Color BG_CARD       = new Color(20, 24, 34);
    private static final Color BG_ROW_ALT    = new Color(26, 30, 44);
    private static final Color BORDER_SUBTLE = new Color(40, 46, 64);

    private static final Color BLUE_ACCENT   = new Color(56, 140, 230);
    private static final Color RED_ACCENT    = new Color(220, 60, 60);
    private static final Color GOLD_COLOR    = new Color(240, 185, 50);

    private static final Color TEXT_PRIMARY  = new Color(220, 225, 240);
    private static final Color TEXT_MUTED    = new Color(120, 130, 155);

    public interface AfterGameListener {
        void onReturnToMenu();
        void onPlayAgain();
    }

    public AfterGamePanel(GameStats stats, Dimension screenSize, AfterGameListener listener) {
        this.gameStats = stats;
        this.screenSize = screenSize;
        this.listener = listener;
        setPreferredSize(screenSize);
        setBackground(BG_DARK);
        setLayout(new BorderLayout(0, 0));

        add(createHeader(), BorderLayout.NORTH);
        add(createTabPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        setFocusable(true);
        requestFocusInWindow();
    }

    private JPanel createHeader() {
        boolean win = gameStats != null && "WIN".equals(gameStats.getGameResult());
        Color resultColor = win ? new Color(80,220,130) : new Color(220,80,80);
        String resultText = win ? "VICTOIRE" : "DÉFAITE";

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(win ? new Color(14,28,18) : new Color(28,14,14));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(resultColor);
                g2.fillRect(0,getHeight()-3,getWidth(),3);
                g2.dispose();
            }
        };

        panel.setBorder(new EmptyBorder(22,32,22,32));

        JLabel title = new JLabel(resultText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(resultColor);

        panel.add(title, BorderLayout.CENTER);

        return panel;
    }
    private JTabbedPane createTabPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.PLAIN, 14));
        
        tabs.addTab("Scoreboard", createScoreboardTab());
        tabs.addTab("Performance", createPerformanceTab());
        tabs.addTab("Économie", createEconomyTab());
        tabs.addTab("Items", createItemsTab());
        tabs.addTab("MVP", createMVPTab());
        
        return tabs;
    }

    private JPanel createScoreboardTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        TeamStats blue = gameStats != null ? gameStats.getTeamStats(0) : null;
        TeamStats red = gameStats != null ? gameStats.getTeamStats(1) : null;

        JPanel teamsPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        teamsPanel.setBackground(BG_DARK);
        teamsPanel.add(createTeamCard("ÉQUIPE BLEUE (VOTRE ÉQUIPE)", blue, BLUE_ACCENT, true));
        teamsPanel.add(createTeamCard("ÉQUIPE ROUGE (ENNEMIE)", red, RED_ACCENT, false));

        panel.add(teamsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTeamCard(String title, TeamStats team, Color color, boolean isPlayerTeam) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(0, 2, 10, 8));
        stats.setBackground(BG_CARD);

        if (team != null) {
            addStatRow(stats, "Kills", String.valueOf(team.getTotalKills()));
            addStatRow(stats, "Deaths", String.valueOf(team.getTotalDeaths()));
            addStatRow(stats, "Assists", String.valueOf(team.getTotalAssists()));
            addStatRow(stats, "KDA", String.format("%.2f", team.getTeamKDA()));
            addStatRow(stats, "Tours détruites", String.valueOf(team.getTowersDestroyed()));
            addStatRow(stats, "Or total", formatGold(team.getGoldEarned()));
        } else {
            addStatRow(stats, "-", "-");
        }

        card.add(stats, BorderLayout.CENTER);

        if (isPlayerTeam && gameStats != null) {
            TeamStats blue = gameStats.getTeamStats(0);
            if (blue != null && !blue.getHeroes().isEmpty()) {
                HeroStats player = blue.getHeroes().stream()
                    .filter(HeroStats::isPlayer)
                    .findFirst()
                    .orElse(blue.getHeroes().get(0));
                
                JPanel playerStats = createPlayerStatsPanel(player);
                card.add(playerStats, BorderLayout.SOUTH);
            }
        }

        return card;
    }

    private JPanel createPlayerStatsPanel(HeroStats player) {
        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 5));
        panel.setBackground(BG_CARD);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        addStatRow(panel, "Niveau", String.valueOf(player.getLevel()));
        addStatRow(panel, "CS", String.valueOf(player.getCsCreeps()));
        addStatRow(panel, "GPM", String.valueOf(player.getGoldPerMinute()));
        addStatRow(panel, "Net Worth", player.getNetWorth());

        return panel;
    }

    private JPanel createPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        DefaultCategoryDataset barData = new DefaultCategoryDataset();
        
        if (gameStats != null) {
            for (TeamStats team : gameStats.getTeamStatsMap().values()) {
                String teamName = team.getTeamId() == 0 ? "Bleu" : "Rouge";
                barData.setValue(team.getTotalDamageDealt(), "Dégats", teamName);
                barData.setValue(team.getTowersDestroyed() * 500, "Objectif", teamName);
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
            "Dégats par Équipe",
            "Équipe",
            "Dégats",
            barData,
            PlotOrientation.VERTICAL,
            true, true, false
        );

        chart.getTitle().setPaint(TEXT_PRIMARY);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
        chart.getLegend().setItemPaint(TEXT_PRIMARY);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(BG_CARD);
        plot.setRangeGridlinePaint(BORDER_SUBTLE);
        plot.getDomainAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 12));
        plot.getRangeAxis().setTickLabelFont(new Font("Arial", Font.PLAIN, 11));

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(screenSize.width / 3, 250));
        chartPanel.setBackground(BG_CARD);
        chartPanel.setMinimumSize(new Dimension(200, 200));
        chartPanel.setMaximumSize(new Dimension(400, 300));

        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEconomyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        DefaultPieDataset goldData = new DefaultPieDataset();
        
        if (gameStats != null) {
            for (TeamStats team : gameStats.getTeamStatsMap().values()) {
                String name = team.getTeamId() == 0 ? "Bleu" : "Rouge";
                goldData.setValue(name, team.getGoldEarned());
            }
        } else {
            goldData.setValue("Bleu", 10000);
            goldData.setValue("Rouge", 10000);
        }

        JFreeChart chart = ChartFactory.createPieChart(
            "Or total par équipe",
            goldData, true, true, false
        );
        
        chart.setBackgroundPaint(Color.WHITE);
        chart.getTitle().setPaint(TEXT_PRIMARY);
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 14));
        
        PiePlot pie = (PiePlot) chart.getPlot();
        pie.setBackgroundPaint(Color.WHITE);
        pie.setLabelFont(new Font("Arial", Font.PLAIN, 12));
        pie.setSectionPaint(0, BLUE_ACCENT);
        pie.setSectionPaint(1, RED_ACCENT);
        pie.setStartAngle(90);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(screenSize.width / 3, 250));
        chartPanel.setBackground(BG_CARD);
        chartPanel.setMinimumSize(new Dimension(200, 200));
        chartPanel.setMaximumSize(new Dimension(400, 300));

        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

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

    private JPanel createMVPTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel mvpCard = new JPanel(new BorderLayout(20, 20));
        mvpCard.setBackground(BG_CARD);
        mvpCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BLUE_ACCENT, 3),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel mvpTitle = new JLabel("MVP", SwingConstants.CENTER);
        mvpTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mvpTitle.setForeground(BLUE_ACCENT);
        mvpCard.add(mvpTitle, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(0, 2, 20, 15));
        stats.setBackground(BG_CARD);

        if (gameStats != null) {
            TeamStats blue = gameStats.getTeamStats(0);
            if (blue != null && !blue.getHeroes().isEmpty()) {
                HeroStats mvp = blue.getHeroes().get(0);
                addStatRow(stats, "Héros", mvp.getHeroName());
                addStatRow(stats, "K/D/A", mvp.getKills() + "/" + mvp.getDeaths() + "/" + mvp.getAssists());
                addStatRow(stats, "KDA", String.format("%.2f", mvp.getKDA()));
                addStatRow(stats, "Dégats", formatNumber(mvp.getDamageDealtToHeroes()));
            }
        } else {
            addStatRow(stats, "Joueur", "Héros");
            addStatRow(stats, "K/D/A", "0/0/0");
        }

        mvpCard.add(stats, BorderLayout.CENTER);
        panel.add(mvpCard, BorderLayout.CENTER);
        return panel;
    }

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

    private String formatGold(int gold) {
        return gold >= 1000 ? String.format("%.1fk", gold / 1000.0) : String.valueOf(gold);
    }

    private String formatNumber(int num) {
        if (num >= 1000000) return String.format("%.1fM", num / 1000000.0);
        if (num >= 1000) return String.format("%.1fk", num / 1000.0);
        return String.valueOf(num);
    }

    private JPanel createFooter() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 15));
        panel.setBackground(BG_DARK);

        JButton menuBtn = createButton("MENU PRINCIPAL", new Color(100, 100, 100));
        menuBtn.addActionListener(e -> {
            if (listener != null) listener.onReturnToMenu();
        });

        JButton replayBtn = createButton("REJOUER", Theme.ACCENT);
        replayBtn.addActionListener(e -> {
            if (listener != null) listener.onPlayAgain();
        });

        panel.add(menuBtn);
        panel.add(replayBtn);

        return panel;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setForeground(TEXT_PRIMARY);
        btn.setBackground(bg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10,25,10,25));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });

        return btn;
    }
}