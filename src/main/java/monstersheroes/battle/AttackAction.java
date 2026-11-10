package monstersheroes.battle;

import monstersheroes.characters.Hero;
import monstersheroes.characters.Monster;

/** Carries out a hero's regular weapon attack against the chosen monster. */
public class AttackAction implements BattleAction {
    private final Monster target;
    public AttackAction(Monster target) { this.target = target; }
    @Override public void execute(Battle battle, Hero hero) { battle.performAttack(hero, target); }
}
