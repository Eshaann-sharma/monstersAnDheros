package monstersheroes.items;

import monstersheroes.characters.Hero;
import monstersheroes.interfaces.Consumable;

/** Represents a potion that raises one of a hero's stats when used. */
public class Potion extends Item implements Consumable {
    private final int effectAmount;
    private final StatType statType;
    private boolean consumed;

    public Potion(String name, int price, int level, int effectAmount, StatType statType) {
        super(name, price, level, 1);
        this.effectAmount = effectAmount; this.statType = statType;
    }
    public int getEffectAmount() { return effectAmount; }
    public StatType getStatType() { return statType; }
    public void applyTo(Hero hero) {
        if (consumed) throw new IllegalStateException("This potion has already been used.");
        hero.increaseStat(statType, effectAmount);
        consume();
    }
    @Override public boolean isConsumed() { return consumed; }
    @Override public void consume() { consumed = true; consumeUse(); }
    @Override public String getDescription() { return "Increases " + statType + " by " + effectAmount; }
}
