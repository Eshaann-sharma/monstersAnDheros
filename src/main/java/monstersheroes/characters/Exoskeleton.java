package monstersheroes.characters;

import monstersheroes.data.GameData;

/** Represents a heavily protected monster with strong defense. */
public class Exoskeleton extends Monster {
    public Exoskeleton(String name, int level, GameData.MonsterStats stats) { super(name, level, stats); }
    public Exoskeleton(String name, int level, GameData.MonsterStats stats, double hpMultiplier, double damageMultiplier) { super(name, level, stats, hpMultiplier, damageMultiplier); }
    @Override public String getMonsterType() { return "Exoskeleton"; }
}
