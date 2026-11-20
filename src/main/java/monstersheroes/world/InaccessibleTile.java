package monstersheroes.world;

// represents a permanent wall between lanes
public class InaccessibleTile extends Tile {
    @Override public char getSymbol() { return 'I'; }
    @Override public String getDescription() { return "Inaccessible terrain"; }
    @Override public boolean isAccessible() { return false; }
}
