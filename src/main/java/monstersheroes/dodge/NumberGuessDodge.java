package monstersheroes.dodge;

import monstersheroes.interfaces.DodgeRule;

/** Checks whether the player guessed the same number as the monster to avoid an attack. */
public class NumberGuessDodge implements DodgeRule {
    @Override
    public boolean dodge(int playerGuess, int monsterNumber) {
        return playerGuess == monsterNumber;
    }
}
