package monstersheroes.items;

import monstersheroes.characters.Monster;

/** An ice spell that lowers the damage dealt by a monster. */
public class IceSpell extends Spell {
    public IceSpell(String name, int price, int level, int damage, int mana) {
        super(name, price, level, damage, mana);
    }
    @Override public void applyEffect(Monster monster) { monster.reduceAttackDamage(0.10); }
    @Override public String getDescription() { return super.getDescription() + "; reduces monster damage"; }
}
