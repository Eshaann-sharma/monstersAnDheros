package monstersheroes.characters;

import monstersheroes.battle.abilities.HealingAbility;
import monstersheroes.battle.abilities.SpecialAttackAbility;
import monstersheroes.battle.abilities.SpecialAttackAbility.DamageStyle;
import monstersheroes.battle.abilities.SpecialAttackAbility.Debuff;
import monstersheroes.data.GameData;
import monstersheroes.interfaces.HeroAbility;
import java.util.List;
import java.util.Set;

/** Represents a Sorcerer, who specializes in magic and can heal another hero during battle. */
public class Sorcerer extends Hero {
    public Sorcerer(String name, GameData.HeroStats stats) { super(name, stats); }
    @Override protected Set<Skill> favoredSkills() { return Set.of(Skill.DEXTERITY, Skill.AGILITY); }
    @Override public String getHeroClass() { return "Sorcerer"; }

    @Override
    public List<HeroAbility> getSpecialAbilities() {
        return List.of(
            new SpecialAttackAbility("Arcane Bolt", "Focus arcane energy into one enemy (1.55x arcane damage).", 10,
                    1.55, DamageStyle.ARCANE, false, Debuff.NONE, 0),
            new SpecialAttackAbility("Frost Nova", "Blast all monsters with frost and weaken their attacks by 20% (0.72x arcane damage).", 18,
                    0.72, DamageStyle.ARCANE, true, Debuff.REDUCE_MONSTER_ATTACK, 0.20),
            new HealingAbility("Healing Light", "Restore exactly 10 HP to one living member of the party.", 8, 10)
        );
    }
}
