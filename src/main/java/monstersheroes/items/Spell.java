package monstersheroes.items;

import monstersheroes.characters.Hero;
import monstersheroes.characters.Monster;
import monstersheroes.interfaces.Consumable;
import monstersheroes.util.CombatBalance;

/** Stores the details shared by magic attacks, including damage and mana cost. */
public abstract class Spell extends Item implements Consumable {
    private final int baseDamage;
    private final int manaCost;
    private boolean consumed;

    protected Spell(String name, int price, int level, int baseDamage, int manaCost) {
        super(name, price, level, 1);
        this.baseDamage = baseDamage; this.manaCost = manaCost;
    }
    public int getBaseDamage() { return baseDamage; }
    public int getManaCost() { return manaCost; }
    public int calculateDamage(Hero hero) {
        double assignmentDamage = baseDamage + (hero.getDexterity() / 10000.0) * baseDamage;
        return (int) Math.round(assignmentDamage * CombatBalance.SPELL_DAMAGE_MULTIPLIER);
    }
    public abstract void applyEffect(Monster monster);
    @Override public boolean isConsumed() { return consumed; }
    @Override public void consume() { consumed = true; consumeUse(); }
    @Override public String getDescription() { return "Magic damage " + baseDamage + ", mana " + manaCost; }
}
