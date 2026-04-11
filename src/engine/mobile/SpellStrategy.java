package engine.mobile;

public interface SpellStrategy {
    void cast(Personnage caster, Entity target, int spellLevel);
}
