package engine.process;

import data.model.Equipment;
import engine.mobile.Player;

/**
 * Gere la logique d achat et de fusion des equipements.
 
 */
public class ShopManager {

    private Player player;

    public ShopManager(Player player) {
        this.player = player;
    }

    /**
     * Verifie si le joueur peut acheter l equipement.
     * Conditions : assez d or et slots disponibles.
     */
    public boolean canBuy(Equipment eq) {
        if (eq == null) return false;
        return player.getGold() >= eq.getPrice();
    }

    /**
     * Verifie si le joueur peut fusionner.
     * Conditions : posseder les 2 items requis.
     */
    public boolean canFuse(Equipment eq) {
        if (eq == null) return false;
        return player.hasEquipment(eq.getReq1())
            && player.hasEquipment(eq.getReq2());
    }

    /**
     * Execute l achat.
     */
    public void buy(Equipment eq) {
        if (!canBuy(eq)) return;
        player.buyEquipment(eq);
    }

    /**
     * Execute la fusion.
     */
    public void fuse(Equipment eq) {
        if (!canFuse(eq)) return;
        player.fuseEquipment(eq.getReq1(), eq.getReq2(), eq);
    }

    /**
     * Verifie si une action est possible selon le type d equipement.
     */
    public boolean canDoAction(Equipment eq) {
        if (eq == null) return false;
        if (eq.isFused()) return canFuse(eq);
        return canBuy(eq);
    }

    /**
     * Execute l action selon le type d equipement.
     */
    public void doAction(Equipment eq) {
        if (eq.isFused()) fuse(eq);
        else buy(eq);
    }
}