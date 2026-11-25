package monstersheroes.legendsofvalor;

/** The three two-column lanes on the Legends of Valor board. */
public enum Lane {
    TOP(0), MIDDLE(3), BOTTOM(6);

    private final int firstColumn;

    Lane(int firstColumn) { this.firstColumn = firstColumn; }

    public int firstColumn() { return firstColumn; }
    public int secondColumn() { return firstColumn + 1; }

    public static Lane atColumn(int column) {
        if (column == 0 || column == 1) return TOP;
        if (column == 3 || column == 4) return MIDDLE;
        if (column == 6 || column == 7) return BOTTOM;
        throw new IllegalArgumentException("Column " + column + " is an impassable divider.");
    }
}
