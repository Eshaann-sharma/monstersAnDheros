package monstersheroes.party;

import monstersheroes.characters.Hero;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Keeps track of the heroes travelling and fighting together. */
public class Party {
    public static final int MAX_SIZE = 3;
    private final List<Hero> heroes = new ArrayList<>();
    public void addHero(Hero hero) {
        if (heroes.size() >= MAX_SIZE) throw new IllegalStateException("A party can contain at most 3 heroes.");
        heroes.add(hero);
    }
    public List<Hero> getHeroes() { return Collections.unmodifiableList(heroes); }
    public List<Hero> getLivingHeroes() { return heroes.stream().filter(Hero::isAlive).toList(); }
    public boolean hasLivingHeroes() { return heroes.stream().anyMatch(Hero::isAlive); }
    public int highestLevel() { return heroes.stream().mapToInt(Hero::getLevel).max().orElse(1); }
}
