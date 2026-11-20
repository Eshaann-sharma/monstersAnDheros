package monstersheroes.world;

import monstersheroes.characters.Hero.Skill;

// defines the properties shared by all board spaces
public abstract class Tile {
    public abstract char getSymbol();
    public abstract String getDescription();
    // identifies the hero stat temporarily boosted by this terrain
    public Skill getBoostedSkill() { return null; }
    public boolean isAccessible() { return true; }
}
