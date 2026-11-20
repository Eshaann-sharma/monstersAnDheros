package monstersheroes.world;

import monstersheroes.characters.Hero.Skill;

// boosts strength while a hero occupies this space
public class KoulouTile extends Tile {
    @Override public Skill getBoostedSkill() { return Skill.STRENGTH; }
    @Override public char getSymbol() { return 'K'; }
    @Override public String getDescription() { return "Koulou"; }
}
