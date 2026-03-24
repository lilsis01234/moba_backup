package data.model;

public class Item {
    private String name;
    private int attackBonus;
    private int defenseBonus;
    private double speedBonus;
    private int hpBonus;
    private int manaBonus;

    public Item(String name, int attack, int defense, double speed, int hp, int mana) {
        this.name = name;
        this.attackBonus = attack;
        this.defenseBonus = defense;
        this.speedBonus = speed;
        this.hpBonus = hp;
        this.manaBonus = mana;
    }

    public String getName() { return name; }
    public int getAttackBonus() { return attackBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public double getSpeedBonus() { return speedBonus; }
    public int getHpBonus() { return hpBonus; }
    public int getManaBonus() { return manaBonus; }
}