package monstersheroes.characters;

import monstersheroes.battle.abilities.SpecialAttackAbility;
import monstersheroes.battle.abilities.SpecialAttackAbility.DamageStyle;
import monstersheroes.battle.abilities.SpecialAttackAbility.Debuff;
import monstersheroes.data.GameData;
import monstersheroes.interfaces.HeroAbility;
import java.util.List;
import java.util.Set;

/** Represents a Warrior, who relies mainly on strength and agility in battle. */
public class Warrior extends Hero {
    public Warrior(String name, GameData.HeroStats stats) { super(name, stats); }
    @Override protected Set<Skill> favoredSkills() { return Set.of(Skill.STRENGTH, Skill.AGILITY); }
    @Override public String getHeroClass() { return "Warrior"; }

    @Override
    public List<HeroAbility> getSpecialAbilities() {
        return List.of(
            new SpecialAttackAbility("Power Strike", "A crushing single-target weapon attack (1.8x physical damage).", 8,
                    1.8, DamageStyle.PHYSICAL, false, Debuff.NONE, 0),
            new SpecialAttackAbility("Whirlwind", "Spin through the battlefield, striking every living monster (0.8x damage).", 14,
                    0.8, DamageStyle.PHYSICAL, true, Debuff.NONE, 0),
            new SpecialAttackAbility("Armor Breaker", "A heavy blow that deals 1.35x damage and reduces the target's defense by 25%.", 11,
                    1.35, DamageStyle.PHYSICAL, false, Debuff.REDUCE_DEFENSE, 0.25)
        );
    }
}
