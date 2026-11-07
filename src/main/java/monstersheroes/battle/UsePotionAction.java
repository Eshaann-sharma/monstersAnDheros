package monstersheroes.battle;

import monstersheroes.characters.Hero;
import monstersheroes.items.Potion;

/** Handles a hero using a potion during battle. */
public class UsePotionAction implements BattleAction {
    private final Potion potion;
    public UsePotionAction(Potion potion) { this.potion = potion; }
    @Override public void execute(Battle battle, Hero hero) { battle.performPotion(hero, potion); }
}
