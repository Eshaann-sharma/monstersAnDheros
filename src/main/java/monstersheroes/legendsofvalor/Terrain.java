package monstersheroes.legendsofvalor;

/** Terrain categories and their board-map codes. */
public enum Terrain {
    NEXUS('N'), MARKET('$'), INACCESSIBLE('I'), OBSTACLE('O'), PLAIN('P'), BUSH('B'), CAVE('C'), KOULOU('K');

    private final char symbol;
    Terrain(char symbol) { this.symbol = symbol; }
    public char symbol() { return symbol; }
}
