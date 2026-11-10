package monstersheroes.characters;

import monstersheroes.data.GameData;
import monstersheroes.interfaces.Attackable;
import monstersheroes.interfaces.Levelable;
import monstersheroes.interfaces.HeroAbility;
import monstersheroes.inventory.Inventory;
import monstersheroes.items.*;
import monstersheroes.util.CombatBalance;
import java.util.List;
import java.util.Set;

/** Stores a hero's skills, money, equipment, and inventory, and provides the actions shared by all hero classes. */
public abstract class Hero extends Combatant implements Attackable, Levelable {
    /** Names the skills that can improve as a hero gains experience. */
    public enum Skill { STRENGTH, DEXTERITY, AGILITY }

    private double strength, dexterity, agility;
    private double mana, maxMana;
    private int experience;
    private int nextLevelThreshold;
    private int gold = 500;
    private final Inventory<Item> inventory = new Inventory<>();
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    private boolean weaponUsesBothHands;

    protected Hero(String name, GameData.HeroStats stats) {
        super(name, 1, 100);
        strength = stats.strength; dexterity = stats.dexterity; agility = stats.agility;
        mana = stats.mana; maxMana = stats.mana;
        nextLevelThreshold = 10;
    }

    protected abstract Set<Skill> favoredSkills();
    public abstract String getHeroClass();
    /** Returns the unique active abilities available to this hero class. */
    public abstract List<HeroAbility> getSpecialAbilities();
    public double getStrength() { return strength; }
    public double getDexterity() { return dexterity; }
    public double getAgility() { return agility; }
    public double getMP() { return mana; }
    public double getMaxMana() { return maxMana; }
    public int getExperience() { return experience; }
    public int getGold() { return gold; }
    public Inventory<Item> getInventory() { return inventory; }
    public Weapon getEquippedWeapon() { return equippedWeapon; }
    public Armor getEquippedArmor() { return equippedArmor; }
    public boolean isWeaponUsingBothHands() { return weaponUsesBothHands; }

    public void addGold(int amount) {
        if (amount > 0) gold += amount;
    }

    public boolean spendGold(int amount) {
        if (amount < 0 || gold < amount) return false;
        gold -= amount; return true;
    }

    public boolean hasEnoughMana(int amount) {
        return mana >= amount;
    }

    public void spendMana(int amount) {
        mana = Math.max(0, mana - amount);
    }

    public void restoreMana(double amount) {
        if (amount > 0) {
            mana = Math.min(maxMana, mana + amount);
        }
    }

    public void increaseStat(StatType stat, int amount) {
        switch (stat) {
            case HP -> heal(amount);
            case MP -> restoreMana(amount);
            case STRENGTH -> strength += amount;
            case DEXTERITY -> dexterity += amount;
            case AGILITY -> agility += amount;
        }
    }

    public void equipWeapon(Weapon weapon, boolean useBothHands) {
        if (!weapon.canBeUsedBy(this))
            throw new IllegalArgumentException("Only " + weapon.getAllowedClassesDescription() + " heroes can equip this weapon.");
        if (weapon.getLevel() > getLevel()) throw new IllegalArgumentException("Your level is too low for this weapon.");
        if (!weapon.isUsable()) throw new IllegalArgumentException("That weapon is broken.");
        if (weapon.getHandsRequired() == 2 && !useBothHands)
            throw new IllegalArgumentException("This weapon requires both hands.");
        equippedWeapon = weapon;
        weaponUsesBothHands = useBothHands || weapon.getHandsRequired() == 2;
    }

    public void equipArmor(Armor armor) {
        if (armor.getLevel() > getLevel()) throw new IllegalArgumentException("Your level is too low for this armor.");
        if (!armor.isUsable()) throw new IllegalArgumentException("That armor is broken.");
        equippedArmor = armor;
    }

    public void unequip(Item item) {
        if (item == equippedWeapon) {
            equippedWeapon = null;
            weaponUsesBothHands = false;
        }
        if (item == equippedArmor) equippedArmor = null;
    }

    @Override
    public int getAttackDamage() {
        double weaponDamage = equippedWeapon == null || !equippedWeapon.isUsable() ? 0 : equippedWeapon.getDamage();
        if (weaponUsesBothHands && equippedWeapon != null && equippedWeapon.getHandsRequired() == 1)
            weaponDamage *= 1.5;
        return Math.max(1, (int) Math.round((strength + weaponDamage) * CombatBalance.HERO_WEAPON_DAMAGE_MULTIPLIER));
    }

    public double getAgilityDodgeChance() {
        return Math.max(0.0, Math.min(0.75, agility * 0.002));
    }

    @Override
    public void gainExperience(int amount) {
        if (amount <= 0) return;
        experience += amount; // Total experience is never deducted.
        while (experience >= nextLevelThreshold) levelUp();
    }

    private void levelUp() {
        int newLevel = getLevel() + 1;
        setLevel(newLevel);
        strength *= 1.05; dexterity *= 1.05; agility *= 1.05;
        for (Skill skill : favoredSkills()) {
            switch (skill) {
                case STRENGTH -> strength *= 1.05;
                case DEXTERITY -> dexterity *= 1.05;
                case AGILITY -> agility *= 1.05;
            }
        }
        maxMana *= 1.10;
        mana = Math.ceil(mana * 1.10);
        maxHp = getLevel() * 100.0;
        hp = maxHp;
        nextLevelThreshold += getLevel() * 10;
        System.out.printf("\n*** %s reached level %d! ***%n", getName(), getLevel());
    }

    public void reviveAtHalf() {
        if (!isAlive()) {
            hp = (getLevel() * 100.0) / 2.0;
            mana = maxMana / 2.0;
        }
    }

    public void showInfo() {
        System.out.printf("%s the %s | Level %d | HP %.0f | MP %.0f | XP %d | Gold %d%n",
                getName(), getHeroClass(), getLevel(), hp, mana, experience, gold);
        System.out.printf("   STR %.1f | DEX %.1f | AGI %.1f | Weapon: %s | Armor: %s%n",
                strength, dexterity, agility,
                equippedWeapon == null ? "None" : equippedWeapon.getName(),
                equippedArmor == null ? "None" : equippedArmor.getName());
    }

    @Override public String getCombatSummary() {
        return String.format("%s the %s (Lvl %d) HP %.0f / MP %.0f", getName(), getHeroClass(), getLevel(), hp, mana);
    }
}
