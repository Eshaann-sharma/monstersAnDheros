package monstersheroes.world;

/** Represents a blocked map space that the party cannot enter. */
public class InaccessibleTile extends Tile {
    @Override public char getSymbol() { return '#'; }
    @Override public String getDescription() { return "Inaccessible terrain"; }
    @Override public boolean isAccessible() { return false; }
}
