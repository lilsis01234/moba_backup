package engine.mobile;

public class DamageEffect implements SpellStrategy {
    private int damage;

    public DamageEffect(int damage) {
        this.damage = damage;
    }

    @Override
    public void cast(Personnage caster, Entity target, int spellLevel) {
        if (target != null) {
            int scaled = (int)(damage * (1 + 0.2 * spellLevel));
            target.takeDamage(scaled);
            if (target instanceof Personnage) {
                caster.addDamageToHeroes(scaled);
            } else if (target instanceof Tower || target instanceof Base) {
                caster.addDamageToBuildings(scaled);
            }
        }
    }
}
