package monstersheroes.items;

/** Stores the details shared by market items, such as name, price, level, and uses. */
public abstract class Item {
    private final String name;
    private final int price;
    private final int level;
    private final int maxUses;
    private int remainingUses;

    protected Item(String name, int price, int level, int uses) {
        if (name == null || name.isBlank() || price < 0 || level < 1 || uses < 1)
            throw new IllegalArgumentException("Invalid item attributes.");
        this.name = name; this.price = price; this.level = level;
        this.maxUses = uses; this.remainingUses = uses;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getLevel() { return level; }
    public int getMaxUses() { return maxUses; }
    public int getRemainingUses() { return remainingUses; }
    public boolean isUsable() { return remainingUses > 0; }
    public void consumeUse() { if (remainingUses > 0) remainingUses--; }
    public void repair() { remainingUses = maxUses; }
    public abstract String getDescription();

    @Override
    public String toString() {
        return String.format("%s (Lvl %d, %d gold, uses %d/%d) - %s",
                name, level, price, remainingUses, maxUses, getDescription());
    }
}
