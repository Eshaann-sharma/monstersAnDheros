package monstersheroes.battle.abilities;

import monstersheroes.battle.Battle;
import monstersheroes.characters.Hero;

/** Carries out a class-specific attack, including any extra effect it may have. */
public final class SpecialAttackAbility extends AbstractHeroAbility {
    /** Lists the kinds of damage a special attack can deal. */
    public enum DamageStyle { PHYSICAL, ARCANE, HOLY }
    /** Lists the effects a special attack can apply to a monster. */
    public enum Debuff { NONE, REDUCE_DEFENSE, REDUCE_MONSTER_ATTACK, REDUCE_MONSTER_DODGE }

    private final double damageMultiplier;
    private final DamageStyle damageStyle;
    private final boolean areaAttack;
    private final Debuff debuff;
    private final double debuffFraction;

    public SpecialAttackAbility(String name, String description, int manaCost,
                                 double damageMultiplier, DamageStyle damageStyle,
                                 boolean areaAttack, Debuff debuff, double debuffFraction) {
        super(name, description, manaCost);
        if (damageMultiplier <= 0 || damageStyle == null || debuff == null
                || debuffFraction < 0 || debuffFraction > 1) {
            throw new IllegalArgumentException("Invalid special attack configuration.");
        }
        this.damageMultiplier = damageMultiplier;
        this.damageStyle = damageStyle;
        this.areaAttack = areaAttack;
        this.debuff = debuff;
        this.debuffFraction = debuffFraction;
    }

    public double getDamageMultiplier() { return damageMultiplier; }
    public DamageStyle getDamageStyle() { return damageStyle; }
    public boolean isAreaAttack() { return areaAttack; }
    public Debuff getDebuff() { return debuff; }
    public double getDebuffFraction() { return debuffFraction; }

    @Override
    public void execute(Battle battle, Hero user) {
        battle.performSpecialAttack(user, this);
    }
}
