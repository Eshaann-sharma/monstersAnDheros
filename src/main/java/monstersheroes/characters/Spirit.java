package monstersheroes.characters;

import monstersheroes.data.GameData;

/** Represents a spirit, a monster that is especially good at avoiding attacks. */
public class Spirit extends Monster {
    public Spirit(String name, int level, GameData.MonsterStats stats) { super(name, level, stats); }
    @Override public String getMonsterType() { return "Spirit"; }
}
