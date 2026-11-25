package monstersheroes.interfaces;

/** Defines how the game decides whether a hero avoids an incoming attack. */
public interface DodgeRule {
    boolean dodge(int playerGuess, int monsterNumber);
}
