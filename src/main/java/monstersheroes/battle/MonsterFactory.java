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
        int level = party.highestLevel();
        List<Monster> monsters = new ArrayList<>();
        for (int i = 1; i <= party.getHeroes().size(); i++) {
            int type = random.nextInt(3);
            String suffix = " " + i;
            switch (type) {
                case 0 -> monsters.add(new Dragon("Ember Dragon" + suffix, level, data.getMonsterStats("dragon")));
                case 1 -> monsters.add(new Exoskeleton("Iron Exoskeleton" + suffix, level, data.getMonsterStats("exoskeleton")));
                default -> monsters.add(new Spirit("Wailing Spirit" + suffix, level, data.getMonsterStats("spirit")));
            }
        }
        return monsters;
    }
}
