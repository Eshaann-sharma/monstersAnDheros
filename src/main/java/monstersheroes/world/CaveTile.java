package monstersheroes.world;

import monstersheroes.characters.Hero.Skill;

// boosts agility while a hero occupies this space
public class CaveTile extends Tile {
    @Override public Skill getBoostedSkill() { return Skill.AGILITY; }
    @Override public char getSymbol() { return 'C'; }
    @Override public String getDescription() { return "Cave"; }
}
