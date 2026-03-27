package gui.menu;

import gui.Theme;
import data.model.Hero;
import engine.process.JsonDataProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;

public class HeroSelection extends JPanel {

    public interface HeroSelectionListener {
        void onHeroSelected(Hero hero);
        void onBack();
    }

    private HeroSelectionListener listener;
    private List<Hero> heroes;
    private int selectedIndex = 0;
    private int page = 0;
    private final int cardsPerPage = 4;

    // Composants Swing
    private JPanel cardsPanel;
    private JButton prevButton;
    private JButton nextButton;
    private JLabel pageLabel;
    private JLabel titleLabel;
    private JLabel hintLabel;

    public HeroSelection(Dimension screenSize) {
        setPreferredSize(screenSize);
        setBackground(Theme.BACKGROUND_DARK);
        setFocusable(true);
        setLayout(new BorderLayout());

        try {
            JsonDataProvider provider = new JsonDataProvider();
            heroes = provider.getAllHeroes();
        } catch (IOException e) {
            heroes = new java.util.ArrayList<>();
            e.printStackTrace();
        }

        buildUI();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
            	switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:   navigate(-1); break;
                case KeyEvent.VK_RIGHT:  navigate(1); break;
                case KeyEvent.VK_ENTER:  confirmSelection(); break;
                case KeyEvent.VK_ESCAPE: if (listener != null) listener.onBack(); break;
            }
            }
        });
    }

    private void buildUI() {
        // Titre
        titleLabel = new JLabel("CHOISIR UN HÉROS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(Theme.ACCENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        titleLabel.setOpaque(false);
        add(titleLabel, BorderLayout.NORTH);

        // Centre : flèches et cartes
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        prevButton = new JButton("<");
        styleArrowButton(prevButton);
        prevButton.addActionListener(e -> {
            if (page > 0) {
                page--;
                selectedIndex = page * cardsPerPage;
                refreshCards();
            }
            requestFocusInWindow();
        });

        nextButton = new JButton(">");
        styleArrowButton(nextButton);
        nextButton.addActionListener(e -> {
            int maxPage = (heroes.size() - 1) / cardsPerPage;
            if (page < maxPage) {
                page++;
                selectedIndex = page * cardsPerPage;
                refreshCards();
            }
            requestFocusInWindow();
        });

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        cardsPanel.setOpaque(false);

        centerPanel.add(prevButton, BorderLayout.WEST);
        centerPanel.add(cardsPanel, BorderLayout.CENTER);
        centerPanel.add(nextButton, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // Bas : page et utilisation
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        pageLabel = new JLabel("", SwingConstants.CENTER);
        pageLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        pageLabel.setForeground(Theme.TEXT_DIM);

        hintLabel = new JLabel("← → pour naviguer   ENTRÉE pour confirmer   ÉCHAP pour retour", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        hintLabel.setForeground(Theme.TEXT_DIM);

        bottomPanel.add(pageLabel, BorderLayout.NORTH);
        bottomPanel.add(hintLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshCards();
    }

    private void styleArrowButton(JButton btn) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 30));
        btn.setForeground(Theme.ACCENT);
        btn.setBackground(Theme.BACKGROUND_DARK);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(60, 60));
        btn.setOpaque(true);
    }

    private void refreshCards() {
        cardsPanel.removeAll();

        int start = page * cardsPerPage;
        int end = Math.min(start + cardsPerPage, heroes.size());

        for (int i = start; i < end; i++) {
            final int index = i;
            Hero hero = heroes.get(i);
            boolean isSelected = (i == selectedIndex);

            JPanel card = buildCard(hero, isSelected);
            card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (selectedIndex == index) {
                        confirmSelection();
                    } else {
                        selectedIndex = index;
                        refreshCards();
                        requestFocusInWindow();
                    }
                }
            });
            cardsPanel.add(card);
        }

        // Mise à jour page label
        int maxPage = heroes.isEmpty() ? 0 : (heroes.size() - 1) / cardsPerPage;
        pageLabel.setText((page + 1) + " / " + (maxPage + 1));
        prevButton.setVisible(page > 0);
        nextButton.setVisible(page < maxPage);

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel buildCard(Hero hero, boolean isSelected) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(160, 220));
        card.setBackground(isSelected ? Theme.BUTTON_HOVER : Theme.BUTTON_BG);

        card.add(new JLabel(" ")); // espace

        JLabel nameLabel = new JLabel(hero.getName() != null ? hero.getName() : "???", SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLabel.setForeground(isSelected ? Theme.TEXT_MAIN : Theme.TEXT_DIM);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);

        card.add(new JLabel(" ")); // espace

        JLabel catLabel = new JLabel("Cat. " + hero.getCategoryId(), SwingConstants.CENTER);
        catLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        catLabel.setForeground(Theme.TEXT_DIM);
        catLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(catLabel);

        card.add(new JLabel(" ")); // espace
        card.add(new JLabel(" ")); // espace

        String[] stats = {
            "HP:  " + hero.getMaxHp(),
            "ATK: " + hero.getAttack(),
            "DEF: " + hero.getDefense(),
            "SPD: " + hero.getSpeed()
        };
        for (String stat : stats) {
            JLabel statLabel = new JLabel("  " + stat);
            statLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
            statLabel.setForeground(Theme.TEXT_DIM);
            card.add(statLabel);
        }

        return card;
    }
    public void setHeroSelectionListener(HeroSelectionListener listener) {
        this.listener = listener;
    }

    private void navigate(int dir) {
        if (heroes.isEmpty()) return;
        selectedIndex = (selectedIndex + dir + heroes.size()) % heroes.size();
        page = selectedIndex / cardsPerPage;
        refreshCards();
    }

    private void confirmSelection() {
        if (listener != null && !heroes.isEmpty()) {
            listener.onHeroSelected(heroes.get(selectedIndex));
        }
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