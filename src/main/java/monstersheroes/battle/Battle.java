package monstersheroes.battle;

import monstersheroes.characters.Hero;
import monstersheroes.characters.Monster;
import monstersheroes.battle.abilities.HealingAbility;
import monstersheroes.battle.abilities.SpecialAttackAbility;
import monstersheroes.battle.abilities.SpecialAttackAbility.DamageStyle;
import monstersheroes.battle.abilities.SpecialAttackAbility.Debuff;
import monstersheroes.interfaces.HeroAbility;
import monstersheroes.dodge.NumberGuessDodge;
import monstersheroes.interfaces.DodgeRule;
import monstersheroes.items.*;
import monstersheroes.party.Party;
import monstersheroes.util.InputHandler;
import monstersheroes.util.AsciiArt;
import monstersheroes.util.CombatBalance;
import java.util.*;
import java.util.stream.Collectors;

/** Runs the battle rounds and keeps track of the heroes, monsters, and their actions. */
public class Battle {
    private final Party party;
    private final List<Monster> monsters;
    private final InputHandler input;
    private final Random random;
    private final DodgeRule dodgeRule = new NumberGuessDodge();
    private int roundNumber = 1;
    private boolean quitRequested;

    public Battle(Party party, List<Monster> monsters, InputHandler input, Random random) {
        this.party = party; this.monsters = new ArrayList<>(monsters);
        this.input = input; this.random = random;
    }

    public boolean run() {
        System.out.println("\n========================================");
        System.out.println("             BATTLE START!");
        System.out.println("========================================");
        AsciiArt.printBattleStart();
        for (Monster monster : monsters) {
            System.out.println("\nA " + monster.getMonsterType() + " appears: " + monster.getName());
<<<<<<< HEAD
            if (monster.getName().equals("Skeleton King") || monster.getName().equals("Gengar")
                    || monster.getName().equals("Chrysophylax the Crystal Dragon"))
                AsciiArt.printBoss(monster.getName());
            else AsciiArt.printMonster(monster.getMonsterType());
=======
            AsciiArt.printMonster(monster.getMonsterType());
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
        }
        showMonsters();

        while (party.hasLivingHeroes() && hasLivingMonsters() && !quitRequested) {
            System.out.println("\n========== ROUND " + roundNumber + " ==========");
            System.out.println("--- Heroes' turn ---");
            for (Hero hero : party.getHeroes()) {
                if (hero.isAlive() && hasLivingMonsters() && !quitRequested) takeHeroTurn(hero);
            }
            if (quitRequested) return false;
            if (!hasLivingMonsters()) break;

            System.out.println("\n--- Monsters' turn ---");
            takeMonsterTurn();
            if (!party.hasLivingHeroes()) break;

<<<<<<< HEAD
=======
            for (Hero hero : party.getLivingHeroes()) hero.recoverAtRoundEnd();
            System.out.println("\nSurviving heroes recovered 10% of their current HP and MP.");
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
            roundNumber++;
        }

        if (quitRequested) return false;
        if (!party.hasLivingHeroes()) {
            return false; // GameController prints the game-over artwork after the game loop ends.
        }
        finishVictory();
        return true;
    }

