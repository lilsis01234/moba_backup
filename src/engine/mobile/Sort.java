package engine.mobile;

import engine.process.Arena;

public abstract class Sort {

    protected String name;
    protected double manaCost;
    protected double cooldown;
    protected double cooldownActuel;

    public Sort(String name, double manaCost, double cooldown) {
        this.name = name;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.cooldownActuel = 0;
    }

    public boolean canCast(double currentMana, double currentTime) {
        return (currentMana >= manaCost && cooldownActuel <= 0);
    }

    public abstract void execute(Entity caster, double targetX, double targetY, Arena arena);
}
