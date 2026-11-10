package monstersheroes.items;

import monstersheroes.characters.Monster;

/** A lightning spell that makes a monster less likely to dodge an attack. */
public class LightningSpell extends Spell {
    public LightningSpell(String name, int price, int level, int damage, int mana) {
        super(name, price, level, damage, mana);
    }
    @Override public void applyEffect(Monster monster) { monster.reduceDodgeChance(0.10); }
    @Override public String getDescription() { return super.getDescription() + "; reduces monster dodge"; }
}
