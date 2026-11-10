package monstersheroes.characters;

import monstersheroes.data.GameData;
import monstersheroes.interfaces.Attackable;
import monstersheroes.util.CombatBalance;
import java.util.Random;

/** Stores the battle details shared by all monsters, including damage, defense, and dodge chance. */
public abstract class Monster extends Combatant implements Attackable {
    private double attackDamage;
    private double defense;
    private double dodgeChance;

    protected Monster(String name, int level, GameData.MonsterStats stats) {
<<<<<<< HEAD
        this(name, level, stats, 1.0, 1.0);
    }
    protected Monster(String name, int level, GameData.MonsterStats stats, double hpMultiplier, double damageMultiplier) {
        super(name, level, level * 100.0 * hpMultiplier);
        attackDamage = stats.damage * level * CombatBalance.MONSTER_DAMAGE_MULTIPLIER * damageMultiplier;
=======
        super(name, level, level * 100.0);
        attackDamage = stats.damage * level * CombatBalance.MONSTER_DAMAGE_MULTIPLIER;
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        defense = stats.defense * level;
        dodgeChance = Math.max(0, Math.min(0.75, stats.dodge * 0.01));
    }
    @Override public int getAttackDamage() { return Math.max(1, (int) Math.round(attackDamage)); }
    public double getDefense() { return defense; }
    public double getDodgeChance() { return dodgeChance; }
    public boolean dodges(Random random) { return random.nextDouble() < dodgeChance; }
    public void reduceAttackDamage(double fraction) { attackDamage *= (1.0 - fraction); }
    public void reduceDefense(double fraction) { defense *= (1.0 - fraction); }
    public void reduceDodgeChance(double fraction) { dodgeChance *= (1.0 - fraction); }
    /** Applies defense and returns the amount of damage that reached this monster. */
    public int takeCombatDamage(double rawDamage) {
        int appliedDamage = Math.max(1, (int) Math.round(rawDamage - defense));
        takeDamage(appliedDamage);
        return appliedDamage;
    }
    public abstract String getMonsterType();
    @Override public String getCombatSummary() {
        return String.format("%s the %s (Lvl %d) HP %.0f | DMG %d | DEF %.0f | Dodge %.0f%%",
                getName(), getMonsterType(), getLevel(), hp, getAttackDamage(), defense, dodgeChance * 100);
    }
}
