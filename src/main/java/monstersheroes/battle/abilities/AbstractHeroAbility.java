package monstersheroes.battle.abilities;

import monstersheroes.interfaces.HeroAbility;

/** Holds the shared name and mana cost used by the heroes' special abilities. */
public abstract class AbstractHeroAbility implements HeroAbility {
    private final String name;
    private final String description;
    private final int manaCost;

    protected AbstractHeroAbility(String name, String description, int manaCost) {
        if (name == null || name.isBlank() || description == null || manaCost < 0) {
            throw new IllegalArgumentException("Invalid hero ability configuration.");
        }
        this.name = name;
        this.description = description;
        this.manaCost = manaCost;
    }

    @Override public final String getName() { return name; }
    @Override public final String getDescription() { return description; }
    @Override public final int getManaCost() { return manaCost; }

    @Override public String toString() {
        return name + " (" + manaCost + " MP) — " + description;
    }
}
