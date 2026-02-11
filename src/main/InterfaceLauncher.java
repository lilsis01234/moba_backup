package main;

import engine.Arena;
import view.GamePanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InterfaceLauncher extends JFrame implements Runnable {
    private Arena arena;
    private GamePanel panel;

    public InterfaceLauncher() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int largeur = (int) screenSize.getWidth();
        int hauteur = (int) screenSize.getHeight();

        arena = new Arena();
        panel = new GamePanel(arena);
        panel.setPreferredSize(new Dimension(largeur, hauteur));

  
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) { // right click
                    double x = (double) e.getX() / panel.getWidth();
                    double y = (double) e.getY() / panel.getHeight();
                    arena.getPlayer().moveTo(x, y);
                }
            }
        });

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
        while (true) {
            try {
                Thread.sleep(16); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            arena.update();
            panel.repaint();
        }
    }

    public static void main(String[] args) {
        new InterfaceLauncher();
    }
}
