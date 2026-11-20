package monstersheroes.world;

import monstersheroes.characters.Hero.Skill;

// boosts dexterity while a hero occupies this space
public class BushTile extends Tile {
    @Override public Skill getBoostedSkill() { return Skill.DEXTERITY; }
    @Override public char getSymbol() { return 'B'; }
    @Override public String getDescription() { return "Bush"; }
}