    private void takeHeroTurn(Hero hero) {
        boolean actionCompleted = false;
        while (!actionCompleted && hero.isAlive() && hasLivingMonsters()) {
            System.out.println("\n" + hero.getCombatSummary());
            System.out.println("1. Attack   2. Cast spell   3. Use potion   4. Equip item");
            System.out.println("5. Class ability   6. View battle info   7. Skip turn   8. Quit game");
            int choice = input.readInt("Choose an action: ", 1, 8);
            switch (choice) {
                case 1 -> {
                    Monster target = chooseMonster();
<<<<<<< HEAD
                    if (target != null) { new AttackAction(target).execute(this, hero);
                        actionCompleted = true; }
=======
                    if (target != null) { new AttackAction(target).execute(this, hero); actionCompleted = true; }
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
                }
                case 2 -> {
                    Spell spell = chooseSpell(hero);
                    if (spell != null) {
                        Monster target = chooseMonster();
<<<<<<< HEAD
                        if (target != null) {
                            new CastSpellAction(spell, target).execute(this, hero);
                            actionCompleted = true; }
=======
                        if (target != null) { new CastSpellAction(spell, target).execute(this, hero); actionCompleted = true; }
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
                    }
                }
                case 3 -> {
                    Potion potion = choosePotion(hero);
<<<<<<< HEAD
                    if (potion != null) {
                        new UsePotionAction(potion).execute(this, hero);
                        actionCompleted = true; }
=======
                    if (potion != null) { new UsePotionAction(potion).execute(this, hero); actionCompleted = true; }
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
                }
                case 4 -> {
                    Item item = chooseEquipable(hero);
                    if (item != null) {
                        boolean bothHands = item instanceof Weapon weapon && weapon.getHandsRequired() == 2;
                        if (item instanceof Weapon weapon && weapon.getHandsRequired() == 1)
                            bothHands = input.readYesNo("Use both hands to increase this weapon's damage?");
                        new EquipAction(item, bothHands).execute(this, hero);
                        actionCompleted = true;
                    }
                }
                case 5 -> {
                    HeroAbility ability = chooseSpecialAbility(hero);
                    if (ability != null) {
                        new UseHeroAbilityAction(ability).execute(this, hero);
                        actionCompleted = true;
                    }
                }
                case 6 -> { showBattleInfo(); }
                case 7 -> { System.out.println(hero.getName() + " skips this turn."); actionCompleted = true; }
                case 8 -> { quitRequested = true; actionCompleted = true; System.out.println("Quitting after leaving the battle..."); }
            }
        }
    }

    private void takeMonsterTurn() {
        for (Monster monster : monsters) {
            if (!monster.isAlive() || !party.hasLivingHeroes()) continue;
            List<Hero> targets = party.getLivingHeroes();
            Hero target = targets.get(random.nextInt(targets.size()));
            System.out.printf("\n%s attacks %s!%n", monster.getName(), target.getName());
            int guess = input.readInt("Guess the monster's number (1-7): ", 1, 7);
            int monsterNumber = random.nextInt(7) + 1;
            System.out.println("Your guess: " + guess + " | Monster's number: " + monsterNumber);

            if (dodgeRule.dodge(guess, monsterNumber)) {
                AsciiArt.printPerfectDodge();
                System.out.println(target.getName() + " avoids the attack completely!");
                continue;
            }

            if (random.nextDouble() < target.getAgilityDodgeChance()) {
                System.out.println("Your guess missed, but " + target.getName() + " dodged using agility!");
                continue;
            }

            int damage = monster.getAttackDamage();
            boolean criticalHit = random.nextDouble() < CombatBalance.CRITICAL_HIT_CHANCE;
            if (criticalHit) {
                damage = (int) Math.round(damage * CombatBalance.CRITICAL_HIT_MULTIPLIER);
                AsciiArt.printCriticalHit();
                System.out.println(monster.getName() + " lands a devastating critical hit!");
            }
            Armor armor = target.getEquippedArmor();
            if (armor != null && armor.isUsable()) {
                damage = Math.max(1, damage - armor.getDamageReduction());
                armor.consumeUse();
                if (!armor.isUsable()) System.out.println(armor.getName() + " broke!");
            }
            target.takeDamage(damage);
            System.out.printf("%s takes %d damage%s. Remaining HP: %.0f%n", target.getName(), damage,
                    criticalHit ? " (critical hit)" : "", target.getHP());
            if (!target.isAlive()) System.out.println(target.getName() + " has fainted and cannot act for the rest of this battle!");
        }
    }

