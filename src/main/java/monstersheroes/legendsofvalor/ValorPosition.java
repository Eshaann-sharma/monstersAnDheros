package monstersheroes.legendsofvalor;

/** A row and column on the 9 by 8 Valor board. */
public record ValorPosition(int row, int column) {
    public int distanceTo(ValorPosition other) {
        return Math.abs(row - other.row) + Math.abs(column - other.column);
    }

    public boolean isAdjacentTo(ValorPosition other) {
        return distanceTo(other) == 1;
    }

    public Lane lane() { return Lane.atColumn(column); }
}
