package monstersheroes.characters;

import monstersheroes.data.GameData;

/** Represents a heavily protected monster with strong defense. */
public class Exoskeleton extends Monster {
    public Exoskeleton(String name, int level, GameData.MonsterStats stats) { super(name, level, stats); }
<<<<<<< HEAD
    public Exoskeleton(String name, int level, GameData.MonsterStats stats, double hpMultiplier, double damageMultiplier) { super(name, level, stats, hpMultiplier, damageMultiplier); }
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
    @Override public String getMonsterType() { return "Exoskeleton"; }
}