    /** Resolves one of the class-specific active attacks. */
    public void performSpecialAttack(Hero hero, SpecialAttackAbility ability) {
        if (ability == null) return;
        if (!hero.hasEnoughMana(ability.getManaCost())) {
            System.out.printf("%s needs %d MP for %s, but has only %.0f MP.%n",
                    hero.getName(), ability.getManaCost(), ability.getName(), hero.getMP());
            return;
        }

        List<Monster> targets;
        if (ability.isAreaAttack()) {
            targets = livingMonsters();
        } else {
            Monster target = chooseMonster();
            if (target == null) return;
            targets = List.of(target);
        }
        if (targets.isEmpty()) {
            System.out.println("There are no monsters left to target.");
            return;
        }

        hero.spendMana(ability.getManaCost());
        System.out.printf("%n%s uses %s! (-%d MP)%n", hero.getName(), ability.getName(), ability.getManaCost());
        int baseDamage = switch (ability.getDamageStyle()) {
            case PHYSICAL -> hero.getAttackDamage();
            case ARCANE -> (int) Math.round((hero.getDexterity() * 0.25 + hero.getLevel() * 10)
                    * CombatBalance.SPELL_DAMAGE_MULTIPLIER);
            case HOLY -> (int) Math.round(((hero.getStrength() + hero.getDexterity()) * 0.125
                    + hero.getLevel() * 10) * CombatBalance.SPELL_DAMAGE_MULTIPLIER);
        };

        for (Monster target : targets) {
            if (!target.isAlive()) continue;
            if (target.dodges(random)) {
                System.out.println(target.getName() + " dodged " + ability.getName() + "!");
                continue;
            }

            applyAbilityDebuff(target, ability);
            int damage = Math.max(1, (int) Math.round(baseDamage * ability.getDamageMultiplier()));
            boolean criticalHit = random.nextDouble() < CombatBalance.CRITICAL_HIT_CHANCE;
            if (criticalHit) {
                damage = (int) Math.round(damage * CombatBalance.CRITICAL_HIT_MULTIPLIER);
                AsciiArt.printCriticalHit();
            }
            int appliedDamage = target.takeCombatDamage(damage);
            System.out.printf("%s hits %s for %d damage%s. %s HP: %.0f%n",
                    ability.getName(), target.getName(), appliedDamage,
                    criticalHit ? " (CRITICAL!)" : "", target.getName(), target.getHP());
            if (!target.isAlive()) System.out.println(target.getName() + " has been defeated!");
        }
    }

    private void applyAbilityDebuff(Monster target, SpecialAttackAbility ability) {
        double fraction = ability.getDebuffFraction();
        if (fraction <= 0) return;
        switch (ability.getDebuff()) {
            case REDUCE_DEFENSE -> {
                target.reduceDefense(fraction);
                System.out.printf("%s weakens %s's defense by %.0f%%!%n", ability.getName(), target.getName(), fraction * 100);
            }
            case REDUCE_MONSTER_ATTACK -> {
                target.reduceAttackDamage(fraction);
                System.out.printf("%s weakens %s's attack damage by %.0f%%!%n", ability.getName(), target.getName(), fraction * 100);
            }
            case REDUCE_MONSTER_DODGE -> {
                target.reduceDodgeChance(fraction);
                System.out.printf("%s weakens %s's dodge chance by %.0f%%!%n", ability.getName(), target.getName(), fraction * 100);
            }
            case NONE -> { }
        }
    }

    /** Sorcerer support action: heal one living party member by a fixed amount. */
    public void performHealing(Hero caster, HealingAbility ability) {
        if (ability == null) return;
        if (!caster.hasEnoughMana(ability.getManaCost())) {
            System.out.printf("%s needs %d MP for %s, but has only %.0f MP.%n",
                    caster.getName(), ability.getManaCost(), ability.getName(), caster.getMP());
            return;
        }
        List<Hero> availableTargets = party.getLivingHeroes();
        if (availableTargets.isEmpty()) {
            System.out.println("There are no living party members to heal.");
            return;
        }
        System.out.println("Choose a party member to heal:");
        for (int i = 0; i < availableTargets.size(); i++) {
            Hero target = availableTargets.get(i);
            System.out.printf("%d. %s | HP %.0f%n", i + 1, target.getName(), target.getHP());
        }
        int choice = input.readInt("Target (0 to cancel): ", 0, availableTargets.size());
        if (choice == 0) return;

        Hero target = availableTargets.get(choice - 1);
        caster.spendMana(ability.getManaCost());
        double previousHp = target.getHP();
        target.heal(ability.getHealingAmount());
        AsciiArt.printHealingLight();
        System.out.printf("%s casts %s on %s! %s recovers exactly %d HP (%.0f -> %.0f HP).%n",
                caster.getName(), ability.getName(), target.getName(), target.getName(),
                ability.getHealingAmount(), previousHp, target.getHP());
        System.out.println("Healing is not capped, consistent with the game's HP rules.");
    }

