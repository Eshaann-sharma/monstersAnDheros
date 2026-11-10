package monstersheroes.items;

import monstersheroes.characters.Hero;
import monstersheroes.interfaces.Equipable;

/** Represents a weapon a hero can equip to deal damage to monsters. */
public class Weapon extends Item implements Equipable {
    private final int damage;
    private final int handsRequired;

    public Weapon(String name, int price, int level, int damage, int handsRequired, int uses) {
        super(name, price, level, uses);
        if (damage < 0 || (handsRequired != 1 && handsRequired != 2))
            throw new IllegalArgumentException("Invalid weapon attributes.");
        this.damage = damage; this.handsRequired = handsRequired;
    }
    public int getDamage() { return damage; }
    public int getHandsRequired() { return handsRequired; }
    @Override public void equip(Hero hero) { hero.equipWeapon(this, handsRequired == 2); }
    public void equipWithBothHands(Hero hero) { hero.equipWeapon(this, true); }
    @Override public String getDescription() { return "Damage " + damage + ", hands " + handsRequired; }
}
