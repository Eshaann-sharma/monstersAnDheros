package monstersheroes.legendsofvalor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/** A randomly-terraformed 9 by 8 board with fixed Nexus, market, and lane walls. */
public final class ValorBoard {
    public static final int ROWS = 9;
    public static final int COLUMNS = 8;
    /** Kept as a row-count alias for source compatibility with existing callers. */
    @Deprecated public static final int SIZE = ROWS;
    public static final int MARKET_ROW = ROWS / 2;
    private final Terrain[][] terrain = new Terrain[ROWS][COLUMNS];

    public ValorBoard(Random random) {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                terrain[row][column] = (column == 2 || column == 5)
                        ? Terrain.INACCESSIBLE : Terrain.PLAIN;
            }
        }
        for (Lane lane : Lane.values()) {
            terrain[0][lane.firstColumn()] = Terrain.NEXUS;
            terrain[0][lane.secondColumn()] = Terrain.NEXUS;
            terrain[ROWS - 1][lane.firstColumn()] = Terrain.NEXUS;
            terrain[ROWS - 1][lane.secondColumn()] = Terrain.NEXUS;
            terrain[MARKET_ROW][lane.firstColumn()] = Terrain.MARKET;
            terrain[MARKET_ROW][lane.secondColumn()] = Terrain.MARKET;
        }

        List<ValorPosition> interior = new ArrayList<>();
        for (int row = 1; row < ROWS - 1; row++) {
            if (row == MARKET_ROW) continue;
            for (Lane lane : Lane.values()) {
                interior.add(new ValorPosition(row, lane.firstColumn()));
                interior.add(new ValorPosition(row, lane.secondColumn()));
            }
        }

        // Keep first moves open and use a few obstacles deeper in the lanes.
        List<ValorPosition> obstacleCandidates = interior.stream()
                .filter(position -> position.row() >= 2 && position.row() <= ROWS - 3)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        Collections.shuffle(obstacleCandidates, random);
        for (int i = 0; i < 3; i++) set(obstacleCandidates.get(i), Terrain.OBSTACLE);
        interior.removeAll(obstacleCandidates.subList(0, 3));
        Collections.shuffle(interior, random);

        int index = 0;
        for (int i = 0; i < 6; i++) set(interior.get(index++), Terrain.BUSH);
        for (int i = 0; i < 6; i++) set(interior.get(index++), Terrain.CAVE);
        for (int i = 0; i < 6; i++) set(interior.get(index++), Terrain.KOULOU);
        while (index < interior.size()) set(interior.get(index++), Terrain.PLAIN);
    }

    public Terrain terrainAt(ValorPosition position) {
        if (!isOnBoard(position)) throw new IllegalArgumentException("Position is outside the board.");
        return terrain[position.row()][position.column()];
    }

    public boolean isOnBoard(ValorPosition position) {
        return position != null && position.row() >= 0 && position.row() < ROWS
                && position.column() >= 0 && position.column() < COLUMNS;
    }

    public boolean isTraversable(ValorPosition position) {
        if (!isOnBoard(position)) return false;
        Terrain type = terrain[position.row()][position.column()];
        return type != Terrain.INACCESSIBLE && type != Terrain.OBSTACLE;
    }

    public boolean removeObstacle(ValorPosition position) {
        if (!isOnBoard(position) || terrain[position.row()][position.column()] != Terrain.OBSTACLE) return false;
        terrain[position.row()][position.column()] = Terrain.PLAIN;
        return true;
    }

    public boolean isNexus(ValorPosition position) {
        return isOnBoard(position) && terrain[position.row()][position.column()] == Terrain.NEXUS;
    }

    public boolean isMarket(ValorPosition position) {
        return isOnBoard(position) && terrain[position.row()][position.column()] == Terrain.MARKET;
    }

    private void set(ValorPosition position, Terrain value) {
        terrain[position.row()][position.column()] = value;
    }
}
