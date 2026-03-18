package engine.mobile;

import java.util.ArrayList;

public class Hero {

    protected String name;
    protected double maxHp;
    protected double atkDMG;
    protected double speed;

    public Hero(String name, double maxHp, double atkDMG, double speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.atkDMG = atkDMG;
        this.speed = speed;
    }
}
