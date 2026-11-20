package monstersheroes.world;

// represents plain terrain with no special effect
public class CommonTile extends Tile {
    @Override public char getSymbol() { return 'P'; }
    @Override public String getDescription() { return "Plain"; }
}
