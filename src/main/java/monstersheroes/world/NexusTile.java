package monstersheroes.world;

// represents a base space at either end of a lane
public class NexusTile extends Tile {
    private final boolean heroNexus;

    public NexusTile(boolean heroNexus) {
        this.heroNexus = heroNexus;
    }

    // indicates whether the space belongs to the heroes
    public boolean isHeroNexus() { return heroNexus; }
    @Override public char getSymbol() { return 'N'; }
    @Override public String getDescription() {
        return heroNexus ? "Heroes' Nexus" : "Monsters' Nexus";
    }
}
