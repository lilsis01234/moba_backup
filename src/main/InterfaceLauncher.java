package main; 
import engine.process.Arena;

import gui.game.ArenaPanel;
import gui.menu.GameOverScreen;
import gui.menu.HeroSelection;
import gui.menu.MainMenu;
import game_config.GameConfiguration;
import data.model.Hero;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.awt.*;
import java.awt.event.*;

/**
 * @author RAHARIMANANA Tianantenaina BOUKIRAT Thafat ZEGHBIB Sonia
 */

public class InterfaceLauncher extends JFrame implements Runnable {
    private static final long serialVersionUID = 1L;
    private MainMenu mainMenu;
    private int screenWidth;
    private int screenHeight;
    private boolean isGameRunning = false;
    private JFrame gameFrame;
    private ArenaPanel panel;
    private Arena arena;
    private boolean gameOver = false;

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
        
        mainMenu = new MainMenu(new Dimension(screenWidth, screenHeight));
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
    private void showGameOver(String result) {
    	
        JFrame gameOverFrame = new JFrame("Game Over");
        gameOverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameOverFrame.setSize(screenWidth, screenHeight);
        gameOverFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        GameOverScreen screen = new GameOverScreen(result, new Dimension(screenWidth, screenHeight), new GameOverScreen.GameOverListener() {
            @Override
            public void onReturnToMenu() {
                gameOverFrame.dispose();
                gameOver = false;
                setVisible(true);
                showMainMenu();
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

        HeroSelection heroSelection = new HeroSelection(new Dimension(screenWidth, screenHeight));
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
        arena = new Arena(hero);
        panel = new ArenaPanel(arena, screenWidth, screenHeight, hero);
        panel.setPreferredSize(new Dimension(screenWidth, screenHeight));

        gameFrame = new JFrame("MOBA - Game");
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.add(panel);
        gameFrame.setSize(screenWidth, screenHeight);
        gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        gameFrame.setLocationRelativeTo(null);

        gameFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameFrame.dispose();
                    setVisible(true);
                    showMainMenu();
                }
            }
        });

        panel.setFocusable(true);
        gameFrame.setVisible(true);
        isGameRunning = true;
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (true) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            lastTime = currentTime;

            if (isGameRunning && arena != null && panel != null && !gameOver) {
                arena.update(deltaTime);
                panel.repaint();

                String result = arena.checkGameOver();
                if (result != null) {
                    gameOver = true;
                    isGameRunning = false;
                    final String finalResult = result;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            gameFrame.dispose();
                            showGameOver(finalResult);
                        }
                    });
                }
            }

            try {
                Thread.sleep(GameConfiguration.GAME_SPEED);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new InterfaceLauncher();
    }
}
