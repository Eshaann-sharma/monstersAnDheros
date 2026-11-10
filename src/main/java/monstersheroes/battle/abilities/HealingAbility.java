package monstersheroes.battle.abilities;

import monstersheroes.battle.Battle;
import monstersheroes.characters.Hero;

/** Lets a Sorcerer restore a fixed amount of health to one living party member. */
public final class HealingAbility extends AbstractHeroAbility {
    private final int healingAmount;

    public HealingAbility(String name, String description, int manaCost, int healingAmount) {
        super(name, description, manaCost);
        if (healingAmount <= 0) throw new IllegalArgumentException("Healing amount must be positive.");
        this.healingAmount = healingAmount;
    }

    public int getHealingAmount() { return healingAmount; }

    @Override
    public void execute(Battle battle, Hero user) {
        battle.performHealing(user, this);
    }
}
