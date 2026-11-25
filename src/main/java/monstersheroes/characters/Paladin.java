package monstersheroes.characters;

import monstersheroes.battle.abilities.SpecialAttackAbility;
import monstersheroes.battle.abilities.SpecialAttackAbility.DamageStyle;
import monstersheroes.battle.abilities.SpecialAttackAbility.Debuff;
import monstersheroes.data.GameData;
import monstersheroes.interfaces.HeroAbility;
import java.util.List;
import java.util.Set;

/** Represents a Paladin, whose strongest skills are strength and dexterity. */
public class Paladin extends Hero {
    public Paladin(String name, GameData.HeroStats stats) { super(name, stats); }
    @Override protected Set<Skill> favoredSkills() { return Set.of(Skill.STRENGTH, Skill.DEXTERITY); }
    @Override public String getHeroClass() { return "Paladin"; }

    @Override
    public List<HeroAbility> getSpecialAbilities() {
        return List.of(
            new SpecialAttackAbility("Holy Smite", "Strike one enemy with holy power (1.6x holy damage).", 10,
                    1.6, DamageStyle.HOLY, false, Debuff.NONE, 0),
            new SpecialAttackAbility("Radiant Wave", "Release a wave of light that hits every living monster (0.7x holy damage).", 16,
                    0.7, DamageStyle.HOLY, true, Debuff.NONE, 0),
            new SpecialAttackAbility("Judgement", "Deliver a powerful holy strike (2.0x damage) that weakens the target's dodge by 35%.", 20,
                    2.0, DamageStyle.HOLY, false, Debuff.REDUCE_MONSTER_DODGE, 0.35)
        );
    }
}
