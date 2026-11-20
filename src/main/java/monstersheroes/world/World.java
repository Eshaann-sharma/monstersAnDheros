package monstersheroes.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// stores the board terrain and the positions of both teams
public class World {
    private static final int SIZE = 8;
    public static final int LANE_COUNT = 3;
    private final Tile[][] grid = new Tile[SIZE][SIZE];
    // store each coordinate as a row followed by a column
    private final List<int[]> heroes = new ArrayList<>();
    private final List<int[]> monsters = new ArrayList<>();

    // accepts zero-based lane numbers in hero order
    public World(Random random, List<Integer> heroLanes) {
        generate(random);
        // start heroes on the left nexus space in each chosen lane
        for (int lane : heroLanes) {
            heroes.add(new int[]{SIZE - 1, lane * 3});
        }
        // show one monster on the right nexus space in each lane
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            monsters.add(new int[]{0, lane * 3 + 1});
        }
    }

    private void generate(Random random) {
        // reserve the fixed wall and nexus spaces before assigning terrain
        List<int[]> openPositions = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int column = 0; column < SIZE; column++) {
                if (column % 3 == 2) {
                    grid[row][column] = new InaccessibleTile();
                } else if (row == 0 || row == SIZE - 1) {
                    grid[row][column] = new NexusTile(row == SIZE - 1);
                } else {
                    openPositions.add(new int[]{row, column});
                }
            }
        }

        // place one obstacle per lane to leave a path between its bases
        for (int lane = 0; lane < LANE_COUNT; lane++) {
            int row = 1 + random.nextInt(SIZE - 2);
            int column = lane * 3 + random.nextInt(2);
            grid[row][column] = new ObstacleTile();
            openPositions.remove(indexAt(openPositions, row, column));
        }
        // place seven of each special terrain type and fill the rest with plain spaces
        Collections.shuffle(openPositions, random);
        for (int i = 0; i < openPositions.size(); i++) {
            int[] position = openPositions.get(i);
            grid[position[0]][position[1]] = switch (i / 7) {
                case 0 -> new BushTile();
                case 1 -> new CaveTile();
                case 2 -> new KoulouTile();
                default -> new CommonTile();
            };
        }
    }

    // returns the terrain under the given hero
    public Tile getHeroTile(int heroIndex) {
        int[] position = heroes.get(heroIndex);
        return grid[position[0]][position[1]];
    }

    // moves one hero a single step when the destination is valid
    public boolean moveHero(int heroIndex, int dx, int dy) {
        if (Math.abs(dx) + Math.abs(dy) != 1) {
            return false;
        }
        int[] current = heroes.get(heroIndex);
        int row = current[0] + dy;
        int column = current[1] + dx;
        if (!isOnBoard(row, column)) {
            System.out.println("You cannot move beyond the edge of the board.");
            return false;
        }
        Tile tile = grid[row][column];
        if (!tile.isAccessible()) {
            System.out.println("You cannot enter this space: " + tile.getDescription() + ".");
            return false;
        }
        // heroes can share a space with a monster but not with another hero
        if (indexAt(heroes, row, column) >= 0) {
            System.out.println("Another hero already occupies this space.");
            return false;
        }
        heroes.set(heroIndex, new int[]{row, column});
        return true;
    }

    // replaces one adjacent obstacle with plain terrain without moving the hero
    public boolean clearObstacle(int heroIndex, int dx, int dy) {
        if (Math.abs(dx) + Math.abs(dy) != 1) return false;
        int[] current = heroes.get(heroIndex);
        int row = current[0] + dy;
        int column = current[1] + dx;
        if (!isOnBoard(row, column) || !(grid[row][column] instanceof ObstacleTile)) {
            return false;
        }
        grid[row][column] = new CommonTile();
        return true;
    }

    private boolean isOnBoard(int row, int column) {
        return row >= 0 && row < SIZE && column >= 0 && column < SIZE;
    }

    // compare the row and column values to locate a coordinate in the list
    private int indexAt(List<int[]> positions, int row, int column) {
        for (int i = 0; i < positions.size(); i++) {
            int[] position = positions.get(i);
            if (position[0] == row && position[1] == column) {
                return i;
            }
        }
        return -1;
    }

    // draws terrain on each cell border and character labels inside
    public void printMap() {
        System.out.println("\n                         LEGENDS OF VALOR");
        for (int row = 0; row < SIZE; row++) {
            StringBuilder border = new StringBuilder();
            StringBuilder contents = new StringBuilder();
            for (int column = 0; column < SIZE; column++) {
                Tile tile = grid[row][column];
                char symbol = tile.getSymbol();
                border.append(String.format("%c - %c - %c ", symbol, symbol, symbol));
                contents.append(String.format("| %-5s | ", contentsAt(row, column, tile)));
            }
            System.out.println(border);
            System.out.println(contents);
            System.out.println(border);
            System.out.println();
        }
        System.out.println("N Nexus   I Wall   O Obstacle   P Plain   B Bush   C Cave   K Koulou");
    }

    // combines both team labels when they occupy the same cell
    private String contentsAt(int row, int column, Tile tile) {
        if (tile instanceof InaccessibleTile) return "X X X";
        int hero = indexAt(heroes, row, column);
        int monster = indexAt(monsters, row, column);
        String heroLabel = hero < 0 ? "" : "H" + (hero + 1);
        String monsterLabel = monster < 0 ? "" : "M" + (monster + 1);
        return (heroLabel + " " + monsterLabel).strip();
    }
}
