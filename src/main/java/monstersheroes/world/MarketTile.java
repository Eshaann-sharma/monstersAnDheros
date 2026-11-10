package monstersheroes.world;

import monstersheroes.market.Market;

/** Represents a map space that contains a market. */
public class MarketTile extends Tile {
    private final Market market;
    public MarketTile(Market market) { this.market = market; }
    public Market getMarket() { return market; }
    @Override public char getSymbol() { return 'M'; }
    @Override public String getDescription() { return "Market"; }
}
