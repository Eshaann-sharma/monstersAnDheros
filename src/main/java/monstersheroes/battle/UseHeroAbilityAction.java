package monstersheroes.battle;

import monstersheroes.characters.Hero;
import monstersheroes.interfaces.HeroAbility;

/** Runs the special ability chosen by a hero. */
public final class UseHeroAbilityAction implements BattleAction {
    private final HeroAbility ability;

    public UseHeroAbilityAction(HeroAbility ability) {
        if (ability == null) throw new IllegalArgumentException("Ability cannot be null.");
        this.ability = ability;
    }

    @Override
    public void execute(Battle battle, Hero hero) {
        ability.execute(battle, hero);
    }
}
