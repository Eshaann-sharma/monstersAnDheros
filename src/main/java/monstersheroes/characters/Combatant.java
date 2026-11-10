package monstersheroes.characters;

/** Stores the basic details shared by every hero and monster, such as name, level, and health. */
public abstract class Combatant {
    private final String name;
    private int level;
    protected double hp;
<<<<<<< HEAD
    protected double maxHp;
=======
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2

    protected Combatant(String name, int level, double hp) {
        if (name == null || name.isBlank() || level < 1 || hp <= 0)
            throw new IllegalArgumentException("Invalid combatant attributes.");
<<<<<<< HEAD
        this.name = name;
        this.level = level;
        this.hp = hp;
        this.maxHp = hp;
    }
    public double getMaxHP() {
        return maxHp;
    }
    public String getName() {
        return name;
    }
    public int getLevel() {
        return level;
    }
    protected void setLevel(int level) {
        this.level = level;
    }
    public double getHP() {
        return hp;
    }
    public boolean isAlive() {
        return hp > 0;
    }
    public void takeDamage(double damage) {
        hp = Math.max(0, hp - Math.max(0, damage));
    }
    public void heal(double amount) {
        if (amount > 0) {
            hp = Math.min(maxHp, hp + amount);
        }
    }

=======
        this.name = name; this.level = level; this.hp = hp;
    }
    public String getName() { return name; }
    public int getLevel() { return level; }
    protected void setLevel(int level) { this.level = level; }
    public double getHP() { return hp; }
    public boolean isAlive() { return hp > 0; }
    public void takeDamage(double damage) { hp = Math.max(0, hp - Math.max(0, damage)); }
    public void heal(double amount) { if (amount > 0) hp += amount; }
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
    public abstract String getCombatSummary();
}