    public void performAttack(Hero hero, Monster target) {
        if (!target.isAlive()) { System.out.println("That monster has already been defeated."); return; }
        int damage = hero.getAttackDamage();
        Weapon weapon = hero.getEquippedWeapon();
        if (weapon != null && weapon.isUsable()) {
            weapon.consumeUse();
            if (!weapon.isUsable()) System.out.println(weapon.getName() + " broke!");
        }
        if (target.dodges(random)) {
            System.out.println(target.getName() + " dodged the attack!");
            return;
        }
        boolean criticalHit = random.nextDouble() < CombatBalance.CRITICAL_HIT_CHANCE;
        if (criticalHit) {
            damage = (int) Math.round(damage * CombatBalance.CRITICAL_HIT_MULTIPLIER);
            AsciiArt.printCriticalHit();
        }
        int appliedDamage = target.takeCombatDamage(damage);
        System.out.printf("%s attacks %s for %d damage%s. %s HP: %.0f%n",
                hero.getName(), target.getName(), appliedDamage,
                criticalHit ? " (CRITICAL!)" : "", target.getName(), target.getHP());
        if (!target.isAlive()) System.out.println(target.getName() + " has been defeated!");
    }

    public void performSpell(Hero hero, Monster target, Spell spell) {
        if (!hero.getInventory().contains(spell) || spell.isConsumed()) {
            System.out.println("That spell is no longer available."); return;
        }
        if (!hero.hasEnoughMana(spell.getManaCost())) {
            System.out.println("Not enough mana to cast " + spell.getName() + "."); return;
        }
        hero.spendMana(spell.getManaCost());
        spell.consume();
        hero.getInventory().removeItem(spell);
        if (target.dodges(random)) {
            System.out.println(target.getName() + " dodged the spell! Mana was still spent.");
            return;
        }
        int damage = spell.calculateDamage(hero);
        boolean criticalHit = random.nextDouble() < CombatBalance.CRITICAL_HIT_CHANCE;
        if (criticalHit) {
            damage = (int) Math.round(damage * CombatBalance.CRITICAL_HIT_MULTIPLIER);
            AsciiArt.printCriticalHit();
        }
        int appliedDamage = target.takeCombatDamage(damage);
        spell.applyEffect(target);
        System.out.printf("%s casts %s on %s for %d damage%s!%n",
                hero.getName(), spell.getName(), target.getName(), appliedDamage,
                criticalHit ? " (CRITICAL!)" : "");
        if (!target.isAlive()) System.out.println(target.getName() + " has been defeated!");
    }

    public void performPotion(Hero hero, Potion potion) {
<<<<<<< HEAD

        if (!hero.getInventory().contains(potion) || potion.isConsumed()) {
            System.out.println("That potion is no longer available.");
            return;
        }

        int restored = potion.getActualEffectAmount(hero);

        potion.applyTo(hero);
        hero.getInventory().removeItem(potion);

        System.out.printf(
                "%s uses %s. %s increases by %d.%n",
                hero.getName(),
                potion.getName(),
                potion.getStatType(),
                restored
        );
=======
        if (!hero.getInventory().contains(potion) || potion.isConsumed()) {
            System.out.println("That potion is no longer available."); return;
        }
        potion.applyTo(hero);
        hero.getInventory().removeItem(potion);
        System.out.printf("%s uses %s. %s increases by %d.%n",
                hero.getName(), potion.getName(), potion.getStatType(), potion.getEffectAmount());
>>>>>>> 1f5ae80801d04726eca9eb129ffed4f68970c7e2
    }

