package monstersheroes.characters;

import monstersheroes.data.GameData;

/** Represents a dragon, a monster that relies on high attack damage. */
public class Dragon extends Monster {
    public Dragon(String name, int level, GameData.MonsterStats stats) { super(name, level, stats); }
    @Override public String getMonsterType() { return "Dragon"; }
}
