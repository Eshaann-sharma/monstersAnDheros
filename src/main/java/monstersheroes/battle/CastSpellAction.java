package monstersheroes.battle;

import monstersheroes.characters.Hero;
import monstersheroes.characters.Monster;
import monstersheroes.items.Spell;

/** Handles a hero casting a spell at a selected monster. */
public class CastSpellAction implements BattleAction {
    private final Spell spell;
    private final Monster target;
    public CastSpellAction(Spell spell, Monster target) { this.spell = spell; this.target = target; }
    @Override public void execute(Battle battle, Hero hero) { battle.performSpell(hero, target, spell); }
}
