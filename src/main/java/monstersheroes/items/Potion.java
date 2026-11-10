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
<<<<<<< HEAD
        if (consumed) {
            throw new IllegalStateException(
                    "This potion has already been used."
            );
        }

        int amount = getActualEffectAmount(hero);

        hero.increaseStat(statType, amount);

        consume();
    }
    public int getActualEffectAmount(Hero hero) {

        int requestedAmount = switch (statType) {
            case HP -> (int) Math.ceil(
                    hero.getMaxHP() * effectAmount / 100.0
            );

            case MP -> (int) Math.ceil(
                    hero.getMaxMana() * effectAmount / 100.0
            );

            default -> effectAmount;
        };

        return switch (statType) {
            case HP -> (int) Math.min(
                    requestedAmount,
                    Math.max(0, hero.getMaxHP() - hero.getHP())
            );

            case MP -> (int) Math.min(
                    requestedAmount,
                    Math.max(0, hero.getMaxMana() - hero.getMP())
            );

            default -> requestedAmount;
        };
    }

=======
        if (consumed) throw new IllegalStateException("This potion has already been used.");
        hero.increaseStat(statType, effectAmount);
        consume();
    }
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
    @Override public boolean isConsumed() { return consumed; }
    @Override public void consume() { consumed = true; consumeUse(); }
    @Override public String getDescription() { return "Increases " + statType + " by " + effectAmount; }
}
