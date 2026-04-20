package gui.menu;

import data.model.GameStats;
import data.model.HeroStats;
import data.model.TeamStats;
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
    
    private Color BLUE_TEAM = new Color(70, 130, 180);
    private Color RED_TEAM = new Color(180, 70, 70);
    private Color BG = new Color(240, 240, 245);
    private Color CARD_BG = Color.WHITE;

    public interface AfterGameListener {
        void onReturnToMenu();
        void onPlayAgain();
    }

    public AfterGamePanel(GameStats stats, Dimension screenSize, AfterGameListener listener) {
        this.gameStats = stats;
        this.screenSize = screenSize;
        this.listener = listener;
        setPreferredSize(screenSize);
        setBackground(BG);
        setLayout(new BorderLayout(10, 10));

        add(createHeader(), BorderLayout.NORTH);
        add(createTabPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        setFocusable(true);
        requestFocusInWindow();
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(40, 40, 50));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        String resultText = "MATCH FINI";
        if (gameStats != null && gameStats.getGameResult() != null) {
            resultText = gameStats.getGameResult().equals("WIN") ? "VICTOIRE!" : "DÉFAITE";
        }

        JLabel title = new JLabel(resultText, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        panel.add(title, BorderLayout.CENTER);

        if (gameStats != null) {
            long secs = gameStats.getGameDuration() / 1000;
            String time = String.format("%02d:%02d", secs / 60, secs % 60);
            JLabel duration = new JLabel("Durée: " + time);
            duration.setFont(new Font("Arial", Font.PLAIN, 14));
            duration.setForeground(new Color(200, 200, 200));
            panel.add(duration, BorderLayout.EAST);
        }

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
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        TeamStats blue = gameStats != null ? gameStats.getTeamStats(0) : null;
        TeamStats red = gameStats != null ? gameStats.getTeamStats(1) : null;

        JPanel teamsPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        teamsPanel.setBackground(BG);
        teamsPanel.add(createTeamCard("ÉQUIPE BLEUE (VOTRE ÉQUIPE)", blue, BLUE_TEAM, true));
        teamsPanel.add(createTeamCard("ÉQUIPE ROUGE (ENNEMIE)", red, RED_TEAM, false));

        panel.add(teamsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTeamCard(String title, TeamStats team, Color color, boolean isPlayerTeam) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(0, 2, 10, 8));
        stats.setBackground(CARD_BG);

        if (team != null) {
            addStatRow(stats, "Kills", String.valueOf(team.getTotalKills()));
            addStatRow(stats, "Deaths", String.valueOf(team.getTotalDeaths()));
            addStatRow(stats, "Assists", String.valueOf(team.getTotalAssists()));
            addStatRow(stats, "KDA", String.format("%.2f", team.getTeamKDA()));
            addStatRow(stats, "Tours détruites", String.valueOf(team.getTowersDestroyed()));
            addStatRow(stats, "Or total", formatGold(team.getGoldEarned()));
            addStatRow(stats, "Dégats totaux", formatNumber(team.getTotalDamageDealt()));
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
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        addStatRow(panel, "Niveau", String.valueOf(player.getLevel()));
        addStatRow(panel, "CS", String.valueOf(player.getCsCreeps()));
        addStatRow(panel, "GPM", String.valueOf(player.getGoldPerMinute()));
        addStatRow(panel, "Net Worth", player.getNetWorth());

        return panel;
    }

    private JPanel createPerformanceTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
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

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        chart.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(CARD_BG);

        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createEconomyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
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
        
        PiePlot pie = (PiePlot) chart.getPlot();
        pie.setBackgroundPaint(Color.WHITE);
        chart.setBackgroundPaint(Color.WHITE);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(CARD_BG);

        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createItemsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel label = new JLabel("Inventaire final", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.GRAY);
        
        JPanel placeholder = new JPanel();
        placeholder.setBackground(CARD_BG);
        placeholder.add(label);
        
        panel.add(placeholder, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMVPTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel mvpCard = new JPanel(new BorderLayout(20, 20));
        mvpCard.setBackground(CARD_BG);
        mvpCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BLUE_TEAM, 3),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel mvpTitle = new JLabel("🏆 MVP", SwingConstants.CENTER);
        mvpTitle.setFont(new Font("Arial", Font.BOLD, 28));
        mvpTitle.setForeground(BLUE_TEAM);
        mvpCard.add(mvpTitle, BorderLayout.NORTH);

        JPanel stats = new JPanel(new GridLayout(0, 2, 20, 15));
        stats.setBackground(CARD_BG);

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
        lbl.setFont(new Font("Arial", Font.PLAIN, 13));
        lbl.setForeground(Color.GRAY);
        parent.add(lbl);

        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 13));
        val.setForeground(Color.DARK_GRAY);
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
        panel.setBackground(BG);

        JButton menuBtn = createButton("MENU PRINCIPAL", new Color(100, 100, 100));
        menuBtn.addActionListener(e -> {
            if (listener != null) listener.onReturnToMenu();
        });

        JButton replayBtn = createButton("REJOUER", BLUE_TEAM);
        replayBtn.addActionListener(e -> {
            if (listener != null) listener.onPlayAgain();
        });

        panel.add(menuBtn);
        panel.add(replayBtn);

        return panel;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        final Color baseColor = color;

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Window win = SwingUtilities.getWindowAncestor(btn);
                if (win != null) win.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.setBackground(new Color(
                    Math.max(0, baseColor.getRed() - 30),
                    Math.max(0, baseColor.getGreen() - 30),
                    Math.max(0, baseColor.getBlue() - 30)
                ));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                Window win = SwingUtilities.getWindowAncestor(btn);
                if (win != null) win.setCursor(Cursor.getDefaultCursor());
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }
}