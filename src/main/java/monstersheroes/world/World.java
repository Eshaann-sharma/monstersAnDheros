package monstersheroes.world;

import monstersheroes.data.GameData;
import monstersheroes.market.Market;
import java.util.*;

/** Builds and displays the map and keeps track of where the party is standing. */
public class World {
    private final int size;
<<<<<<< HEAD
    private final int level;
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
    private final Tile[][] grid;
    private int partyX, partyY;

    public World(int size, GameData data, Random random) {
<<<<<<< HEAD
        this(size, data, random, 0);
    }

    public World(int size, GameData data, Random random, int level) {
        this.size = size;
        this.level = level;
=======
        this.size = size;
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        grid = new Tile[size][size];
        generate(data, random);
        partyX = size / 2; partyY = size / 2;
    }

    private void generate(GameData data, Random random) {
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < size; r++) for (int c = 0; c < size; c++) {
<<<<<<< HEAD
            int portalX = size / 2 + 1 < size ? size / 2 + 1 : size / 2 - 1;
            if (!(r == size / 2 && c == size / 2) && !(r == size / 2 && c == portalX))
                positions.add(new int[]{r, c});
=======
            if (!(r == size / 2 && c == size / 2)) positions.add(new int[]{r, c});
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        }
        Collections.shuffle(positions, random);
        int total = size * size;
        int inaccessibleCount = (int) Math.round(total * 0.20);
        int marketCount = (int) Math.round(total * 0.30);
        grid[size / 2][size / 2] = new CommonTile();
<<<<<<< HEAD
        // Put the portal beside the starting tile so every level is immediately reachable.
        int portalX = size / 2 + 1 < size ? size / 2 + 1 : size / 2 - 1;
        grid[size / 2][portalX] = new PortalTile();
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        java.util.Set<String> marketStockSignatures = new java.util.HashSet<>();
        int index = 0;
        for (; index < inaccessibleCount; index++) {
            int[] p = positions.get(index); grid[p[0]][p[1]] = new InaccessibleTile();
        }
        for (int i = 0; i < marketCount; i++, index++) {
            int[] p = positions.get(index); grid[p[0]][p[1]] = new MarketTile(new Market(data, random, marketStockSignatures));
        }
        while (index < positions.size()) {
            int[] p = positions.get(index++); grid[p[0]][p[1]] = new CommonTile();
        }
    }

    public Tile getCurrentTile() { return grid[partyY][partyX]; }
<<<<<<< HEAD
    public int getLevel() { return level; }
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
    public int getPartyX() { return partyX; }
    public int getPartyY() { return partyY; }

    public boolean move(int dx, int dy) {
        int nextX = partyX + dx, nextY = partyY + dy;
        if (nextX < 0 || nextY < 0 || nextX >= size || nextY >= size) {
            System.out.println("You cannot move beyond the edge of the map."); return false;
        }
        if (!grid[nextY][nextX].isAccessible()) {
            System.out.println("That space is inaccessible. Choose another direction."); return false;
        }
        partyX = nextX; partyY = nextY;
        System.out.println("Moved to: " + getCurrentTile().getDescription());
        return true;
    }

    /** Prints a compact map without the oversized per-cell box borders. */
    public void printMap() {
        String border = "   +" + "-".repeat(size * 4) + "+";
        System.out.println("\n==================== WORLD MAP ====================");
        System.out.print("    ");
        for (int c = 0; c < size; c++) System.out.printf(" %-2d ", c + 1);
        System.out.println();
        System.out.println(border);

        for (int r = 0; r < size; r++) {
            System.out.printf("%2d |", r + 1);
            for (int c = 0; c < size; c++) {
                char symbol = (r == partyY && c == partyX)
                        ? '@' : displaySymbol(grid[r][c]);
                System.out.printf(" %-2s ", symbol);
            }
            System.out.println("|");
        }

        System.out.println(border);
<<<<<<< HEAD
        System.out.printf(" Depth: %d%n", level);
        System.out.println(" @ = Your party   . = Open area   $ = Market   # = Blocked   O = Portal");
=======
        System.out.println(" @ = Your party   . = Open area   $ = Market   # = Blocked");
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        System.out.printf(" Party position: row %d, column %d (%s)%n",
                partyY + 1, partyX + 1, getCurrentTile().getDescription());
    }

    private char displaySymbol(Tile tile) {
        if (tile instanceof MarketTile) return '$';
        if (tile instanceof InaccessibleTile) return '#';
<<<<<<< HEAD
        if (tile instanceof PortalTile) return 'O';
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        return '.';
    }
}
