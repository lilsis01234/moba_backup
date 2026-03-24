package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import data.model.Hero;
import data.model.Item;

public class EquipmentMenu extends JFrame {

    private Hero hero;
    private JButton[] slots;

    public EquipmentMenu(Hero hero) {
        this.hero = hero;
        setTitle("Equipment Menu");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 6)); // 6 slots

        slots = new JButton[6];
        for (int i = 0; i < 6; i++) {
            slots[i] = new JButton("Empty");
            final int index = i; // nécessaire pour listener
            slots[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // clic sur le slot
                    if (index < hero.getItems().size()) {
                        Item item = hero.getItems().get(index);
                        JOptionPane.showMessageDialog(null, "Slot " + (index + 1) 
                            + ": " + item.getName());
                    } else {
                        JOptionPane.showMessageDialog(null, "Slot " + (index + 1) + " is empty");
                    }
                }
            });
            add(slots[i]);
        }

        updateInventory();
        setVisible(true);
    }

    public void updateInventory() {
        for (int i = 0; i < 6; i++) {
            if (i < hero.getItems().size()) {
                slots[i].setText(hero.getItems().get(i).getName());
            } else {
                slots[i].setText("Empty");
            }
        }
    }
}