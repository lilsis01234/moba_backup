package main; 
import engine.process.Arena;
import gui.game.ArenaPanel;
import gui.menu.MainMenu;
import config.GameConfiguration;

import javax.swing.JFrame;
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

    private void startGame() {
        setVisible(false);
        
        arena = new Arena();
        panel = new ArenaPanel(arena, screenWidth, screenHeight);
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

            if (isGameRunning && arena != null && panel != null) {
                arena.update(deltaTime);
                panel.repaint();
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
