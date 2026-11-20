package monstersheroes.world;

// blocks movement until the obstacle is cleared
public class ObstacleTile extends Tile {
    @Override public char getSymbol() { return 'O'; }
    @Override public String getDescription() { return "Obstacle"; }
    @Override public boolean isAccessible() { return false; }
}
