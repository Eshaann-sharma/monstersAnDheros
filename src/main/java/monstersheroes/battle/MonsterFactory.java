package monstersheroes.battle;

import monstersheroes.characters.*;
import monstersheroes.data.GameData;
import monstersheroes.party.Party;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Creates a group of monsters based on the level and size of the heroes' party. */
public class MonsterFactory {
    private final GameData data;
    private final Random random;
    public MonsterFactory(GameData data, Random random) { this.data = data; this.random = random; }

    public List<Monster> createFor(Party party) {
        return createFor(party, 0);
    }

    public List<Monster> createFor(Party party, int depth) {
        int level = Math.max(party.highestLevel(), depth + 1);
        List<Monster> monsters = new ArrayList<>();
        String[] roster = switch (depth) {
            case 0 -> new String[]{"Iron Exoskeleton", "Undead Zombie", "Skull Eater"};
            case 1 -> new String[]{"Ghost Face", "Wailing Spirit"};
            default -> new String[]{"Ember Dragon", "Ice Dragon", "Old Dragon"};
        };
        for (int i = 1; i <= party.getHeroes().size(); i++) {
            String name = roster[random.nextInt(roster.length)] + " " + i;
            monsters.add(createCommon(name, level, depth));
        }
        return monsters;
    }

    public Monster createBoss(Party party, int depth) {
        int level = Math.max(party.highestLevel(), depth + 1);
        return switch (depth) {
            case 0 -> new Exoskeleton("Skeleton King", level, data.getMonsterStats("exoskeleton"), 5.0, 2.5);
            case 1 -> new Spirit("Gengar", level, data.getMonsterStats("spirit"), 5.0, 2.5);
            default -> new Dragon("Chrysophylax the Crystal Dragon", level, data.getMonsterStats("dragon"), 6.0, 3.0);
        };
    }

    private Monster createCommon(String name, int level, int depth) {
        return switch (depth) {
            case 0 -> new Exoskeleton(name, level, data.getMonsterStats("exoskeleton"));
            case 1 -> new Spirit(name, level, data.getMonsterStats("spirit"));
            default -> new Dragon(name, level, data.getMonsterStats("dragon"));
        };
    }
}
