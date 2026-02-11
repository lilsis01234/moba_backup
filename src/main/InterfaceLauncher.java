package main;

import engine.Arena;
import view.ArenaPanel; 
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InterfaceLauncher extends JFrame implements Runnable {
    private Arena arena;
    private ArenaPanel panel; 

    public InterfaceLauncher() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int largeur = (int) screenSize.getWidth();
        int hauteur = (int) screenSize.getHeight();

        arena = new Arena();
        panel = new ArenaPanel(arena, largeur, hauteur); 
        panel.setPreferredSize(new Dimension(largeur, hauteur));

        setTitle("MOBA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        add(panel);
        pack();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setVisible(true);

        new Thread(this).start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();

        while (true) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / 1_000_000_000.0;
            lastTime = currentTime;

            arena.update(deltaTime);
            panel.repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new InterfaceLauncher();
    }
}