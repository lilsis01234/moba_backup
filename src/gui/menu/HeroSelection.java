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

    private JPanel categoryPanel;
    private JPanel cardsContainer;
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
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 20, 50));

        backButton = new JButton("  ← Retour  ");
        styleBackButton(backButton);
        backButton.addActionListener(e -> {
            if (listener != null) listener.onBack();
        });
        topPanel.add(backButton, BorderLayout.WEST);

        titleLabel = new JLabel("CHOISIR UN HÉROS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(Theme.ACCENT);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        categoryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            JButton btn = createCategoryButton(cat.getName(), i);
            categoryButtons.add(btn);
            categoryPanel.add(btn);
        }

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(categoryPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        cardsContainer = new JPanel(new GridBagLayout());
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        add(cardsContainer, BorderLayout.CENTER);

        hintLabel = new JLabel("← → naviguer  ↑ ↓ catégories  ENTRÉE confirmer  ÉCHAP retour", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hintLabel.setForeground(Theme.TEXT_DIM);
        hintLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        add(hintLabel, BorderLayout.SOUTH);

        updateCategoryButtons();
        refreshCards();
    }

    private JButton createCategoryButton(String name, int index) {
        JButton btn = new JButton(name.toUpperCase());
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Theme.TEXT_DIM);
        btn.setBackground(Theme.BUTTON_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BUTTON_BORDER, 2),
            BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        btn.setFocusPainted(false);
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
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setForeground(Theme.TEXT_DIM);
        btn.setBackground(Theme.BUTTON_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BUTTON_BORDER, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
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
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.ACCENT_BRIGHT, 3),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
                ));
            } else {
                btn.setBackground(Theme.BUTTON_BG);
                btn.setForeground(Theme.TEXT_DIM);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BUTTON_BORDER, 2),
                    BorderFactory.createEmptyBorder(10, 25, 10, 25)
                ));
            }
        }
    }

    private void refreshCards() {
        cardsContainer.removeAll();

        List<Hero> currentCategoryHeroes = heroesByCategory.getOrDefault(selectedCategoryIndex + 1, new ArrayList<>());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

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

    private JPanel buildCard(Hero hero, boolean isSelected, int categoryIndex) {
        Color[] categoryColors = {CATEGORY_FORCE, CATEGORY_AGILITY, CATEGORY_INTELLIGENCE};
        Color catColor = categoryColors[categoryIndex % categoryColors.length];

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(180, 260));
        card.setMaximumSize(new Dimension(180, 260));
        card.setBackground(isSelected ? new Color(50, 45, 60) : Theme.BUTTON_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                isSelected ? Theme.ACCENT_BRIGHT : Theme.BUTTON_BORDER, 
                isSelected ? 3 : 1
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel heroIcon = new JPanel();
        heroIcon.setPreferredSize(new Dimension(140, 30));
        heroIcon.setOpaque(false);
        heroIcon.setOpaque(false);
        heroIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(heroIcon);

        JLabel nameLabel = new JLabel(hero.getName() != null ? hero.getName() : "???", SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        nameLabel.setForeground(isSelected ? Theme.ACCENT_BRIGHT : Theme.TEXT_MAIN);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 8, 0));
        card.add(nameLabel);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 8, 5));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(150, 45));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        statsPanel.add(createStatLabel("HP", hero.getMaxHp(), CATEGORY_FORCE));
        statsPanel.add(createStatLabel("ATK", hero.getAttack(), new Color(200, 80, 80)));
        statsPanel.add(createStatLabel("DEF", hero.getDefense(), new Color(80, 150, 200)));
        statsPanel.add(createStatLabel("SPD", (int)hero.getSpeed(), new Color(180, 180, 80)));

        statsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statsPanel);

        if (isSelected) {
            card.add(Box.createVerticalStrut(8));
            JButton selectBtn = new JButton("SÉLECTIONNER");
            selectBtn.setFont(new Font("SansSerif", Font.BOLD, 11));
            selectBtn.setForeground(Theme.BACKGROUND_DARK);
            selectBtn.setBackground(Theme.ACCENT_BRIGHT);
            selectBtn.setBorderPainted(false);
            selectBtn.setFocusPainted(false);
            selectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            selectBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            selectBtn.setMaximumSize(new Dimension(150, 28));
            selectBtn.addActionListener(e -> confirmSelection());
            card.add(selectBtn);
        }

        return card;
    }

    private JLabel createStatLabel(String label, int value, Color color) {
        JLabel lbl = new JLabel(label + ": " + value, SwingConstants.CENTER);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 10));
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
