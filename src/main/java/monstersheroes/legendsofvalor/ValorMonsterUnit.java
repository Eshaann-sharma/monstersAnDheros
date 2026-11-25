package monstersheroes.legendsofvalor;

import monstersheroes.characters.Monster;

/** A reusable Monster placed on the Valor board. */
public final class ValorMonsterUnit {
    private final int number;
    private final Monster monster;
    private ValorPosition position;

    ValorMonsterUnit(int number, Monster monster, ValorPosition position) {
        this.number = number;
        this.monster = monster;
        this.position = position;
    }

    public int number() { return number; }
    public Monster monster() { return monster; }
    public ValorPosition position() { return position; }
    void moveTo(ValorPosition destination) { position = destination; }
}
