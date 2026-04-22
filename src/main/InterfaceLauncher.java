package main;

import engine.process.Arena;
import engine.process.BotManager;
import engine.process.EquipmentLoader;
import engine.process.JsonDataProviderFactory;
import engine.process.Lane;
import engine.process.MinionSpawner;
import engine.process.ShopManager;
import engine.map.TilesManager;

import gui.HUDRenderer;
import gui.LoadingScreen;
import gui.PauseMenu;
import gui.ShopPanel;
import gui.game.ArenaPanel;
import gui.menu.GameOverScreen;
import gui.menu.HeroSelection;
import gui.menu.MainMenu;
import game_config.GameConfiguration;
import data.model.Hero;
import data.model.HeroStats;
import data.model.TeamStats;
import data.model.GameStats;
import gui.menu.AfterGamePanel;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InterfaceLauncher extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;

    private MainMenu mainMenu;
    private int screenWidth;
    private int screenHeight;
    private boolean isGameRunning = false;
    private boolean isPaused = false;
    private JFrame gameFrame;
    private ArenaPanel panel;
    private Arena arena;
    private boolean gameOver = false;
    private JLayeredPane glassPane;
    private PauseMenu pauseMenu;

    public InterfaceLauncher() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int) screenSize.getWidth();
        screenHeight = (int) screenSize.getHeight();

        setTitle("MOBA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        showMainMenu();
        setVisible(true);
        new Thread(this).start();
    }

    private void showMainMenu() {
        getContentPane().removeAll();

        mainMenu = MainMenu.getInstance(new Dimension(screenWidth, screenHeight));
        mainMenu.setMenuListener(new MainMenu.MenuListener() {
            @Override
            public void onStartGame() {
                startGame();
            }

            @Override
            public void onExit() {
                System.exit(0);
            }
        });

        add(mainMenu);
        setSize(screenWidth, screenHeight);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainMenu.requestFocusInWindow();
    }

    private void showAfterGameScreen(String result) {
        if (arena == null) return;
        
        GameStats stats = arena.buildGameStats(result);
        
        AfterGamePanel screen = new AfterGamePanel(stats,
            new Dimension(screenWidth, screenHeight),
            new AfterGamePanel.AfterGameListener() {
                @Override
                public void onReturnToMenu() {
                    if (gameFrame != null) {
                        gameFrame.setVisible(false);
                    }
                    gameOver = false;
                    isGameRunning = false;
                    isPaused = false;
                    resetGameState();
                    arena = null;
                    setVisible(true);
                    showMainMenu();
                }
                
                @Override
                public void onPlayAgain() {
                    if (gameFrame != null) {
                        gameFrame.setVisible(false);
                    }
                    gameOver = false;
                    isGameRunning = false;
                    isPaused = false;
                    resetGameState();
                    arena = null;
                    startGame();
                }
            });

        gameFrame.getContentPane().removeAll();
        JPanel glass = new JPanel();
        glass.setOpaque(false);
        gameFrame.setGlassPane(glass);
        glass.setVisible(false);
        gameFrame.add(screen);
        gameFrame.revalidate();
        gameFrame.repaint();
        screen.requestFocusInWindow();
    }

    private void showGameOver(String result) {
        if (arena == null) return;
        
        GameStats stats = arena.buildGameStats(result);
        
        JFrame gameOverFrame = new JFrame("Game Over");
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.setSize(screenWidth, screenHeight);
        gameOverFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        AfterGamePanel screen = new AfterGamePanel(stats,
            new Dimension(screenWidth, screenHeight),
            new AfterGamePanel.AfterGameListener() {
                @Override
                public void onReturnToMenu() {
                    gameOverFrame.dispose();
                    gameOver = false;
                    resetGameState();
                    setVisible(true);
                    showMainMenu();
                }
                
                @Override
                public void onPlayAgain() {
                    gameOverFrame.dispose();
                    gameOver = false;
                    resetGameState();
                    startGame();
                }
            });

        gameOverFrame.add(screen);
        gameOverFrame.setVisible(true);
        screen.requestFocusInWindow();
    }

    private void startGame() {
        setVisible(false);
        showHeroSelection();
    }

    private void showHeroSelection() {
        JFrame selectionFrame = new JFrame("MOBA - Sélection du héros");
        selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectionFrame.setSize(screenWidth, screenHeight);
        selectionFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        HeroSelection heroSelection = HeroSelection.getInstance(new Dimension(screenWidth, screenHeight));
        heroSelection.setHeroSelectionListener(new HeroSelection.HeroSelectionListener() {
            @Override
            public void onHeroSelected(Hero hero) {
                selectionFrame.dispose();
                launchGame(hero);
            }

            @Override
            public void onBack() {
                selectionFrame.dispose();
                setVisible(true);
            }
        });

        selectionFrame.add(heroSelection);
        selectionFrame.setVisible(true);
        heroSelection.requestFocusInWindow();
    }

    private void launchGame(Hero hero) {
        getContentPane().removeAll();
        
        final LoadingScreen loadingScreen = new LoadingScreen(screenWidth, screenHeight);
        loadingScreen.setBounds(0, 0, screenWidth, screenHeight);
        getContentPane().add(loadingScreen);
        
        setSize(screenWidth, screenHeight);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        setVisible(true);
        validate();
        revalidate();
        repaint();
        
        for (int i = 0; i < 20; i++) {
            loadingScreen.paintImmediately(loadingScreen.getBounds());
            try { Thread.sleep(50); } catch (Exception e) {}
        }

        final LoadingScreen finalLoadingScreen = loadingScreen;
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                finalLoadingScreen.setMessage("Loading heroes...");
                finalLoadingScreen.repaint();
                Thread.sleep(1500);
                finalLoadingScreen.setMessage("Loading map...");
                finalLoadingScreen.repaint();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        remove(loadingScreen);

        Arena.init(hero);
        arena = Arena.getInstance();
        panel = new ArenaPanel(arena, screenWidth, screenHeight, hero);
        panel.setPreferredSize(new Dimension(screenWidth, screenHeight));

        glassPane = new JLayeredPane();
        glassPane.setPreferredSize(new Dimension(screenWidth, screenHeight));
        glassPane.setVisible(false);

        pauseMenu = PauseMenu.getInstance();
        pauseMenu.setBounds(0, 0, screenWidth, screenHeight);
        pauseMenu.setFocusable(true);
        glassPane.add(pauseMenu, JLayeredPane.PALETTE_LAYER);

        panel.setPauseCallback(() -> {
            if (!isPaused) {
                togglePause(true);
            }
        });

        pauseMenu.setPauseMenuListener(new PauseMenu.PauseMenuListener() {
            @Override
            public void onResume() {
                togglePause(false);
            }

            @Override
            public void onExit() {
                exitToMenu();
            }
        });

        gameFrame = new JFrame("MOBA - Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(panel);
        gameFrame.setGlassPane(glassPane);
        gameFrame.setSize(screenWidth, screenHeight);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setLocationRelativeTo(null);

        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    panel.toggleShop();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause(!isPaused);
                }
            }
        });

        panel.setFocusable(true);
        panel.requestFocusInWindow();
        gameFrame.setVisible(true);
        setVisible(false);
        isGameRunning = true;
    }

    private void togglePause(boolean pause) {
        isPaused = pause;
        panel.setPaused(pause);
        if (pause) {
            pauseMenu.showMenu(screenWidth, screenHeight);
            glassPane.setVisible(true);
            glassPane.repaint();
            pauseMenu.requestFocusInWindow();
        } else {
            glassPane.setVisible(false);
        }
    }

    private void exitToMenu() {
        glassPane.setVisible(false);
        gameFrame.dispose();
        isGameRunning = false;
        isPaused = false;
        resetGameState();
        setVisible(true);
        showMainMenu();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (true) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            lastTime = currentTime;

            if (isGameRunning && arena != null && panel != null && !gameOver && !isPaused) {
                arena.update(deltaTime);
                panel.repaint();

                String result = arena.checkGameOver();
                if (result != null) {
                    gameOver = true;
                    isGameRunning = false;
                    isPaused = false;
                    String finalResult = result;
                    SwingUtilities.invokeLater(() -> {
                        showAfterGameScreen(finalResult);
                    });
                }
            }

            try {
                Thread.sleep(GameConfiguration.GAME_SPEED);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void resetGameState() {
        Arena.reset();
        Lane.reset();
        MinionSpawner.reset();
        TilesManager.reset();
        BotManager.reset();
        ShopManager.reset();
        EquipmentLoader.reset();
        JsonDataProviderFactory.reset();
        HeroSelection.reset();
        MainMenu.reset();
        GameOverScreen.reset();
        ShopPanel.reset();
        PauseMenu.reset();
        HUDRenderer.reset();
        gameOver = false;
    }

    public static void main(String[] args) {
        new InterfaceLauncher();
    }
}
