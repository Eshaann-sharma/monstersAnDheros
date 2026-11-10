package monstersheroes.util;

/** Keeps the damage and critical-hit settings used by the battle system in one place. */
public final class CombatBalance {
    /** Hero weapon damage is four times the original assignment example multiplier. */
    public static final double HERO_WEAPON_DAMAGE_MULTIPLIER = 0.20;
    /** Spell base damage is increased to make limited-use spells worthwhile. */
    public static final double SPELL_DAMAGE_MULTIPLIER = 1.75;
    /** Monster damage is increased to keep faster battles dangerous. */
    public static final double MONSTER_DAMAGE_MULTIPLIER = 2.0;
    /** Chance for any successful attack to become a critical hit. */
    public static final double CRITICAL_HIT_CHANCE = 0.15;
    /** Critical hits deal twice the normal pre-defense damage. */
    public static final double CRITICAL_HIT_MULTIPLIER = 2.0;

    private CombatBalance() {
        // Utility class.
    }
}
