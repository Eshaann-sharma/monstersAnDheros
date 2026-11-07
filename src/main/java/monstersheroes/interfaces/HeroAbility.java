package monstersheroes.interfaces;

import monstersheroes.battle.Battle;
import monstersheroes.characters.Hero;

/** Describes a special move that a hero can use during battle. */
public interface HeroAbility {
    String getName();
    String getDescription();
    int getManaCost();
    void execute(Battle battle, Hero user);
}
