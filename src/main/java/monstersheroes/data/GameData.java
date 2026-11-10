package monstersheroes.data;

import monstersheroes.items.*;
import java.util.*;

/** Keeps the default hero, monster, and item details loaded when the game starts. */
public class GameData {
    /** Holds the starting skills and health values for a hero type. */
    public static class HeroStats {
        public final double strength, dexterity, agility, mana;
        public HeroStats(double strength, double dexterity, double agility, double mana) {
            this.strength = strength; this.dexterity = dexterity; this.agility = agility; this.mana = mana;
        }
    }

    /** Holds the starting damage, defense, and dodge values for a monster type. */
    public static class MonsterStats {
        public final int damage, defense, dodge;
        public MonsterStats(int damage, int defense, int dodge) {
            this.damage = damage; this.defense = defense; this.dodge = dodge;
        }
    }

    /** Holds the details needed to create an item from the data files. */
    public static class ItemDefinition {
        public final String type, name, extra;
        public final int price, level, value1, value2, uses;
        public ItemDefinition(String type, String name, int price, int level,
                              int value1, int value2, int uses, String extra) {
            this.type = type.toUpperCase(Locale.ROOT); this.name = name;
            this.price = price; this.level = level; this.value1 = value1;
            this.value2 = value2; this.uses = uses; this.extra = extra;
        }

        public Item createItem() {
            return switch (type) {
                case "WEAPON" -> new Weapon(name, price, level, value1, value2, uses,
                        extra.equals("-") ? java.util.Set.of() : java.util.Arrays.stream(extra.split(",")).map(String::trim).collect(java.util.stream.Collectors.toSet()));
                case "ARMOR" -> new Armor(name, price, level, value1, uses);
                case "POTION" -> new Potion(name, price, level, value1, StatType.valueOf(extra.toUpperCase(Locale.ROOT)));
                case "SPELL" -> switch (extra.toUpperCase(Locale.ROOT)) {
                    case "ICE" -> new IceSpell(name, price, level, value1, value2);
                    case "FIRE" -> new FireSpell(name, price, level, value1, value2);
                    default -> new LightningSpell(name, price, level, value1, value2);
                };
                default -> throw new IllegalArgumentException("Unknown item type: " + type);
            };
        }
    }

    private final Map<String, HeroStats> heroStats = new HashMap<>();
    private final Map<String, MonsterStats> monsterStats = new HashMap<>();
    private final List<ItemDefinition> itemDefinitions = new ArrayList<>();

    public void addHeroStats(String name, HeroStats stats) { heroStats.put(name.toLowerCase(Locale.ROOT), stats); }
    public void addMonsterStats(String name, MonsterStats stats) { monsterStats.put(name.toLowerCase(Locale.ROOT), stats); }
    public void addItemDefinition(ItemDefinition item) { itemDefinitions.add(item); }

    public HeroStats getHeroStats(String name) {
        HeroStats stats = heroStats.get(name.toLowerCase(Locale.ROOT));
        if (stats == null) throw new IllegalArgumentException("Missing hero data for " + name);
        return stats;
    }
    public MonsterStats getMonsterStats(String name) {
        MonsterStats stats = monsterStats.get(name.toLowerCase(Locale.ROOT));
        if (stats == null) throw new IllegalArgumentException("Missing monster data for " + name);
        return stats;
    }
    public List<ItemDefinition> getItemDefinitions() { return Collections.unmodifiableList(itemDefinitions); }
}
