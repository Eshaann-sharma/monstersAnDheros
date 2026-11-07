package monstersheroes.items;

import monstersheroes.characters.Hero;
import monstersheroes.interfaces.Equipable;

/** Represents armor that a hero can wear to reduce damage from monster attacks. */
public class Armor extends Item implements Equipable {
    private final int damageReduction;
    public Armor(String name, int price, int level, int damageReduction, int uses) {
        super(name, price, level, uses);
        this.damageReduction = Math.max(0, damageReduction);
    }
    public int getDamageReduction() { return damageReduction; }
    @Override public void equip(Hero hero) { hero.equipArmor(this); }
    @Override public String getDescription() { return "Damage reduction " + damageReduction; }
}
