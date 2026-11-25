package monstersheroes.world;

/** Describes a space on the map and the basic details every type of space must provide. */
public abstract class Tile {
    public abstract char getSymbol();
    public abstract String getDescription();
    public boolean isAccessible() { return true; }
}
