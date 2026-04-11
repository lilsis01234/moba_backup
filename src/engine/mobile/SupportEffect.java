package engine.mobile;

public class SupportEffect implements SpellStrategy {
    private int healAmount;

    public SupportEffect(int healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public void cast(Personnage caster, Entity target, int spellLevel) {
        int scaled = (int)(healAmount * (1 + 0.2 * spellLevel));
        caster.heal(scaled);
    }
}
