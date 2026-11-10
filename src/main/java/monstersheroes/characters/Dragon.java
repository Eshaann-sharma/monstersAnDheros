package monstersheroes.characters;

import monstersheroes.data.GameData;

/** Represents a dragon, a monster that relies on high attack damage. */
public class Dragon extends Monster {
    public Dragon(String name, int level, GameData.MonsterStats stats) { super(name, level, stats); }
<<<<<<< HEAD
    public Dragon(String name, int level, GameData.MonsterStats stats, double hpMultiplier, double damageMultiplier) { super(name, level, stats, hpMultiplier, damageMultiplier); }
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
    @Override public String getMonsterType() { return "Dragon"; }
}
