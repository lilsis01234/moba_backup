package main;

import engine.Arena;
import view.GamePanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Toolkit;

public class InterfaceLauncher {

    public static void main(String[] args) {
       Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int largeur = (int) screenSize.getWidth();
        int hauteur = (int) screenSize.getHeight();

        Arena arena = new Arena();

        GamePanel panel = new GamePanel(arena);
        panel.setPreferredSize(new Dimension(largeur, hauteur));

        JFrame fenetre = new JFrame("MOBA");
        fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fenetre.setResizable(true);

        fenetre.add(panel);

        fenetre.pack();
       
        fenetre.setExtendedState(JFrame.MAXIMIZED_BOTH);
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
    }
}