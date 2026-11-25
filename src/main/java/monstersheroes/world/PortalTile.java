package monstersheroes.world;

/** The single gateway to the boss chamber and the next depth. */
public class PortalTile extends Tile {
    @Override public char getSymbol() { return 'O'; }
    @Override public String getDescription() { return "Portal"; }
}
