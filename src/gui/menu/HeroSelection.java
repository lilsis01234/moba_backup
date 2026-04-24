package gui.menu;

import gui.ButtonLayout;
import gui.Theme;
import data.model.Hero;
import data.model.Category;
import engine.process.JsonDataProvider;
import engine.process.JsonDataProviderFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeroSelection extends JPanel {

    private static final long serialVersionUID = 1L;

    public interface HeroSelectionListener {
        void onHeroSelected(Hero hero);
        void onBack();
    }

    private static HeroSelection instance;
    private HeroSelectionListener listener;
    private Map<Integer, List<Hero>> heroesByCategory;
    private List<Category> categories;
    private int selectedCategoryIndex = 0;
    private int selectedHeroIndex = 0;
    private int hoveredCategoryIndex = -1;
    private int hoveredFooter = -1;

    private JPanel cardsContainer;
    private Rectangle[] categoryRects = new Rectangle[0];
    private Rectangle backRect = new Rectangle();
    private Rectangle selectRect = new Rectangle();

    private static final Color CATEGORY_FORCE        = new Color(180, 60, 60);
    private static final Color CATEGORY_AGILITY      = new Color(60, 160, 80);
    private static final Color CATEGORY_INTELLIGENCE = new Color(60, 100, 180);

    private HeroSelection(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(Theme.BACKGROUND_DARK);
        setFocusable(true);
        setLayout(new BorderLayout());

        try {
            JsonDataProvider provider = JsonDataProviderFactory.create();
            categories = provider.getAllCategories();
            groupHeroesByCategory(provider.getAllHeroes());
        } catch (IOException e) {
            categories = new ArrayList<>();
            heroesByCategory = new HashMap<>();
            e.printStackTrace();
        }

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCardsPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    navigate(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    navigate(1);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    selectCategory(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    selectCategory(1);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    confirmSelection();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (listener != null) listener.onBack();
                }
            }
        });

        refreshCards();
    }

    public static HeroSelection getInstance(Dimension screenSize) {
        if (instance == null) {
            instance = new HeroSelection(screenSize);
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private void groupHeroesByCategory(List<Hero> heroes) {
        heroesByCategory = new HashMap<>();
        for (Hero hero : heroes) {
            int catId = hero.getCategoryId();
            heroesByCategory.computeIfAbsent(catId, k -> new ArrayList<>()).add(hero);
        }
    }

    /**
     * Creates the top header panel containing the title and Back button.
     * Buttons are drawn manually in paintComponent 
     */
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        // Title + back button panel
        JPanel topRow = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                // Title
                g2.setFont(new Font("Serif", Font.BOLD, 36));
                g2.setColor(Theme.ACCENT);
                FontMetrics fm = g2.getFontMetrics();
                String title = "CHOISIR UN HEROS";
                g2.drawString(title, (getWidth() - fm.stringWidth(title)) / 2, 60);

                // Back button
                backRect.setBounds(30, 20, 90, 32);
                g2.setColor(Theme.BUTTON_BG);
                g2.fillRect(backRect.x, backRect.y, backRect.width, backRect.height);
                g2.setColor(Theme.BUTTON_BORDER);
                g2.drawRect(backRect.x, backRect.y, backRect.width, backRect.height);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g2.setColor(Theme.TEXT_DIM);
                FontMetrics fm2 = g2.getFontMetrics();
                String backLabel = "Retour";
                g2.drawString(backLabel,
                    backRect.x + (backRect.width - fm2.stringWidth(backLabel)) / 2,
                    backRect.y + (backRect.height + fm2.getAscent() - fm2.getDescent()) / 2);
            }
        };
        topRow.setOpaque(false);
        topRow.setPreferredSize(new Dimension(0, 90));

        topRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (backRect.contains(e.getPoint()) && listener != null)
                    listener.onBack();
            }
        });
        topRow.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                boolean over = backRect.contains(e.getPoint());
                topRow.setCursor(over
                    ? new Cursor(Cursor.HAND_CURSOR)
                    : new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        // Category bar
        JPanel categoryBar = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int bw = 130, bh = 38, gap = 20;
                int totalW = categories.size() * bw + (categories.size() - 1) * gap;
                int startX = (getWidth() - totalW) / 2;
                int y = (getHeight() - bh) / 2;

                categoryRects = new Rectangle[categories.size()];
                Color[] catColors = {CATEGORY_FORCE, CATEGORY_AGILITY, CATEGORY_INTELLIGENCE};

                for (int i = 0; i < categories.size(); i++) {
                    int x = startX + i * (bw + gap);
                    categoryRects[i] = new Rectangle(x, y, bw, bh);

                    boolean selected = (i == selectedCategoryIndex);
                    boolean hovered  = (i == hoveredCategoryIndex);
                    Color base = catColors[i % catColors.length];

                    // Category button background
                    g2.setColor(selected ? base : (hovered ? Theme.BUTTON_HOVER : Theme.BUTTON_BG));
                    g2.fillRect(x, y, bw, bh);

                    // Category button border
                    g2.setColor(selected ? Theme.ACCENT_BRIGHT : Theme.BUTTON_BORDER);
                    g2.setStroke(new BasicStroke(selected ? 3 : 2));
                    g2.drawRect(x, y, bw, bh);
                    g2.setStroke(new BasicStroke(1));

                    // Category label
                    g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                    g2.setColor(selected ? Color.WHITE : Theme.TEXT_DIM);
                    FontMetrics fm = g2.getFontMetrics();
                    String label = categories.get(i).getName().toUpperCase();
                    g2.drawString(label,
                        x + (bw - fm.stringWidth(label)) / 2,
                        y + (bh + fm.getAscent() - fm.getDescent()) / 2);
                }
            }
        };
        categoryBar.setOpaque(false);
        categoryBar.setPreferredSize(new Dimension(0, 60));

        categoryBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                for (int i = 0; i < categoryRects.length; i++) {
                    if (categoryRects[i].contains(e.getPoint())) {
                        selectedCategoryIndex = i;
                        selectedHeroIndex = 0;
                        categoryBar.repaint();
                        refreshCards();
                        requestFocusInWindow();
                        break;
                    }
                }
            }
        });
        categoryBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int prev = hoveredCategoryIndex;
                hoveredCategoryIndex = -1;
                for (int i = 0; i < categoryRects.length; i++) {
                    if (categoryRects[i].contains(e.getPoint())) {
                        hoveredCategoryIndex = i;
                        break;
                    }
                }
                categoryBar.setCursor(hoveredCategoryIndex >= 0
                    ? new Cursor(Cursor.HAND_CURSOR)
                    : new Cursor(Cursor.DEFAULT_CURSOR));
                if (hoveredCategoryIndex != prev) categoryBar.repaint();
            }
        });

        header.add(topRow, BorderLayout.NORTH);
        header.add(categoryBar, BorderLayout.CENTER);
        return header;
    }

    /**
     * Creates Scrollable cards container panel.
     */
    private JPanel createCardsPanel() {
        cardsContainer = new JPanel(new GridBagLayout());
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        return cardsContainer;
    }

    /**
     * Creates the bottom panel with keyboard hint and Select button.
     * Draw Button manually
     */
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Keyboard hint
                g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
                g2.setColor(Theme.TEXT_DIM);
                String hint = "<- -> naviguer  ^ v categories  ENTER confirmer  ECHAP retour";
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(hint, (getWidth() - fm.stringWidth(hint)) / 2, 18);

                // Select button
                int bw = 200, bh = 35;
                int x = (getWidth() - bw) / 2;
                int y = 28;
                selectRect.setBounds(x, y, bw, bh);

                Color bg = hoveredFooter == 1 ? Theme.ACCENT_BRIGHT.brighter() : Theme.ACCENT_BRIGHT;
                g2.setColor(bg);
                g2.fillRect(x, y, bw, bh);

                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.setColor(Theme.BACKGROUND_DARK);
                FontMetrics fm2 = g2.getFontMetrics();
                String label = "SELECTIONNER";
                g2.drawString(label,
                    x + (bw - fm2.stringWidth(label)) / 2,
                    y + (bh + fm2.getAscent() - fm2.getDescent()) / 2);
            }
        };
        bottom.setOpaque(false);
        bottom.setPreferredSize(new Dimension(0, 70));

        bottom.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (selectRect.contains(e.getPoint())) confirmSelection();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredFooter = -1;
                bottom.repaint();
            }
        });
        bottom.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int prev = hoveredFooter;
                hoveredFooter = selectRect.contains(e.getPoint()) ? 1 : -1;
                bottom.setCursor(hoveredFooter == 1
                    ? new Cursor(Cursor.HAND_CURSOR)
                    : new Cursor(Cursor.DEFAULT_CURSOR));
                if (hoveredFooter != prev) bottom.repaint();
            }
        });

        return bottom;
    }

    /**
     * Rebuilds the hero cards grid for the currently selected category.
     */
    private void refreshCards() {
        cardsContainer.removeAll();

        List<Hero> heroes = heroesByCategory.getOrDefault(selectedCategoryIndex + 1, new ArrayList<>());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        for (int i = 0; i < heroes.size(); i++) {
            final int index = i;
            Hero hero = heroes.get(i);
            boolean isSelected = (i == selectedHeroIndex);

            JPanel card = buildCard(hero, isSelected);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedHeroIndex = index;
                    refreshCards();
                    requestFocusInWindow();
                }
            });

            gbc.gridx = i;
            gbc.gridy = 0;
            cardsContainer.add(card, gbc);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    /**
     * Builds a single hero card panel with sprite, stats, and Lore button.
     */
    private JPanel buildCard(Hero hero, boolean isSelected) {
        Color[] categoryColors = {CATEGORY_FORCE, CATEGORY_AGILITY, CATEGORY_INTELLIGENCE};
        Color borderColor = categoryColors[selectedCategoryIndex % categoryColors.length];

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(220, 360));
        card.setMaximumSize(new Dimension(220, 360));
        card.setBackground(isSelected ? new Color(50, 45, 60) : Theme.BUTTON_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                isSelected ? Theme.ACCENT_BRIGHT : Theme.BUTTON_BORDER,
                isSelected ? 3 : 1
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Sprite preview
        SpritePreview preview = new SpritePreview(hero.getSpriteFile());
        preview.setPreferredSize(new Dimension(90, 90));
        preview.setMinimumSize(new Dimension(90, 90));
        preview.setMaximumSize(new Dimension(90, 90));
        preview.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(preview);

        // Hero name
        JLabel nameLabel = new JLabel(hero.getName() != null ? hero.getName() : "???", SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(isSelected ? Theme.ACCENT_BRIGHT : Theme.TEXT_MAIN);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));
        card.add(nameLabel);

        // Stats grid
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 8, 5));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(200, 90));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statsPanel.add(createStatLabel("HP",   hero.getMaxHp(),                    CATEGORY_FORCE));
        statsPanel.add(createStatLabel("ATK",  hero.getAttack(),                   new Color(200, 80, 80)));
        statsPanel.add(createStatLabel("DEF",  hero.getDefense(),                  new Color(80, 150, 200)));
        statsPanel.add(createStatLabel("SPD",  (int) hero.getSpeed(),              new Color(180, 180, 80)));
        statsPanel.add(createStatLabel("MANA", hero.getMaxMana(),                  new Color(80, 120, 220)));
        statsPanel.add(createStatLabel("ASPD", (int)(hero.getAttackSpeed() * 100), new Color(180, 100, 200)));
        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statsPanel);

        card.add(Box.createVerticalStrut(10));

        // Lore button 
        JPanel loreBtn = new JPanel(null) {
            private boolean hovered = false;

            {
                setOpaque(false);
                setPreferredSize(new Dimension(190, 30));
                setMaximumSize(new Dimension(190, 30));
                setAlignmentX(Component.CENTER_ALIGNMENT);

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e)  { showLoreDialog(hero); }
                    @Override
                    public void mouseEntered(MouseEvent e)  { hovered = true;  setCursor(new Cursor(Cursor.HAND_CURSOR));    repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e)   { hovered = false; setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setFont(new Font("SansSerif", Font.BOLD, 13));
                g2.setColor(hovered ? Theme.ACCENT_BRIGHT.brighter() : Theme.ACCENT_BRIGHT);
                FontMetrics fm = g2.getFontMetrics();
                String label = "Lore";
                g2.drawString(label,
                    (getWidth() - fm.stringWidth(label)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        card.add(loreBtn);

        return card;
    }

    /**
     * Opens a modal dialog showing the hero's lore text.
     */
    private void showLoreDialog(Hero hero) {
        JDialog dialog = new JDialog(
            SwingUtilities.getWindowAncestor(this) instanceof Frame
                ? (Frame) SwingUtilities.getWindowAncestor(this) : null,
            hero.getName(), true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BACKGROUND_DARK);
        dialog.setLayout(new BorderLayout());

        // Hero name header
        JLabel nameLabel = new JLabel(hero.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Serif", Font.BOLD, 28));
        nameLabel.setForeground(Theme.ACCENT_BRIGHT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        dialog.add(nameLabel, BorderLayout.NORTH);

        // Lore text
        JTextArea loreArea = new JTextArea(hero.getHistory() != null ? hero.getHistory() : "");
        loreArea.setFont(new Font("Serif", Font.PLAIN, 16));
        loreArea.setForeground(new Color(200, 190, 220));
        loreArea.setBackground(Theme.BACKGROUND_DARK);
        loreArea.setEditable(false);
        loreArea.setLineWrap(true);
        loreArea.setWrapStyleWord(true);
        loreArea.setFocusable(false);
        loreArea.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        dialog.add(loreArea, BorderLayout.CENTER);

        // Close button 
        Rectangle closeRect = new Rectangle();
        JPanel closePanel = new JPanel(null) {
            private boolean hovered = false;

            {
                setOpaque(false);
                setPreferredSize(new Dimension(0, 50));

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e)  { if (closeRect.contains(e.getPoint())) dialog.dispose(); }
                    @Override
                    public void mouseEntered(MouseEvent e)  { hovered = true;  setCursor(new Cursor(Cursor.HAND_CURSOR));    repaint(); }
                    @Override
                    public void mouseExited(MouseEvent e)   { hovered = false; setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); repaint(); }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;

                int bw = 100, bh = 32;
                int x = (getWidth() - bw) / 2, y = 9;
                closeRect.setBounds(x, y, bw, bh);

                g2.setColor(hovered ? Theme.BUTTON_HOVER : Theme.BUTTON_BG);
                g2.fillRect(x, y, bw, bh);
                g2.setColor(Theme.BUTTON_BORDER);
                g2.drawRect(x, y, bw, bh);

                g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g2.setColor(Theme.TEXT_DIM);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("Fermer",
                    x + (bw - fm.stringWidth("Fermer")) / 2,
                    y + (bh + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        dialog.add(closePanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JLabel createStatLabel(String label, int value, Color color) {
        JLabel lbl = new JLabel(label + ": " + value, SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        lbl.setForeground(color);
        return lbl;
    }

    private void navigate(int dir) {
        List<Hero> heroes = heroesByCategory.getOrDefault(selectedCategoryIndex + 1, new ArrayList<>());
        if (heroes.isEmpty()) return;
        selectedHeroIndex = (selectedHeroIndex + dir + heroes.size()) % heroes.size();
        refreshCards();
    }

    private void selectCategory(int dir) {
        selectedCategoryIndex = (selectedCategoryIndex + dir + categories.size()) % categories.size();
        selectedHeroIndex = 0;
        refreshCards();
        requestFocusInWindow();
    }

    private void confirmSelection() {
        List<Hero> heroes = heroesByCategory.getOrDefault(selectedCategoryIndex + 1, new ArrayList<>());
        if (listener != null && !heroes.isEmpty()) {
            listener.onHeroSelected(heroes.get(selectedHeroIndex));
        }
    }

    public void setHeroSelectionListener(HeroSelectionListener listener) {
        this.listener = listener;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, Theme.BACKGROUND_DARK, 0, getHeight(), new Color(10, 10, 20));
        g2.setPaint(gradient);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }
}