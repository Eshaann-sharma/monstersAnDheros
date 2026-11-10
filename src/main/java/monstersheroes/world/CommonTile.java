package monstersheroes.world;

/** Represents an ordinary map space where a battle may begin. */
public class CommonTile extends Tile {
    @Override public char getSymbol() { return '.'; }
    @Override public String getDescription() { return "Common area"; }
}
