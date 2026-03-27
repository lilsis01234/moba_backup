package gui.menu;

import gui.Theme;
import data.model.Hero;
import data.model.Category;
import engine.process.JsonDataProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeroSelection extends JPanel {

    public interface HeroSelectionListener {
        void onHeroSelected(Hero hero);
        void onBack();
    }

    private HeroSelectionListener listener;
    private Map<Integer, List<Hero>> heroesByCategory;
    private List<Category> categories;
    private int selectedCategoryIndex = 0;
    private int selectedHeroIndex = 0;
    private Hero selectedHero;

    private JPanel categoryPanel;
    private JPanel cardsPanel;
    private JLabel titleLabel;
    private JLabel hintLabel;
    private JButton backButton;
    private List<JButton> categoryButtons = new ArrayList<>();

    private static final Color CATEGORY_FORCE = new Color(180, 60, 60);
    private static final Color CATEGORY_AGILITY = new Color(60, 160, 80);
    private static final Color CATEGORY_INTELLIGENCE = new Color(60, 100, 180);

    public HeroSelection(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(Theme.BACKGROUND_DARK);
        setFocusable(true);
        setLayout(new BorderLayout());

        try {
            JsonDataProvider provider = new JsonDataProvider();
            categories = provider.getAllCategories();
            groupHeroesByCategory(provider.getAllHeroes());
        } catch (IOException e) {
            categories = new ArrayList<>();
            heroesByCategory = new HashMap<>();
            e.printStackTrace();
        }

        buildUI();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:   navigate(-1); break;
                    case KeyEvent.VK_RIGHT:  navigate(1); break;
                    case KeyEvent.VK_UP:     selectCategory(-1); break;
                    case KeyEvent.VK_DOWN:   selectCategory(1); break;
                    case KeyEvent.VK_ENTER:  confirmSelection(); break;
                    case KeyEvent.VK_ESCAPE: if (listener != null) listener.onBack(); break;
                }
            }
        });
    }

    private void groupHeroesByCategory(List<Hero> heroes) {
        heroesByCategory = new HashMap<>();
        for (Hero hero : heroes) {
            int catId = hero.getCategoryId();
            heroesByCategory.computeIfAbsent(catId, k -> new ArrayList<>()).add(hero);
        }
    }

    private void buildUI() {
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        titleLabel = new JLabel("CHOISIR UN HÉROS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Theme.ACCENT);
        northPanel.add(titleLabel, BorderLayout.NORTH);

        backButton = new JButton("← RETOUR");
        styleBackButton(backButton);
        backButton.addActionListener(e -> {
            if (listener != null) listener.onBack();
        });
        northPanel.add(backButton, BorderLayout.WEST);

        categoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        categoryPanel.setOpaque(false);

        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            JButton btn = createCategoryButton(cat.getName(), i);
            categoryButtons.add(btn);
            categoryPanel.add(btn);
        }

        JPanel categoryWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        categoryWrapper.setOpaque(false);
        categoryWrapper.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        categoryWrapper.add(categoryPanel);
        northPanel.add(categoryWrapper, BorderLayout.CENTER);

        add(northPanel, BorderLayout.NORTH);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 30));
        cardsPanel.setOpaque(false);
        add(cardsPanel, BorderLayout.CENTER);

        hintLabel = new JLabel("← → pour naviguer   ↑ ↓ catégories   ENTRÉE pour confirmer   ÉCHAP pour retour", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        hintLabel.setForeground(Theme.TEXT_DIM);
        hintLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 25, 0));
        add(hintLabel, BorderLayout.SOUTH);

        updateCategoryButtons();
        refreshCards();
    }

    private JButton createCategoryButton(String name, int index) {
        JButton btn = new JButton(name.toUpperCase());
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Theme.TEXT_DIM);
        btn.setBackground(Theme.BUTTON_BG);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BUTTON_BORDER, 2));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> {
            selectedCategoryIndex = index;
            selectedHeroIndex = 0;
            updateCategoryButtons();
            refreshCards();
            requestFocusInWindow();
        });
        return btn;
    }

    private void styleBackButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(Theme.TEXT_DIM);
        btn.setBackground(Theme.BUTTON_BG);
        btn.setBorder(BorderFactory.createLineBorder(Theme.BUTTON_BORDER, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void updateCategoryButtons() {
        Color[] categoryColors = {CATEGORY_FORCE, CATEGORY_AGILITY, CATEGORY_INTELLIGENCE};
        
        for (int i = 0; i < categoryButtons.size(); i++) {
            JButton btn = categoryButtons.get(i);
            if (i == selectedCategoryIndex) {
                Color catColor = categoryColors[i % categoryColors.length];
                btn.setBackground(catColor);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(Theme.ACCENT_BRIGHT, 3));
            } else {
                btn.setBackground(Theme.BUTTON_BG);
                btn.setForeground(Theme.TEXT_DIM);
                btn.setBorder(BorderFactory.createLineBorder(Theme.BUTTON_BORDER, 2));
            }
        }
    }

    private void refreshCards() {
        cardsPanel.removeAll();

        List<Hero> currentCategoryHeroes = heroesByCategory.getOrDefault(selectedCategoryIndex + 1, new ArrayList<>());

        for (int i = 0; i < currentCategoryHeroes.size(); i++) {
            final int index = i;
            Hero hero = currentCategoryHeroes.get(i);
            boolean isSelected = (i == selectedHeroIndex);

            JPanel card = buildCard(hero, isSelected, selectedCategoryIndex);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedHeroIndex = index;
                    selectedHero = hero;
                    refreshCards();
                    requestFocusInWindow();
                }
            });
            cardsPanel.add(card);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel buildCard(Hero hero, boolean isSelected, int categoryIndex) {
        Color[] categoryColors = {CATEGORY_FORCE, CATEGORY_AGILITY, CATEGORY_INTELLIGENCE};
        Color catColor = categoryColors[categoryIndex % categoryColors.length];

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(200, 280));
        card.setBackground(isSelected ? new Color(50, 45, 60) : Theme.BUTTON_BG);
        card.setBorder(BorderFactory.createLineBorder(
            isSelected ? Theme.ACCENT_BRIGHT : Theme.BUTTON_BORDER, 
            isSelected ? 3 : 1
        ));

        JPanel heroIcon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(catColor);
                g2.fillOval(30, 20, 100, 100);
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setFont(new Font("Serif", Font.BOLD, 48));
                FontMetrics fm = g2.getFontMetrics();
                String initial = hero.getName() != null ? hero.getName().substring(0, 1) : "?";
                g2.drawString(initial, 55 + (50 - fm.stringWidth(initial) / 2), 80);
            }
        };
        heroIcon.setPreferredSize(new Dimension(160, 120));
        heroIcon.setOpaque(false);
        heroIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heroIcon);

        JLabel nameLabel = new JLabel(hero.getName() != null ? hero.getName() : "???", SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setForeground(isSelected ? Theme.ACCENT_BRIGHT : Theme.TEXT_MAIN);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 5, 0));
        card.add(nameLabel);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(160, 50));

        statsPanel.add(createStatLabel("HP", hero.getMaxHp(), CATEGORY_FORCE));
        statsPanel.add(createStatLabel("ATK", hero.getAttack(), new Color(200, 80, 80)));
        statsPanel.add(createStatLabel("DEF", hero.getDefense(), new Color(80, 150, 200)));
        statsPanel.add(createStatLabel("SPD", (int)hero.getSpeed(), new Color(180, 180, 80)));

        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statsPanel);

        if (isSelected) {
            JButton selectBtn = new JButton("SÉLECTIONNER");
            selectBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            selectBtn.setForeground(Theme.BACKGROUND_DARK);
            selectBtn.setBackground(Theme.ACCENT_BRIGHT);
            selectBtn.setBorderPainted(false);
            selectBtn.setFocusPainted(false);
            selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            selectBtn.setMaximumSize(new Dimension(160, 30));
            selectBtn.addActionListener(e -> confirmSelection());
            card.add(Box.createVerticalStrut(10));
            card.add(selectBtn);
        }

        return card;
    }

    private JLabel createStatLabel(String label, int value, Color color) {
        JLabel lbl = new JLabel(label + ": " + value, SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setForeground(color);
        return lbl;
    }

    private void navigate(int dir) {
        List<Hero> currentCategoryHeroes = heroesByCategory.getOrDefault(selectedCategoryIndex + 1, new ArrayList<>());
        if (currentCategoryHeroes.isEmpty()) return;
        
        selectedHeroIndex = (selectedHeroIndex + dir + currentCategoryHeroes.size()) % currentCategoryHeroes.size();
        refreshCards();
    }

    private void selectCategory(int dir) {
        selectedCategoryIndex = (selectedCategoryIndex + dir + categories.size()) % categories.size();
        selectedHeroIndex = 0;
        updateCategoryButtons();
        refreshCards();
        requestFocusInWindow();
    }

    private void confirmSelection() {
        List<Hero> currentCategoryHeroes = heroesByCategory.getOrDefault(selectedCategoryIndex + 1, new ArrayList<>());
        if (listener != null && !currentCategoryHeroes.isEmpty()) {
            listener.onHeroSelected(currentCategoryHeroes.get(selectedHeroIndex));
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
