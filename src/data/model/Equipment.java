package data.model;

public class Equipment {

    private int    id;
    private String name;
    private EquipmentType type;
    private int    attackBonus;
    private int    defenseBonus;
    private int    price;
    private String description;
    private boolean fused;
    private int    req1;
    private int    req2;

    // Constructeur item BASIQUE
    public Equipment(int id, String name, EquipmentType type,
                     int attackBonus, int defenseBonus,
                     int price, String description) {
        this.id           = id;
        this.name         = name;
        this.type         = type;
        this.attackBonus  = attackBonus;
        this.defenseBonus = defenseBonus;
        this.price        = price;
        this.description  = description;
        this.fused        = false;
        this.req1         = -1;
        this.req2         = -1;
    }

    // Constructeur item FUSIONNE
    public Equipment(int id, String name, EquipmentType type,
                     int attackBonus, int defenseBonus,
                     String description, int req1, int req2) {
        this.id           = id;
        this.name         = name;
        this.type         = type;
        this.attackBonus  = attackBonus;
        this.defenseBonus = defenseBonus;
        this.price        = 0;
        this.description  = description;
        this.fused        = true;
        this.req1         = req1;
        this.req2         = req2;
    }

    public int           getId()           { return id; }
    public String        getName()         { return name; }
    public EquipmentType getType()         { return type; }
    public int           getAttackBonus()  { return attackBonus; }
    public int           getDefenseBonus() { return defenseBonus; }
    public int           getPrice()        { return price; }
    public String        getDescription()  { return description; }
    public boolean       isFused()         { return fused; }
    public int           getReq1()         { return req1; }
    public int           getReq2()         { return req2; }
}