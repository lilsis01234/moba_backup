package engine.mobile;

public class CCEffect implements SpellStrategy {
    private int damage;
    private double stunDuration;

    public CCEffect(int damage, double stunDuration) {
        this.damage = damage;
        this.stunDuration = stunDuration;
    }

    @Override
    public void cast(Personnage caster, Entity target, int spellLevel) {
        if (target != null) {
            int scaled = (int)(damage * (1 + 0.2 * spellLevel));
            target.takeDamage(scaled);
            if (target instanceof Personnage)
                ((Personnage) target).applyStun(stunDuration);
        }
    }
}
