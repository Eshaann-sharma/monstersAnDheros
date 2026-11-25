package monstersheroes.battle;

import monstersheroes.characters.Hero;
import monstersheroes.items.Item;

/** Handles equipping a weapon or a piece of armor during battle. */
public class EquipAction implements BattleAction {
    private final Item item;
    private final boolean bothHands;
    public EquipAction(Item item, boolean bothHands) { this.item = item; this.bothHands = bothHands; }
    @Override public void execute(Battle battle, Hero hero) { battle.performEquip(hero, item, bothHands); }
}
