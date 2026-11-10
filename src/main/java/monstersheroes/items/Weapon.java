package monstersheroes.items;

import monstersheroes.characters.Hero;
import monstersheroes.interfaces.Equipable;
<<<<<<< HEAD
import java.util.Set;
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2

/** Represents a weapon a hero can equip to deal damage to monsters. */
public class Weapon extends Item implements Equipable {
    private final int damage;
    private final int handsRequired;
<<<<<<< HEAD
    private final Set<String> allowedClasses;

    public Weapon(String name, int price, int level, int damage, int handsRequired, int uses) {
        this(name, price, level, damage, handsRequired, uses, Set.of());
    }
    public Weapon(String name, int price, int level, int damage, int handsRequired, int uses, Set<String> allowedClasses) {
=======

    public Weapon(String name, int price, int level, int damage, int handsRequired, int uses) {
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        super(name, price, level, uses);
        if (damage < 0 || (handsRequired != 1 && handsRequired != 2))
            throw new IllegalArgumentException("Invalid weapon attributes.");
        this.damage = damage; this.handsRequired = handsRequired;
<<<<<<< HEAD
        this.allowedClasses = Set.copyOf(allowedClasses);
    }
    public int getDamage() { return damage; }
    public int getHandsRequired() { return handsRequired; }
    public boolean canBeUsedBy(Hero hero) {
        return allowedClasses.isEmpty() || allowedClasses.stream().anyMatch(name -> name.equalsIgnoreCase(hero.getHeroClass()));
    }
    public String getAllowedClassesDescription() { return allowedClasses.isEmpty() ? "All classes" : String.join(", ", allowedClasses); }
    @Override public void equip(Hero hero) { hero.equipWeapon(this, handsRequired == 2); }
    public void equipWithBothHands(Hero hero) { hero.equipWeapon(this, true); }
    @Override public String getDescription() {
        return "Damage " + damage + ", hands " + handsRequired
                + (allowedClasses.isEmpty() ? "" : ", restricted to " + getAllowedClassesDescription());
    }
=======
    }
    public int getDamage() { return damage; }
    public int getHandsRequired() { return handsRequired; }
    @Override public void equip(Hero hero) { hero.equipWeapon(this, handsRequired == 2); }
    public void equipWithBothHands(Hero hero) { hero.equipWeapon(this, true); }
    @Override public String getDescription() { return "Damage " + damage + ", hands " + handsRequired; }
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
}
