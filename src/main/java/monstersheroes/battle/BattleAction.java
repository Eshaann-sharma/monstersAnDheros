package monstersheroes.battle;

import monstersheroes.characters.Hero;

/** Describes an action a hero can take during their turn in battle. */
public interface BattleAction {
    void execute(Battle battle, Hero hero);
}