    public void performEquip(Hero hero, Item item, boolean bothHands) {
        if (!hero.getInventory().contains(item)) { System.out.println("That item is not in the inventory."); return; }
        try {
            if (item instanceof Weapon weapon) hero.equipWeapon(weapon, bothHands);
            else if (item instanceof Armor armor) hero.equipArmor(armor);
            else { System.out.println("That item cannot be equipped."); return; }
            System.out.println(hero.getName() + " equipped " + item.getName() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private HeroAbility chooseSpecialAbility(Hero hero) {
        List<HeroAbility> abilities = hero.getSpecialAbilities();
        if (abilities.isEmpty()) {
            System.out.println("This hero has no special abilities.");
            return null;
        }
        System.out.println("\n" + hero.getHeroClass() + " abilities:");
        for (int i = 0; i < abilities.size(); i++) {
            HeroAbility ability = abilities.get(i);
            System.out.printf("%d. %s | %d MP | %s%n", i + 1, ability.getName(),
                    ability.getManaCost(), ability.getDescription());
        }
        int choice = input.readInt("Choose an ability (0 to cancel): ", 0, abilities.size());
        return choice == 0 ? null : abilities.get(choice - 1);
    }

    private Monster chooseMonster() {
        List<Monster> living = livingMonsters();
        if (living.isEmpty()) return null;
        System.out.println("Choose a target:");
        for (int i = 0; i < living.size(); i++) System.out.println((i + 1) + ". " + living.get(i).getCombatSummary());
        return living.get(input.readInt("Target: ", 1, living.size()) - 1);
    }

    private Spell chooseSpell(Hero hero) {
        List<Spell> spells = hero.getInventory().getItems().stream()
                .filter(Spell.class::isInstance).map(Spell.class::cast).filter(s -> !s.isConsumed()).collect(Collectors.toList());
        if (spells.isEmpty()) { System.out.println("You have no spells in your inventory."); return null; }
        System.out.println("Available spells:");
        for (int i = 0; i < spells.size(); i++) System.out.println((i + 1) + ". " + spells.get(i));
        int choice = input.readInt("Spell (0 to cancel): ", 0, spells.size());
        return choice == 0 ? null : spells.get(choice - 1);
    }

    private Potion choosePotion(Hero hero) {
        List<Potion> potions = hero.getInventory().getItems().stream()
                .filter(Potion.class::isInstance).map(Potion.class::cast).filter(p -> !p.isConsumed()).collect(Collectors.toList());
        if (potions.isEmpty()) { System.out.println("You have no potions in your inventory."); return null; }
        System.out.println("Available potions:");
        for (int i = 0; i < potions.size(); i++) System.out.println((i + 1) + ". " + potions.get(i));
        int choice = input.readInt("Potion (0 to cancel): ", 0, potions.size());
        return choice == 0 ? null : potions.get(choice - 1);
    }

    private Item chooseEquipable(Hero hero) {
        List<Item> items = hero.getInventory().getItems().stream()
                .filter(item -> item instanceof Weapon || item instanceof Armor).collect(Collectors.toList());
        if (items.isEmpty()) { System.out.println("No weapons or armor available to equip."); return null; }
        System.out.println("Equipment:");
        for (int i = 0; i < items.size(); i++) System.out.println((i + 1) + ". " + items.get(i));
        int choice = input.readInt("Item (0 to cancel): ", 0, items.size());
        return choice == 0 ? null : items.get(choice - 1);
    }

    private void showBattleInfo() {
        System.out.println("\n-- Heroes --");
        party.getHeroes().forEach(h -> System.out.println(h.getCombatSummary()));
        showMonsters();
    }

    private void showMonsters() {
        System.out.println("\n-- Monsters --");
        for (Monster monster : monsters) System.out.println(monster.getCombatSummary() + (monster.isAlive() ? "" : " [DEFEATED]"));
    }

    private List<Monster> livingMonsters() { return monsters.stream().filter(Monster::isAlive).toList(); }
    private boolean hasLivingMonsters() { return monsters.stream().anyMatch(Monster::isAlive); }

    private void finishVictory() {
        int experienceReward = monsters.size() * 2;
        int goldReward = monsters.stream().mapToInt(m -> m.getLevel() * 100).sum();
        AsciiArt.printVictory();
        for (Hero hero : party.getHeroes()) {
            if (hero.isAlive()) {
                hero.gainExperience(experienceReward);
                hero.addGold(goldReward);
                System.out.printf("%s receives %d XP and %d gold.%n", hero.getName(), experienceReward, goldReward);
            } else {
                hero.reviveAtHalf();
                System.out.printf("%s is revived with half HP and MP, but receives no rewards.%n", hero.getName());
            }
        }
    }
}
