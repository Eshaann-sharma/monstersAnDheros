package monstersheroes.characters;

/** Stores the basic details shared by every hero and monster, such as name, level, and health. */
public abstract class Combatant {
    private final String name;
    private int level;
    protected double hp;
    protected double maxHp;

    protected Combatant(String name, int level, double hp) {
        if (name == null || name.isBlank() || level < 1 || hp <= 0)
            throw new IllegalArgumentException("Invalid combatant attributes.");
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

    public abstract String getCombatSummary();
}
