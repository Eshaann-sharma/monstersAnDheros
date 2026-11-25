package monstersheroes.items;

import monstersheroes.characters.Monster;

/** A fire spell that weakens a monster's defense. */
public class FireSpell extends Spell {
    public FireSpell(String name, int price, int level, int damage, int mana) {
        super(name, price, level, damage, mana);
    }
    @Override public void applyEffect(Monster monster) { monster.reduceDefense(0.10); }
    @Override public String getDescription() { return super.getDescription() + "; reduces monster defense"; }
}
